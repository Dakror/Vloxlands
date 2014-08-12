package de.dakror.vloxlands.game.world;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.item.inv.Inventory;
import de.dakror.vloxlands.game.item.inv.ResourceList;
import de.dakror.vloxlands.game.query.VoxelPos;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.generate.biome.BiomeType;
import de.dakror.vloxlands.util.Direction;
import de.dakror.vloxlands.util.event.InventoryListener;
import de.dakror.vloxlands.util.event.SelectionListener;
import de.dakror.vloxlands.util.interf.Savable;
import de.dakror.vloxlands.util.interf.Tickable;
import de.dakror.vloxlands.util.math.Bits;

/**
 * @author Dakror
 */
public class Island implements RenderableProvider, Tickable, Savable, InventoryListener
{
	public static final int CHUNKS = 8;
	public static final int SIZE = CHUNKS * Chunk.SIZE;
	public static final int SNOWLEVEL = 50;
	public static final float SNOW_PER_TICK = 0.2f;
	public static final float SNOW_INCREASE = 16;
	
	public FrameBuffer fbo;
	public Vector3 index, pos;
	public Chunk[] chunks;
	public ResourceList totalResources;
	
	public int visibleChunks;
	public int loadedChunks;
	
	public float initBalance = 0;
	
	public boolean initFBO;
	
	CopyOnWriteArrayList<Entity> entities = new CopyOnWriteArrayList<Entity>();
	
	BiomeType biome;
	
	float weight, uplift;
	
	int tick;
	
	boolean minimapMode;
	boolean inFrustum;
	boolean resourceListUpdateRequested;
	
	public Island(BiomeType biome)
	{
		this.biome = biome;
		chunks = new Chunk[CHUNKS * CHUNKS * CHUNKS];
		initFBO = false;
		minimapMode = false;
		index = new Vector3();
		pos = new Vector3();
		
		totalResources = new ResourceList();
	}
	
	public void setPos(Vector3 pos)
	{
		this.pos = pos;
	}
	
	public BiomeType getBiome()
	{
		return biome;
	}
	
	public void calculateInitBalance()
	{
		recalculate();
		initBalance = (uplift * World.calculateRelativeUplift(pos.y) - weight) / 100000f;
	}
	
	public void calculateWeight()
	{
		weight = 0;
		for (Chunk c : chunks)
		{
			if (c == null) continue;
			c.calculateWeight();
			weight += c.weight;
		}
		
		for (Entity s : entities)
			weight += s.getWeight();
	}
	
	public void calculateUplift()
	{
		uplift = 0;
		for (Chunk c : chunks)
		{
			if (c == null) continue;
			c.calculateUplift();
			uplift += c.uplift;
		}
		
		for (Entity s : entities)
			uplift += s.getUplift();
	}
	
	public void recalculate()
	{
		calculateUplift();
		calculateWeight();
	}
	
	@Override
	public void tick(int tick)
	{
		this.tick = tick;
		
		float delta = getDelta();
		
		if (Game.instance.activeIsland == this && delta != 0)
		{
			Game.camera.position.y += delta;
			Game.instance.controller.target.y += delta;
			Game.camera.update();
		}
		pos.y += delta;
		
		for (Chunk c : chunks)
			if (c != null) c.tick(tick);
		
		for (Entity e : entities)
		{
			if (e.isMarkedForRemoval())
			{
				e.selected = false;
				if (e instanceof Structure)
				{
					for (SelectionListener sl : Game.instance.listeners)
						sl.onStructureSelection(null, true);
					
					totalResources.decreaseCostBuildings();
				}
				else if (e instanceof Creature)
				{
					for (SelectionListener sl : Game.instance.listeners)
						sl.onCreatureSelection(null, true);
					
					if (e instanceof Human) totalResources.decreaseCostPopulation();
				}
				e.dispose();
				entities.remove(e);
				
			}
			else if (e.isSpawned())
			{
				e.tick(tick);
				if (delta != 0) e.getTransform().translate(0, delta, 0);
			}
		}
		
		inFrustum = Game.camera.frustum.boundsInFrustum(pos.x + SIZE / 2, pos.y + SIZE / 2, pos.z + SIZE / 2, SIZE / 2, SIZE / 2, SIZE / 2);
	}
	
