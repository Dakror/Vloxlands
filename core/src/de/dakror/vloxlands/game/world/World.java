package de.dakror.vloxlands.game.world;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.badlogic.gdx.utils.Pool;

import de.dakror.vloxlands.ai.AStar;
import de.dakror.vloxlands.ai.Path;
import de.dakror.vloxlands.ai.Path.PathBundle;
import de.dakror.vloxlands.game.Query;
import de.dakror.vloxlands.game.Query.Queryable;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.entity.structure.StructureNode.NodeType;
import de.dakror.vloxlands.render.Mesher;
import de.dakror.vloxlands.util.Tickable;

/**
 * @author Dakror
 */
public class World implements RenderableProvider, Tickable, Queryable
{
	public static final Color SELECTION = Color.valueOf("ff9900");
	
	public static final int MAXHEIGHT = 512;
	
	static Material opaque, transp, highlight;
	
	Island[] islands;
	
	int width, depth;
	
	public int visibleChunks, chunks, visibleEntities, totalEntities;
	
	public static Mesh chunkCube, blockCube;
	public static final float gap = 0.01f;
	
	Array<Entity> entities = new Array<Entity>();
	
	public World(int width, int depth)
	{
		this.width = width;
		this.depth = depth;
		
		islands = new Island[width * depth];
		
		Texture tex = new Texture(Gdx.files.internal("img/voxelTextures.png"));
		Texture tex2 = new Texture(Gdx.files.internal("img/transparent.png"));
		
		opaque = new Material(TextureAttribute.createDiffuse(tex));
		transp = new Material(TextureAttribute.createDiffuse(tex), new BlendingAttribute());
		highlight = new Material(TextureAttribute.createDiffuse(tex2), ColorAttribute.createDiffuse(SELECTION));
		
		chunkCube = Mesher.genCubeWireframe(Chunk.SIZE + gap);
		blockCube = Mesher.genCubeWireframe(1 + gap);
	}
	
	/**
	 * @param x in index space
	 * @param y in pos space
	 * @param z in index space
	 */
	public void addIsland(int x, int z, Island island)
	{
		islands[z * width + x] = island;
		chunks += Island.CHUNKS * Island.CHUNKS * Island.CHUNKS;
	}
	
	public void update()
	{
		for (Iterator<Entity> iter = entities.iterator(); iter.hasNext();)
		{
			Entity e = iter.next();
			e.update();
		}
	}
	
	@Override
	public void tick(int tick)
	{
		for (Island island : islands)
			if (island != null) island.tick(tick);
		
		for (Iterator<Entity> iter = entities.iterator(); iter.hasNext();)
		{
			Entity e = iter.next();
			if (e.isMarkedForRemoval())
			{
				e.dispose();
				iter.remove();
			}
			else e.tick(tick);
		}
	}
	
	public Island[] getIslands()
	{
		return islands;
	}
	
	public Array<Entity> getEntities()
	{
		return entities;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getDepth()
	{
		return depth;
	}
	
	public void addEntity(Entity e)
	{
		if (e instanceof Structure) Gdx.app.debug("World.addEntity", "Discouraged! Structures should be added to a specific island!");
		e.onSpawn();
		entities.add(e);
	}
	
	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool)
	{
		visibleChunks = 0;
		for (Island island : islands)
		{
			if (island != null)
			{
				island.getRenderables(renderables, pool);
				visibleChunks += island.visibleChunks;
			}
		}
	}
	
	public void render(ModelBatch batch, Environment environment)
	{
		batch.render(this, environment);
		
		visibleEntities = 0;
		totalEntities = entities.size;
		for (Iterator<Entity> iter = entities.iterator(); iter.hasNext();)
		{
			Entity e = iter.next();
			if (e.inFrustum)
			{
				e.render(batch, environment);
				visibleEntities++;
			}
		}
		
		for (Island island : islands)
		{
			island.render(batch, environment);
			totalEntities += island.getStructureCount();
		}
	}
	
