package de.dakror.vloxlands.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config;
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
	
	boolean printedPrefixLength;
	
	public Vector3 index, pos;
	
	public Chunk[] chunks;
	
	public Island()
	{
		chunks = new Chunk[CHUNKS * CHUNKS * CHUNKS];
		int l = 0;
		for (int i = 0; i < CHUNKS; i++)
			for (int j = 0; j < CHUNKS; j++)
				for (int k = 0; k < CHUNKS; k++)
					chunks[l++] = new Chunk(i, j, k, this);
		
		// opaqueMeshData = new float[Chunk.SIZE * Chunk.SIZE * Chunk.SIZE * Chunk.VERTEX_SIZE * 6];
		// transpMeshData = new float[Chunk.SIZE * Chunk.SIZE * Chunk.SIZE * Chunk.VERTEX_SIZE * 6];
	}
	
	public void setPos(Vector3 pos)
	{
		this.pos = pos;
	}
	
	public void calculateInitBalance()
	{
		calculateWeight();
		calculateUplift();
		initBalance = (uplift * World.calculateUplift(pos.y) - weight) / 100000f;
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
	
	@Override
	public void tick(int tick)
	{
		float deltaY = (int) (((uplift * World.calculateUplift(pos.y) - weight) / 100000f - initBalance) * 100f) / 100f;
		pos.y += deltaY;
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
		
		chunks[chunkZ + chunkY * CHUNKS + chunkX * CHUNKS * CHUNKS].set((int) x % Chunk.SIZE, (int) y % Chunk.SIZE, (int) z % Chunk.SIZE, id, force);
	}
	
	public boolean isSurrounded(float x, float y, float z, boolean opaque)
	{
		for (Direction d : Direction.values())
		{
			Voxel v = Voxel.getVoxelForId(get(x + d.dir.x, y + d.dir.y, z + d.dir.z));
			if (v.isOpaque() != opaque || v.getId() == 0) return false;
		}
		
		return true;
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
		
		for (int i = 0; i < chunks.length; i++)
		{
			Chunk chunk = chunks[i];
			if (Vloxlands.currentGame.camera.frustum.boundsInFrustum(pos.x + chunk.pos.x + hs, pos.y + chunk.pos.y + hs, pos.z + chunk.pos.z + hs, hs, hs, hs))
			{
				if (chunk.updateMeshes() && !chunk.isEmpty()) visibleChunks++;
				
				if (chunk.isEmpty()) continue;
				
				Renderable opaque = pool.obtain();
				opaque.worldTransform.setTranslation(pos.x, pos.y, pos.z);
				opaque.material = World.opaque;
				opaque.mesh = chunk.getOpaqueMesh();
				opaque.meshPartOffset = 0;
				opaque.meshPartSize = chunk.opaqueVerts;
				opaque.primitiveType = GL20.GL_TRIANGLES;
				
				Renderable transp = pool.obtain();
				transp.worldTransform.setTranslation(pos.x, pos.y, pos.z);
				transp.material = World.transp;
				transp.mesh = chunk.getTransparentMesh();
				transp.meshPartOffset = 0;
				transp.meshPartSize = chunk.transpVerts;
				transp.primitiveType = GL20.GL_TRIANGLES;
				
				if (!printedPrefixLength)
				{
					Gdx.app.log("prefixLength", DefaultShader.createPrefix(opaque, new Config()).split("\n").length + "");
					printedPrefixLength = true;
				}
				
				renderables.add(opaque);
				renderables.add(transp);
			}
		}
	}
}