	public void update(float delta)
	{
		for (Entity e : entities)
			if (e.isSpawned() && !e.isMarkedForRemoval()) e.update(delta);
	}
	
	public float getDelta()
	{
		return (int) (((uplift * World.calculateRelativeUplift(pos.y) - weight) / 100000f - initBalance) * 100f) / 100f;
	}
	
	public float getDeltaPerSecond()
	{
		return getDelta() * 60f;
	}
	
	public void addEntity(Entity s, boolean user, boolean clearArea)
	{
		s.setIsland(this);
		if (s instanceof Structure) ((Structure) s).getInnerInventory().addListener(this);
		s.getTransform().translate(pos);
		entities.add(s);
		s.onSpawn();
		
		if (!user && clearArea && (s instanceof Structure))
		{
			byte air = Voxel.get("AIR").getId();
			
			Vector3 vp = ((Structure) s).getVoxelPos();
			
			for (int i = -1; i < Math.ceil(s.getBoundingBox().getDimensions().x) + 1; i++)
				for (int j = 0; j < Math.ceil(s.getBoundingBox().getDimensions().y); j++)
					for (int k = -1; k < Math.ceil(s.getBoundingBox().getDimensions().z) + 1; k++)
						set(i + vp.x, j + vp.y + 1, k + vp.z, air);
			
			grassify();
		}
		
		if (s instanceof Structure || s.getWeight() != 0 || s.getUplift() != 0) recalculate();
	}
	
	public byte get(float x, float y, float z)
	{
		int chunkX = (int) (x / Chunk.SIZE);
		if (chunkX < 0 || chunkX >= CHUNKS) return 0;
		int chunkY = (int) (y / Chunk.SIZE);
		if (chunkY < 0 || chunkY >= CHUNKS) return 0;
		int chunkZ = (int) (z / Chunk.SIZE);
		if (chunkZ < 0 || chunkZ >= CHUNKS) return 0;
		
		ensureChunkExists(chunkX, chunkY, chunkZ);
		
		return chunks[chunkZ + chunkY * CHUNKS + chunkX * CHUNKS * CHUNKS].get((int) x % Chunk.SIZE, (int) y % Chunk.SIZE, (int) z % Chunk.SIZE);
	}
	
	public void add(float x, float y, float z, byte id)
	{
		set(x, y, z, id, false, true);
	}
	
	public void set(float x, float y, float z, byte id)
	{
		set(x, y, z, id, true, true);
	}
	
	public void set(float x, float y, float z, byte id, boolean force, boolean notify)
	{
		int chunkX = (int) (x / Chunk.SIZE);
		if (chunkX < 0 || chunkX >= CHUNKS) return;
		int chunkY = (int) (y / Chunk.SIZE);
		if (chunkY < 0 || chunkY >= CHUNKS) return;
		int chunkZ = (int) (z / Chunk.SIZE);
		if (chunkZ < 0 || chunkZ >= CHUNKS) return;
		
		int x1 = (int) x % Chunk.SIZE;
		int y1 = (int) y % Chunk.SIZE;
		int z1 = (int) z % Chunk.SIZE;
		
		ensureChunkExists(chunkX, chunkY, chunkZ);
		
		if (chunks[chunkZ + chunkY * CHUNKS + chunkX * CHUNKS * CHUNKS].set(x1, y1, z1, id, force))
		{
			if (Game.instance.activeIsland == this && id == Voxel.get("AIR").getId() && x == Game.instance.selectedVoxel.x && y == Game.instance.selectedVoxel.y && z == Game.instance.selectedVoxel.z)
			{
				Game.instance.selectedVoxel.set(-1, 0, 0);
			}
			if (notify) notifySurroundingChunks(chunkX, chunkY, chunkZ);
		}
	}
	