	@Override
	public PathBundle query(Query query)
	{
		if (query.island == -1)
		{
			Gdx.app.error("World.query", "You should specify an island index because they can't be connected yet! Return null.");
			return null;
		}
		
		Structure structure = query.sourceStructure;
		Creature creature = query.sourceCreature;
		Path path = null;
		float distance = 0;
		
		if (structure == null && creature == null)
		{
			Gdx.app.error("World.query", "You have to specify either a source Creature or Structure when querying! Return null.");
			return null;
		}
		
		if (query.searchingStructure)
		{
			if (query.sourceCreature == null)
			{
				Gdx.app.error("World.query", "You should specify a source Creature when querying a Structure! Return null.");
				return null;
			}
			Vector3 v = query.sourceCreature.getVoxelBelow();
			
			for (Iterator<Structure> iter = new ArrayIterator<Structure>(islands[query.island].structures); iter.hasNext();)
			{
				Structure s = iter.next();
				if (s == query.sourceStructure) continue;
				if (!s.getClass().equals(query.searchedClass)) continue;
				if (query.mustWork && !s.isWorking()) continue;
				if (query.mustBeEmpty && s.getInventory().getCount() > 0) continue;
				if (query.mustBeFull && !s.getInventory().isFull()) continue;
				if (query.mustHaveCapacity && s.getInventory().isFull()) continue;
				if (query.mustHaveCapacityForTransportedItemStack && query.transportedItemStack != null && s.getInventory().getCount() + query.transportedItemStack.getAmount() > s.getInventory().getCapacity()) continue;
				if (query.searchedNodeType != null && !s.hasStructureNode(query.searchedNodeType)) continue;
				if (query.searchedNodeName != null && !s.hasStructureNode(query.searchedNodeName)) continue;
				if (query.searchedItemStack != null && !s.getInventory().contains(query.searchedItemStack)) continue;
				
				NodeType type = query.searchedNodeType != null ? query.searchedNodeType : NodeType.target;
				Path p = AStar.findPath(v, s.getStructureNode(v, type, query.searchedNodeName).pos.cpy().add(s.getVoxelPos()), query.sourceCreature, type.useGhostTarget);
				if (p == null) continue;
				
				float dist = p.length();
				if (path == null || (query.takeClosest && dist < distance) || (!query.takeClosest && dist > distance))
				{
					distance = dist;
					path = p;
					structure = s;
				}
			}
		}
		else
		{
			NodeType type = query.searchedNodeType != null ? query.searchedNodeType : NodeType.target;
			
			for (Iterator<Entity> iter = new ArrayIterator<Entity>(entities); iter.hasNext();)
			{
				Entity e = iter.next();
				if (!(e instanceof Creature)) continue;
				if (e == query.sourceCreature) continue;
				if (!e.getClass().equals(query.searchedClass)) continue;
				if (query.searchedClass.equals(Human.class))
				{
					if (query.mustIdle && !((Human) e).isIdle()) continue;
					if (query.mustBeFull && !((Human) e).getCarryingItemStack().isFull()) continue;
					if (query.mustBeEmpty && !((Human) e).getCarryingItemStack().isNull()) continue;
					if (query.mustHaveCapacity && ((Human) e).getCarryingItemStack().isFull()) continue;
					if (query.mustHaveCapacityForTransportedItemStack && !((Human) e).getCarryingItemStack().canAdd(query.transportedItemStack)) continue;
				}
				
				Vector3 v = ((Creature) e).getVoxelBelow();
				Vector3 to = query.sourceStructure.getStructureNode(v, type, query.searchedNodeName).pos.cpy().add(query.sourceStructure.getVoxelPos());
				Path p = AStar.findPath(v, query.sourceCreature != null ? query.sourceCreature.getVoxelBelow() : to, query.sourceCreature != null ? query.sourceCreature : (Creature) e, query.sourceCreature != null ? true : type.useGhostTarget);
				if (p == null) continue;
				
				float dist = p.length();
				if (path == null || (query.takeClosest && dist < distance) || (!query.takeClosest && dist > distance))
				{
					distance = dist;
					path = p;
					creature = (Creature) e;
				}
			}
		}
		
		if (path == null) return null;
		return new PathBundle(path, structure, creature);
	}
	
	public static float calculateRelativeUplift(float y)
	{
		return (1 - y / MAXHEIGHT) * 4 + 0.1f;
	}
}
