package de.dakror.vloxlands.game.world;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.layer.GameLayer;
import de.dakror.vloxlands.render.Face;
import de.dakror.vloxlands.render.Mesher;
import de.dakror.vloxlands.render.MeshingThread;
import de.dakror.vloxlands.util.Compressor;
import de.dakror.vloxlands.util.Direction;
import de.dakror.vloxlands.util.Meshable;
import de.dakror.vloxlands.util.Savable;
import de.dakror.vloxlands.util.Tickable;
import de.dakror.vloxlands.util.math.Bits;

/**
 * @author Dakror
 */
public class Chunk implements Meshable, Tickable, Disposable, Savable
{
	public static short[] indices;
	public static final int SIZE = 16;
	public static final int VERTEX_SIZE = 11;
	public static final int UNLOAD_TICKS = 120;
	
	public int opaqueVerts, transpVerts;
	public Vector3 index;
	public Vector3 pos;
	
	int random;
	
	byte[] voxels;
	
	FloatArray opaqueMeshData;
	FloatArray transpMeshData;
	
	float weight, uplift;
	
	Mesh opaque, transp;
	
	public boolean inFrustum;
	
	boolean updateRequired;
	boolean meshing;
	boolean meshRequest;
	boolean doneMeshing;
	public boolean onceLoaded = false;
	public boolean drawn = false;
	public boolean loaded = false;
	
	Vector2 tex;
	Island island;
	
	int[] resources;
	int ticksInvisible;
	
	boolean requestsUnload;
	
	Array<Disposable> disposables = new Array<Disposable>();
	
	public Chunk(Vector3 index, Island island)
	{
		random = MathUtils.random(UNLOAD_TICKS);
		
		this.index = index;
		this.island = island;
		pos = index.cpy().scl(SIZE);
		
		voxels = new byte[SIZE * SIZE * SIZE];
		
		resources = new int[Voxel.VOXELS];
		resources[Voxel.get("AIR").getId() + 128] = SIZE * SIZE * SIZE;
		
		MeshingThread.register(this);
	}
	
	public Chunk(int x, int y, int z, Island island)
	{
		this(new Vector3(x, y, z), island);
	}
	
	public void load()
	{
		if (indices == null)
		{
			int len = SIZE * SIZE * SIZE * 6 * 6 / 3;
			indices = new short[len];
			short j = 0;
			for (int i = 0; i < len; i += 6, j += 4)
			{
				indices[i + 0] = (short) (j + 0);
				indices[i + 1] = (short) (j + 1);
				indices[i + 2] = (short) (j + 2);
				indices[i + 3] = (short) (j + 2);
				indices[i + 4] = (short) (j + 3);
				indices[i + 5] = (short) (j + 0);
			}
		}
		
		opaque = new Mesh(true, SIZE * SIZE * SIZE * 6 * 4, SIZE * SIZE * SIZE * 36 / 3, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.ColorPacked(), VertexAttribute.TexCoords(0), VertexAttribute.TexCoords(1) /* how many faces together? */);
		opaque.setIndices(indices);
		transp = new Mesh(true, SIZE * SIZE * SIZE * 6 * 4, SIZE * SIZE * SIZE * 36 / 3, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.ColorPacked(), VertexAttribute.TexCoords(0), VertexAttribute.TexCoords(1) /* how many faces together? */);
		transp.setIndices(indices);
		
		opaqueMeshData = new FloatArray();
		transpMeshData = new FloatArray();
		
		loaded = true;
		onceLoaded = true;
		drawn = false;
	}
	
	public void unload()
	{
		updateRequired = true;
		doneMeshing = false;
		meshing = false;
		loaded = false;
		opaqueVerts = 0;
		transpVerts = 0;
		
		opaque.dispose();
		opaque = null;
		transp.dispose();
		transp = null;
		
		opaqueMeshData = null;
		transpMeshData = null;
	}
	
	public void forceUpdate()
	{
		updateRequired = true;
	}
	
	public void add(int x, int y, int z, byte id)
	{
		set(x, y, z, id, false);
	}
	