	public void ensureChunkExists(int x, int y, int z)
	{
		int index = z + y * CHUNKS + x * CHUNKS * CHUNKS;
		if (chunks[index] == null) chunks[index] = new Chunk(x, y, z, this);
	}
	
	public void notifySurroundingChunks(int cx, int cy, int cz)
	{
		for (Direction d : Direction.values())
		{
			try
			{
				int index = (int) ((cz + d.dir.z) + (cy + d.dir.y) * CHUNKS + (cx + d.dir.x) * CHUNKS * CHUNKS);
				if (chunks[index] != null) chunks[index].forceUpdate();
			}
			catch (IndexOutOfBoundsException e)
			{
				continue;
			}
		}
	}
	
	public float getWeight()
	{
		return weight;
	}
	
	public float getUplift()
	{
		return uplift;
	}
	
	public Chunk[] getChunks()
	{
		return chunks;
	}
	
	public Chunk getChunk(float x, float y, float z)
	{
		return getChunk((int) (x * CHUNKS * CHUNKS + y * CHUNKS + z));
	}
	
	public Chunk getChunk(int i)
	{
		return chunks[i];
	}
	
	public int getStructureCount()
	{
		return entities.size();
	}
	
	public CopyOnWriteArrayList<Entity> getEntities()
	{
		return entities;
	}
	
	public void grassify()
	{
		for (Chunk c : chunks)
			if (c != null) c.grassify(this);
	}
	
	protected void renderEntities(ModelBatch batch, Environment environment, boolean minimapMode)
	{
		for (Entity s : entities)
		{
			if (minimapMode && !(s instanceof Structure)) continue;
			
			if (s.inFrustum || minimapMode)
			{
				s.render(batch, environment, minimapMode);
				if (!minimapMode) Game.world.visibleEntities++;
			}
		}
	}
	
	public void render(ModelBatch batch, Environment environment)
	{
		renderEntities(batch, environment, false);
		
		if (Game.instance.activeIsland == this && Game.instance.selectedVoxel.x > -1)
		{
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Game.shapeRenderer.setProjectionMatrix(Game.camera.combined);
			Game.shapeRenderer.identity();
			Game.shapeRenderer.translate(pos.x + Game.instance.selectedVoxel.x, pos.y + Game.instance.selectedVoxel.y, pos.z + Game.instance.selectedVoxel.z + 1);
			Game.shapeRenderer.begin(ShapeType.Line);
			Game.shapeRenderer.setColor(Color.WHITE);
			Game.shapeRenderer.box(-World.gap / 2, -World.gap / 2, -World.gap / 2, 1 + World.gap, 1 + World.gap, 1 + World.gap);
			Game.shapeRenderer.end();
		}
		
		if (((tick % 60 == 0 && Game.instance.activeIsland == this) || !initFBO || fbo.getWidth() != Gdx.graphics.getWidth() || fbo.getHeight() != Gdx.graphics.getHeight()) && environment != null)
		{
			if (fbo == null || fbo.getWidth() != Gdx.graphics.getWidth() || fbo.getHeight() != Gdx.graphics.getHeight()) fbo = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
			
			fbo.begin();
			Gdx.gl.glClearColor(0.5f, 0.8f, 0.85f, 0);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			
			Game.instance.minimapCamera.position.set(pos);
			((OrthographicCamera) Game.instance.minimapCamera).zoom = 0.05f * Math.max(0.5f, Island.SIZE / 32f) / (Gdx.graphics.getWidth() / 1920f);
			Game.instance.minimapCamera.translate(0, SIZE, 0);
			Game.instance.minimapCamera.lookAt(pos.x + SIZE / 2, pos.y + SIZE / 2, pos.z + SIZE / 2);
			Game.instance.minimapCamera.translate(0, 5, 0);
			Game.instance.minimapCamera.update();
			
			minimapMode = true;
			
			Game.instance.minimapBatch.begin(Game.instance.minimapCamera);
			Game.instance.minimapBatch.render(this, Game.instance.minimapEnv);
			renderEntities(Game.instance.minimapBatch, Game.instance.minimapEnv, true);
			Game.instance.minimapBatch.end();
			fbo.end();
			initFBO = wasDrawnOnce();
			minimapMode = false;
			Gdx.gl.glClearColor(0.5f, 0.8f, 0.85f, 1);
		}
	}
	
