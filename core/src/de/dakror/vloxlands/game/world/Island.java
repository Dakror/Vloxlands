package de.dakror.vloxlands.game.world;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.util.Direction;
import de.dakror.vloxlands.util.Tickable;

/**
 * @author Dakror
 */
public class Island implements RenderableProvider, Tickable
{
	public static final int CHUNKS = 8;
	public static final int SIZE = CHUNKS * Chunk.SIZE;
	public static final int SNOWLEVEL = 50;
	public static final float SNOW_PER_TICK = 0.2f;
	public static final float SNOW_INCREASE = 16;
	
	public int visibleChunks;
	
	float weight, uplift;
	
	/**
	 * Used to balance after generating. Smaller than 1
	 */
	public float initBalance = 0;
	
	public Vector3 index, pos;
	
	public Chunk[] chunks;
	
	public Island()
	{
		chunks = new Chunk[CHUNKS * CHUNKS * CHUNKS];
		
		index = new Vector3();
		pos = new Vector3();
		
		int l = 0;
		for (int i = 0; i < CHUNKS; i++)
			for (int j = 0; j < CHUNKS; j++)
				for (int k = 0; k < CHUNKS; k++)
					chunks[l++] = new Chunk(i, j, k, this);
	}
	
	public void setPos(Vector3 pos)
	{
		this.pos = pos;
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
			c.calculateWeight();
			weight += c.weight;
		}
	}
	
	public void calculateUplift()
	{
		uplift = 0;
		for (Chunk c : chunks)
		{
			c.calculateUplift();
			uplift += c.uplift;
		}
	}
	
	public void recalculate()
	{
		calculateUplift();
		calculateWeight();
	}
	
	@Override
	public void tick(int tick)
	{
		float deltaY = (int) (((uplift * World.calculateRelativeUplift(pos.y) - weight) / 100000f - initBalance) * 100f) / 100f;
		pos.y += deltaY;
		
		for (Chunk c : chunks)
			c.tick(tick);
	}
	
	public byte get(float x, float y, float z)
	{
		int chunkX = (int) (x / Chunk.SIZE);
		if (chunkX < 0 || chunkX >= CHUNKS) return 0;
		int chunkY = (int) (y / Chunk.SIZE);
		if (chunkY < 0 || chunkY >= CHUNKS) return 0;
		int chunkZ = (int) (z / Chunk.SIZE);
		if (chunkZ < 0 || chunkZ >= CHUNKS) return 0;
		return chunks[chunkZ + chunkY * CHUNKS + chunkX * CHUNKS * CHUNKS].get((int) x % Chunk.SIZE, (int) y % Chunk.SIZE, (int) z % Chunk.SIZE);
	}
	
	public void add(float x, float y, float z, byte id)
	{
		set(x, y, z, id, false);
	}
	
	public void set(float x, float y, float z, byte id)
	{
		set(x, y, z, id, true);
	}
	
	public void set(float x, float y, float z, byte id, boolean force)
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
		
		if (chunks[chunkZ + chunkY * CHUNKS + chunkX * CHUNKS * CHUNKS].set(x1, y1, z1, id, force)) notifySurroundingChunks(chunkX, chunkY, chunkZ);
	}
	
	public void notifySurroundingChunks(int cx, int cy, int cz)
	{
		for (Direction d : Direction.values())
		{
			try
			{
				chunks[(int) ((cz + d.dir.z) + (cy + d.dir.y) * CHUNKS + (cx + d.dir.x) * CHUNKS * CHUNKS)].forceUpdate();
			}
			catch (IndexOutOfBoundsException e)
			{
				continue;
			}
		}
	}
	
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
	
	public float getWeight()
	{
		return weight;
	}
	
	public float getUplift()
	{
		return uplift;
	}
	
	public Vector3 getPos()
	{
		return pos;
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
	
	public void grassify()
	{
		for (Chunk c : chunks)
			c.grassify(this);
	}
	
	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool)
	{
		visibleChunks = 0;
		int hs = Chunk.SIZE / 2;
		
		Renderable block = null;
		
		for (int i = 0; i < chunks.length; i++)
		{
			Chunk chunk = chunks[i];
			if (!chunk.initialized) chunk.init();
			
			if (chunk.inFrustum = Vloxlands.camera.frustum.boundsInFrustum(pos.x + chunk.pos.x + hs, pos.y + chunk.pos.y + hs, pos.z + chunk.pos.z + hs, hs, hs, hs))
			{
				if (chunk.isEmpty()) continue;
				
				if (chunk.updateMeshes()) visibleChunks++;
				
				Renderable opaque = pool.obtain();
				opaque.worldTransform.setToTranslation(pos.x, pos.y, pos.z);
				opaque.material = World.opaque;
				opaque.mesh = chunk.getOpaqueMesh();
				opaque.meshPartOffset = 0;
				opaque.meshPartSize = chunk.opaqueVerts;
				opaque.primitiveType = GL20.GL_TRIANGLES;
				renderables.add(opaque);
				
				Renderable transp = pool.obtain();
				transp.worldTransform.setToTranslation(pos.x, pos.y, pos.z);
				transp.material = World.transp;
				transp.mesh = chunk.getTransparentMesh();
				transp.meshPartOffset = 0;
				transp.meshPartSize = chunk.transpVerts;
				transp.primitiveType = GL20.GL_TRIANGLES;
				renderables.add(transp);
				
				if (chunk.selectedVoxel.x > -1)
				{
					block = pool.obtain();
					block.worldTransform.setToTranslation(pos.x + chunk.pos.x + chunk.selectedVoxel.x - World.gap / 2, pos.y + chunk.pos.y + chunk.selectedVoxel.y - World.gap / 2, pos.z + chunk.pos.z + chunk.selectedVoxel.z - World.gap / 2);
					block.material = World.highlight;
					block.mesh = World.blockCube;
					block.meshPartOffset = 0;
					block.meshPartSize = 36;
					block.primitiveType = GL20.GL_LINES;
				}
			}
		}
		
		if (block != null) renderables.add(block);
	}
}
