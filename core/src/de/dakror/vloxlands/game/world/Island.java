package de.dakror.vloxlands.game.world;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.query.VoxelPos;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.generate.biome.BiomeType;
import de.dakror.vloxlands.layer.GameLayer;
import de.dakror.vloxlands.util.Direction;
import de.dakror.vloxlands.util.Savable;
import de.dakror.vloxlands.util.Tickable;
import de.dakror.vloxlands.util.event.SelectionListener;
import de.dakror.vloxlands.util.math.Bits;

/**
 * @author Dakror
 */
public class Island implements RenderableProvider, Tickable, Savable
{
	public static final int CHUNKS = 8;
	public static final int SIZE = CHUNKS * Chunk.SIZE;
	public static final int SNOWLEVEL = 50;
	public static final float SNOW_PER_TICK = 0.2f;
	public static final float SNOW_INCREASE = 16;

	public FrameBuffer fbo;
	public Vector3 index, pos;
	public Chunk[] chunks;

	public int visibleChunks;
	public int loadedChunks;

	public float initBalance = 0;

	public boolean initFBO;

	Array<Structure> structures = new Array<Structure>();

	BiomeType biome;

	float weight, uplift;

	int tick;

	boolean minimapMode;
	boolean inFrustum;

	public Island(BiomeType biome)
	{
		this.biome = biome;
		chunks = new Chunk[CHUNKS * CHUNKS * CHUNKS];
		initFBO = false;
		minimapMode = false;
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

		for (Structure s : structures)
			weight += s.getWeight();
	}

	public void calculateUplift()
	{
		uplift = 0;
		for (Chunk c : chunks)
		{
			c.calculateUplift();
			uplift += c.uplift;
		}

		for (Structure s : structures)
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

		float deltaY = (int) (((uplift * World.calculateRelativeUplift(pos.y) - weight) / 100000f - initBalance) * 100f) / 100f;
		pos.y += deltaY;

		for (Chunk c : chunks)
			c.tick(tick);

		for (Iterator<Structure> iter = structures.iterator(); iter.hasNext();)
		{
			Structure s = iter.next();
			if (s.isMarkedForRemoval())
			{
				s.selected = false;
				for (SelectionListener sl : GameLayer.instance.listeners)
					sl.onStructureSelection(null, true);
				s.dispose();
				iter.remove();
			}
			else
			{
				s.tick(tick);
				if (deltaY != 0) s.getTransform().translate(0, deltaY, 0);
			}
		}

		inFrustum = GameLayer.camera.frustum.boundsInFrustum(pos.x + SIZE / 2, pos.y + SIZE / 2, pos.z + SIZE / 2, SIZE / 2, SIZE / 2, SIZE / 2);
	}

	public void addStructure(Structure s, boolean user, boolean clearArea)
	{
		s.onSpawn();
		s.getTransform().translate(pos);
		structures.add(s);

		if (!user && clearArea)
		{
			byte air = Voxel.get("AIR").getId();

			Vector3 vp = s.getVoxelPos();

			for (int i = -1; i < Math.ceil(s.getBoundingBox().getDimensions().x) + 1; i++)
				for (int j = 0; j < Math.ceil(s.getBoundingBox().getDimensions().y); j++)
					for (int k = -1; k < Math.ceil(s.getBoundingBox().getDimensions().z) + 1; k++)
						set(i + vp.x, j + vp.y + 1, k + vp.z, air);
			
			grassify();
		}

		recalculate();
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
		return structures.size;
	}

	public Array<Structure> getStructures()
	{
		return structures;
	}

	public void grassify()
	{
		for (Chunk c : chunks)
			c.grassify(this);
	}

	protected void renderStructures(ModelBatch batch, Environment environment, boolean minimapMode)
	{
		for (Iterator<Structure> iter = structures.iterator(); iter.hasNext();)
		{
			Structure s = iter.next();
			if (s.inFrustum || minimapMode)
			{
				s.render(batch, environment, minimapMode);
				if (!minimapMode) GameLayer.world.visibleEntities++;
			}
		}
	}

	public void render(ModelBatch batch, Environment environment)
	{
		renderStructures(batch, environment, false);
		
		if (((tick % 60 == 0 && GameLayer.instance.activeIsland == this) || !initFBO || fbo.getWidth() != Gdx.graphics.getWidth() || fbo.getHeight() != Gdx.graphics.getHeight()) && environment != null)
		{
			if (fbo == null || fbo.getWidth() != Gdx.graphics.getWidth() || fbo.getHeight() != Gdx.graphics.getHeight()) fbo = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
			
			fbo.begin();
			Gdx.gl.glClearColor(0.5f, 0.8f, 0.85f, 0);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			
			GameLayer.instance.minimapCamera.position.set(pos);
			((OrthographicCamera) GameLayer.instance.minimapCamera).zoom = 0.05f * Math.max(0.5f, Island.SIZE / 32f) / (Gdx.graphics.getWidth() / 1920f);
			GameLayer.instance.minimapCamera.translate(0, SIZE, 0);
			GameLayer.instance.minimapCamera.lookAt(pos.x + SIZE / 2, pos.y + SIZE / 2, pos.z + SIZE / 2);
			GameLayer.instance.minimapCamera.translate(0, 5, 0);
			GameLayer.instance.minimapCamera.update();
			
			minimapMode = true;
			
			GameLayer.instance.minimapBatch.begin(GameLayer.instance.minimapCamera);
			GameLayer.instance.minimapBatch.render(this, GameLayer.instance.minimapEnv);
			renderStructures(GameLayer.instance.minimapBatch, GameLayer.instance.minimapEnv, true);
			GameLayer.instance.minimapBatch.end();
			fbo.end();
			initFBO = wasDrawnOnce();
			minimapMode = false;
			Gdx.gl.glClearColor(0.5f, 0.8f, 0.85f, 1);
		}
	}

	public boolean wasDrawnOnce()
	{
		for (Chunk c : chunks)
			if (!c.isEmpty() && !c.drawn) return false;
		return true;
	}

	public boolean isFullyLoaded()
	{
		for (Chunk c : chunks)
			if (!c.isEmpty() && !c.loaded) return false;
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
		Renderable block = null;

		if (!minimapMode && !inFrustum) return;

		for (int i = 0; i < chunks.length; i++)
		{
			Chunk chunk = chunks[i];
			if (chunk.isEmpty()) continue;

			if (!chunk.onceLoaded) chunk.load();

			if (minimapMode || (chunk.inFrustum = GameLayer.camera.frustum.boundsInFrustum(pos.x + chunk.pos.x + hs, pos.y + chunk.pos.y + hs, pos.z + chunk.pos.z + hs, hs, hs, hs)))
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

					if (chunk.selectedVoxel.x > -1 && !minimapMode)
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

			if (chunk.loaded && !minimapMode) loadedChunks++;
		}

		if (block != null && !minimapMode) renderables.add(block);
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
			if (!c.isEmpty()) i++;
			c.save(baos1);
		}

		Bits.putShort(baos, i); // maximum of i is 8³ = 512 so 1 byte is not enough
		baos.write(baos1.toByteArray());

		Bits.putInt(baos, structures.size);
	}
}