	public boolean set(int x, int y, int z, byte id)
	{
		return set(x, y, z, id, true);
	}
	
	public boolean set(int x, int y, int z, byte id, boolean force)
	{
		if (x >= SIZE || x < 0) return false;
		if (y >= SIZE || y < 0) return false;
		if (z >= SIZE || z < 0) return false;
		
		byte air = Voxel.get("AIR").getId();
		
		int index = z + y * SIZE + x * SIZE * SIZE;
		
		if (!force && voxels[index] != air) return false;
		
		if (resources[get(x, y, z) + 128] > 0) resources[get(x, y, z) + 128]--;
		
		voxels[index] = id;
		
		resources[id + 128]++;
		
		updateRequired = true;
		
		return true;
	}
	
	public byte get(int x, int y, int z)
	{
		if (x >= SIZE || x < 0) return 0;
		if (y >= SIZE || y < 0) return 0;
		if (z >= SIZE || z < 0) return 0;
		
		return voxels[z + y * SIZE + x * SIZE * SIZE];
	}
	
	public byte[] getVoxels()
	{
		return voxels;
	}
	
	public boolean updateMeshes()
	{
		if (!loaded) return false;
		
		if (doneMeshing)
		{
			opaque.setVertices(opaqueMeshData.items, 0, opaqueMeshData.size);
			transp.setVertices(transpMeshData.items, 0, transpMeshData.size);
			
			opaqueMeshData = null;
			transpMeshData = null;
			
			doneMeshing = false;
			return true;
		}
		
		if (!updateRequired) return !meshing;
		
		updateRequired = false;
		
		if (!meshing) meshRequest = true;
		
		return false;
	}
	
	public Mesh getOpaqueMesh()
	{
		return opaque;
	}
	
	public Mesh getTransparentMesh()
	{
		return transp;
	}
	
	public boolean isEmpty()
	{
		return getResource(Voxel.get("AIR").getId()) == SIZE * SIZE * SIZE;
	}
	
	public boolean pickVoxel(Ray ray, Vector3 intersection, Vector3 v)
	{
		byte air = Voxel.get("AIR").getId();
		
		float distance = 0;
		Vector3 is = new Vector3();
		Vector3 voxel = null;
		
		for (int x = 0; x < Chunk.SIZE; x++)
		{
			for (int y = 0; y < Chunk.SIZE; y++)
			{
				for (int z = 0; z < Chunk.SIZE; z++)
				{
					byte b = get(x, y, z);
					
					if (b == air || !island.isTargetable(pos.x + x, pos.y + y, pos.z + z)) continue;
					
					GameLayer.instance.tmp3.set(GameLayer.instance.tmp1.cpy().add(x, y, z));
					GameLayer.instance.tmp4.set(GameLayer.instance.tmp3.cpy().add(1, 1, 1));
					GameLayer.instance.bb2.set(GameLayer.instance.tmp3, GameLayer.instance.tmp4);
					
					if (Intersector.intersectRayBounds(ray, GameLayer.instance.bb2, is))
					{
						float dist = ray.origin.dst(is);
						if (voxel == null || (dist = ray.origin.dst(is)) < distance)
						{
							voxel = new Vector3(x, y, z);
							distance = dist;
							intersection.set(is);
						}
					}
				}
			}
		}
		
		if (voxel != null) v.set(voxel);
		
		return voxel != null;
	}
	
	public int getResource(byte id)
	{
		return resources[id + 128];
	}
	
	public void calculateWeight()
	{
		weight = 0;
		for (int x = 0; x < SIZE; x++)
		{
			for (int y = 0; y < SIZE; y++)
			{
				for (int z = 0; z < SIZE; z++)
				{
					if (get(x, y, z) == 0) continue;
					weight += Voxel.getForId(get(x, y, z)).getWeight();
				}
			}
		}
	}
	
	public void calculateUplift()
	{
		uplift = 0;
		for (int x = 0; x < SIZE; x++)
		{
			for (int y = 0; y < SIZE; y++)
			{
				for (int z = 0; z < SIZE; z++)
				{
					if (get(x, y, z) == 0) continue;
					uplift += Voxel.getForId(get(x, y, z)).getUplift();
				}
			}
		}
	}
	