	public boolean wasDrawnOnce()
	{
		for (Chunk c : chunks)
			if (c != null && !c.isEmpty() && !c.drawn) return false;
		return true;
	}
	
	public boolean isFullyLoaded()
	{
		for (Chunk c : chunks)
			if (c != null && !c.isEmpty() && !c.loaded) return false;
		return true;
	}
	
	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool)
	{
		if (!minimapMode)
		{
			visibleChunks = 0;
			loadedChunks = 0;
		}
		int hs = Chunk.SIZE / 2;
		
		if (!minimapMode && !inFrustum) return;
		
		for (int i = 0; i < chunks.length; i++)
		{
			Chunk chunk = chunks[i];
			if (chunk == null) continue;
			if (chunk.isEmpty()) continue;
			
			if (!chunk.onceLoaded) chunk.load();
			
			if (minimapMode || (chunk.inFrustum = Game.camera.frustum.boundsInFrustum(pos.x + chunk.pos.x + hs, pos.y + chunk.pos.y + hs, pos.z + chunk.pos.z + hs, hs, hs, hs)))
			{
				if (!chunk.loaded && !minimapMode) chunk.load();
				
				if (chunk.updateMeshes() && !minimapMode) visibleChunks++;
				
				if (chunk.loaded && (chunk.opaqueVerts > 0 || chunk.transpVerts > 0))
				{
					if (minimapMode) chunk.drawn = true;
					
					Renderable opaque = pool.obtain();
					opaque.worldTransform.setToTranslation(pos.x, pos.y, pos.z);
					opaque.material = World.opaque;
					opaque.mesh = chunk.getOpaqueMesh();
					opaque.meshPartOffset = 0;
					opaque.meshPartSize = chunk.opaqueVerts;
					opaque.primitiveType = GL20.GL_TRIANGLES;
					if (chunk.opaqueVerts > 0) renderables.add(opaque);
					
					Renderable transp = pool.obtain();
					transp.worldTransform.setToTranslation(pos.x, pos.y, pos.z);
					transp.material = World.transp;
					transp.mesh = chunk.getTransparentMesh();
					transp.meshPartOffset = 0;
					transp.meshPartSize = chunk.transpVerts;
					transp.primitiveType = GL20.GL_TRIANGLES;
					if (chunk.transpVerts > 0) renderables.add(transp);
					
					if (Vloxlands.wireframe && !minimapMode)
					{
						Renderable opaque1 = pool.obtain();
						opaque1.worldTransform.setToTranslation(pos.x, pos.y, pos.z);
						opaque1.material = World.highlight;
						opaque1.mesh = chunk.getOpaqueMesh();
						opaque1.meshPartOffset = 0;
						opaque1.meshPartSize = chunk.opaqueVerts;
						opaque1.primitiveType = GL20.GL_LINES;
						if (chunk.opaqueVerts > 0) renderables.add(opaque1);
						
						Renderable transp1 = pool.obtain();
						transp1.worldTransform.setToTranslation(pos.x, pos.y, pos.z);
						transp1.material = World.highlight;
						transp1.mesh = chunk.getTransparentMesh();
						transp1.meshPartOffset = 0;
						transp1.meshPartSize = chunk.transpVerts;
						transp1.primitiveType = GL20.GL_LINES;
						if (chunk.transpVerts > 0) renderables.add(transp1);
					}
				}
			}
			
			if (chunk.loaded && !minimapMode) loadedChunks++;
		}
	}
	
	@Override
	public void onItemAdded(int countBefore, Item item, Inventory inventory)
	{
		if (item != null)
		{
			int delta = inventory.getCount() - countBefore;
			totalResources.add(item, delta);
		}
		else Gdx.app.error("Island.onItemAdded", "item = null, not handled!");
	}
	
	@Override
	public void onItemRemoved(int countBefore, Item item, Inventory inventory)
	{
		if (item != null)
		{
			int delta = countBefore - inventory.getCount();
			totalResources.remove(item, delta);
		}
		else Gdx.app.error("Island.onItemRemoved", "item = null, not handled!");
	}
	
	public boolean takeItemsIslandWide(ItemStack stack)
	{
		return takeItemsIslandWide(stack.getItem(), stack.getAmount());
	}
	
	public boolean takeItemsIslandWide(Item item, int amount)
	{
		if (totalResources.get(item) < amount) return false;
		
		for (Entity e : entities)
		{
			if (!(e instanceof Structure) || !((Structure) e).isBuilt()) continue;
			
			if (((Structure) e).getInventory().get(item) > 0)
			{
				amount -= ((Structure) e).getInventory().take(item, amount).getAmount();
			}
			
			if (amount == 0) break;
		}
		
		return amount == 0;
	}
	
	// -- voxel queries -- //
	
	public boolean isSurrounded(float x, float y, float z, boolean opaque)
	{
		for (Direction d : Direction.values())
		{
			Voxel v = Voxel.getForId(get(x + d.dir.x, y + d.dir.y, z + d.dir.z));
			if (v.isOpaque() != opaque || v.getId() == 0) return false;
		}
		
		return true;
	}
	
	/**
	 * Has atleast one air voxel adjacent
	 */
	public boolean isTargetable(float x, float y, float z)
	{
		byte air = Voxel.get("AIR").getId();
		for (Direction d : Direction.values())
		{
			byte b = get(x + d.dir.x, y + d.dir.y, z + d.dir.z);
			if (b == air) return true;
		}
		
		return false;
	}
	
	/**
	 * Is solid and has air above
	 */
	public boolean isWalkable(float x, float y, float z)
	{
		byte air = Voxel.get("AIR").getId();
		byte above = get(x, y + 1, z);
		return get(x, y, z) != air && (above == air || above == 0);
	}
	
	public boolean isSpaceAbove(float x, float y, float z, int height)
	{
		byte air = Voxel.get("AIR").getId();
		for (int i = 0; i < height; i++)
		{
			byte b = get(x, y + i + 1, z);
			if (b != 0 && b != air) return false;
		}
		
		return true;
	}
	
	public boolean isWrapped(float x, float y, float z, int height)
	{
		byte air = Voxel.get("AIR").getId();
		Direction[] directions = { Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.WEST };
		for (Direction d : directions)
		{
			byte b = get(x + d.dir.x, y + d.dir.y, z + d.dir.z);
			if (b == air || (b == air && isSpaceAbove(x + d.dir.x, y + d.dir.y, z + d.dir.z, height - (int) d.dir.y))) return false;
		}
		
		return true;
	}
	
	public boolean isWrapped(float x, float y, float z)
	{
		byte air = Voxel.get("AIR").getId();
		Direction[] directions = { Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.WEST };
		for (Direction d : directions)
		{
			if (get(x + d.dir.x, y + d.dir.y, z + d.dir.z) == air) return false;
		}
		
		return true;
	}
	
	public VoxelPos getHighestVoxel(int x, int z)
	{
		byte air = Voxel.get("AIR").getId();
		for (int y = SIZE - 1; y > -1; y--)
		{
			byte b;
			if ((b = get(x, y, z)) != air) return new VoxelPos(x, y, z, b);
		}
		
		return new VoxelPos();
	}
	
	@Override
	public void save(ByteArrayOutputStream baos) throws IOException
	{
		baos.write(biome.ordinal());
		baos.write((int) index.x);
		baos.write((int) index.z);
		Bits.putFloat(baos, pos.y);
		
		short i = 0;
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		
		for (Chunk c : chunks)
		{
			if (c == null) continue;
			if (!c.isEmpty())
			{
				i++;
				c.save(baos1);
			}
		}
		
		Bits.putShort(baos, i); // maximum of i is 8³ = 512 so 1 byte is not enough
		baos.write(baos1.toByteArray());
		
		Bits.putInt(baos, entities.size());
	}
}
