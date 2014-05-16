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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.render.Mesher;
import de.dakror.vloxlands.util.Tickable;



/**
 * @author Dakror
 */
public class World implements RenderableProvider, Tickable
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
	
	public static float calculateRelativeUplift(float y)
	{
		return (1 - y / MAXHEIGHT) * 4 + 0.1f;
	}
}