	public void grassify(Island island)
	{
		if (isEmpty() || getResource(Voxel.get("DIRT").getId()) == 0) return;
		
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				for (int k = 0; k < SIZE; k++)
					if (get(i, j, k) == Voxel.get("DIRT").getId() && island.get(i + pos.x, j + pos.y + 1, k + pos.z) == 0) set(i, j, k, Voxel.get("GRASS").getId());
	}
	
	private void getVertices()
	{
		IntMap<Face> faces = new IntMap<Face>();
		IntMap<Face> transpFaces = new IntMap<Face>();
		
		int i = 0;
		for (int x = 0; x < SIZE; x++)
		{
			for (int y = 0; y < SIZE; y++)
			{
				for (int z = 0; z < SIZE; z++, i++)
				{
					byte voxel = voxels[i];
					if (voxel == 0) continue;
					Voxel v = Voxel.getForId(voxel);
					
					if (island.isSurrounded(x + pos.x, y + pos.y, z + pos.z, v.isOpaque())) continue;
					
					for (Direction d : Direction.values())
					{
						byte w = island.get(x + d.dir.x + pos.x, y + d.dir.y + pos.y, z + d.dir.z + pos.z);
						Voxel ww = Voxel.getForId(w);
						if (w == 0 || (ww == null || !ww.isOpaque()) && w != voxel)
						{
							Face face = new Face(d, new Vector3(x + pos.x, y + pos.y, z + pos.z), Voxel.getForId(voxel).getTextureUV(x, y, z, d));
							if (v.isOpaque()) faces.put(face.hashCode(), face);
							else transpFaces.put(face.hashCode(), face);
						}
					}
				}
			}
		}
		
		Mesher.generateGreedyMesh((int) index.x, (int) index.y, (int) index.z, faces);
		Mesher.generateGreedyMesh((int) index.x, (int) index.y, (int) index.z, transpFaces);
		
		for (IntMap.Entry<Face> f : faces)
			f.value.getVertexData(opaqueMeshData);
		
		IntArray transpKeys = transpFaces.keys().toArray();
		transpKeys.sort();
		
		for (int index : transpKeys.toArray())
			transpFaces.get(index).getVertexData(transpMeshData);
	}
	
	@Override
	public void tick(int tick)
	{
		if (!inFrustum && loaded && drawn && GameLayer.instance.activeIsland != island)
		{
			ticksInvisible++;
			if (ticksInvisible > UNLOAD_TICKS + random) requestsUnload = true;
		}
		else ticksInvisible = 0;
	}
	
	public void render()
	{
		if (requestsUnload)
		{
			unload();
			ticksInvisible = 0;
			requestsUnload = false;
		}
	}
	
	@Override
	public void mesh()
	{
		if (meshRequest && !meshing)
		{
			if (!loaded) return;
			meshing = true;
			opaqueMeshData = new FloatArray();
			transpMeshData = new FloatArray();
			try
			{
				getVertices();
				int opaqueNumVerts = opaqueMeshData.size / VERTEX_SIZE;
				int transpNumVerts = transpMeshData.size / VERTEX_SIZE;
				opaqueVerts = opaqueNumVerts / 4 * 6;
				transpVerts = transpNumVerts / 4 * 6;
				meshRequest = false;
				doneMeshing = true;
				meshing = false;
			}
			catch (Exception e)
			{
				meshing = true;
			}
		}
	}
	
	@Override
	public void dispose()
	{
		for (Disposable d : disposables)
			d.dispose();
	}
	
	@Override
	public void save(ByteArrayOutputStream baos) throws IOException
	{
		if (isEmpty()) return;
		
		baos.write((int) index.x);
		baos.write((int) index.y);
		baos.write((int) index.z);
		
		byte[] b = Compressor.compressRow(voxels);
		Bits.putInt(baos, b.length);
		baos.write(b);
	}
}
