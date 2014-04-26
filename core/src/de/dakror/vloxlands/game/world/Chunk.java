package de.dakror.vloxlands.game.world;

import java.util.Comparator;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ObjectMap;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.render.Mesher;
import de.dakror.vloxlands.render.MeshingThread;
import de.dakror.vloxlands.render.VoxelFace;
import de.dakror.vloxlands.render.VoxelFace.VoxelFaceKey;
import de.dakror.vloxlands.util.Direction;
import de.dakror.vloxlands.util.Meshable;

/**
 * @author Dakror
 */
public class Chunk implements Meshable
{
	public static final int SIZE = 8;
	public static final int VERTEX_SIZE = 10;
	
	public int opaqueVerts, transpVerts;
	
	public Vector3 index;
	public Vector3 pos;
	
	byte[] voxels;
	
	FloatArray opaqueMeshData;
	FloatArray transpMeshData;
	
	float weight, uplift;
	
	Mesh opaque, transp;
	
	public boolean inFrustum;
	public boolean selected;
	
	boolean updateRequired;
	boolean meshing;
	boolean meshRequest;
	boolean doneMeshing;
	
	public Vector3 selectedVoxel;
	
	Vector2 tex;
	Island island;
	
	int[] resources;
	
	public Chunk(Vector3 index, Island island)
	{
		this.index = index;
		this.island = island;
		pos = index.cpy().scl(SIZE);
		
		voxels = new byte[SIZE * SIZE * SIZE];
		for (int i = 0; i < voxels.length; i++)
			voxels[i] = 0;
		
		resources = new int[Voxel.VOXELS];
		resources[Voxel.get("AIR").getId() + 128] = SIZE * SIZE * SIZE;
		
		updateRequired = true;
		
		int len = SIZE * SIZE * SIZE * 6 * 6 / 3;
		short[] indices = new short[len];
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
		
		opaque = new Mesh(true, SIZE * SIZE * SIZE * 6 * 4, SIZE * SIZE * SIZE * 36 / 3, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0), VertexAttribute.TexCoords(1) /* how many faces together? */);
		opaque.setIndices(indices);
		transp = new Mesh(false, SIZE * SIZE * SIZE * 6 * 4, SIZE * SIZE * SIZE * 36 / 3, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0), VertexAttribute.TexCoords(1) /* how many faces together? */);
		transp.setIndices(indices);
		
		opaqueMeshData = new FloatArray();
		transpMeshData = new FloatArray();
		
		MeshingThread.register(this);
	}
	
	public Chunk(int x, int y, int z, Island island)
	{
		this(new Vector3(x, y, z), island);
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
		
		if (!force && voxels[z + y * SIZE + x * SIZE * SIZE] != Voxel.get("AIR").getId()) return false;
		
		if (selectedVoxel != null)
		{
			if (selectedVoxel.equals(new Vector3(x, y, z)) && id == Voxel.get("AIR").getId()) selectedVoxel = null;
		}
		
		if (resources[get(x, y, z) + 128] > 0) resources[get(x, y, z) + 128]--;
		
		voxels[z + y * SIZE + x * SIZE * SIZE] = id;
		
		resources[id + 128]++;
		
		updateRequired = true;
		
		return true;
	}
	
	public void forceUpdate()
	{
		updateRequired = true;
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
		if (doneMeshing)
		{
			opaque.setVertices(opaqueMeshData.items, 0, opaqueMeshData.size);
			transp.setVertices(transpMeshData.items, 0, transpMeshData.size);
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
					
					Vloxlands.currentGame.tmp3.set(Vloxlands.currentGame.tmp1.cpy().add(x, y, z));
					Vloxlands.currentGame.tmp4.set(Vloxlands.currentGame.tmp3.cpy().add(1, 1, 1));
					Vloxlands.currentGame.bb2.set(Vloxlands.currentGame.tmp3, Vloxlands.currentGame.tmp4);
					
					if (Intersector.intersectRayBounds(ray, Vloxlands.currentGame.bb2, is))
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
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				for (int k = 0; k < SIZE; k++)
					if (get(i, j, k) == Voxel.get("DIRT").getId() && island.get(i + pos.x, j + pos.y + 1, k + pos.z) == 0) set(i, j, k, Voxel.get("GRASS").getId());
	}
	
	public synchronized void getVertices()
	{
		ObjectMap<VoxelFaceKey, VoxelFace> faces = new ObjectMap<VoxelFaceKey, VoxelFace>();
		ObjectMap<VoxelFaceKey, VoxelFace> transpFaces = new ObjectMap<VoxelFaceKey, VoxelFace>();
		
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
							VoxelFace face = new VoxelFace(d, new Vector3(x + pos.x, y + pos.y, z + pos.z), Voxel.getForId(voxel).getTextureUV(x, y, z, d));
							VoxelFaceKey key = new VoxelFaceKey(x + (int) pos.x, y + (int) pos.y, z + (int) pos.z, d.ordinal());
							if (v.isOpaque()) faces.put(key, face);
							else transpFaces.put(key, face);
						}
					}
				}
			}
		}
		
		faces = Mesher.generateGreedyMesh((int) index.x, (int) index.y, (int) index.z, faces);
		transpFaces = Mesher.generateGreedyMesh((int) index.x, (int) index.y, (int) index.z, transpFaces);
		for (VoxelFace vf : faces.values())
			vf.getVertexData(opaqueMeshData);
		
		Array<VoxelFaceKey> vfks = transpFaces.keys().toArray();
		
		vfks.sort(new Comparator<VoxelFaceKey>()
		{
			@Override
			public int compare(VoxelFaceKey o1, VoxelFaceKey o2)
			{
				return o1.compareTo(o2);
			}
		});
		
		for (VoxelFaceKey vfk : vfks)
			transpFaces.get(vfk).getVertexData(transpMeshData);
	}
	
	@Override
	public void mesh()
	{
		if (meshRequest && !meshing)
		{
			meshing = true;
			opaqueMeshData.clear();
			transpMeshData.clear();
			getVertices();
			int opaqueNumVerts = opaqueMeshData.size / VERTEX_SIZE;
			int transpNumVerts = transpMeshData.size / VERTEX_SIZE;
			opaqueVerts = opaqueNumVerts / 4 * 6;
			transpVerts = transpNumVerts / 4 * 6;
			meshRequest = false;
			doneMeshing = true;
			meshing = false;
		}
	}
}
