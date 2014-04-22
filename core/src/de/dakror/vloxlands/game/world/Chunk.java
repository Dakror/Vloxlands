package de.dakror.vloxlands.game.world;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ObjectMap;

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
	
	public int opaqueVerts;// , transpVerts;
	
	Vector3 index, pos;
	byte[] voxels;
	FloatArray opaqueMeshData;
	
	float weight, uplift;
	
	Mesh opaque;// , transp;
	
	boolean empty;
	boolean updateRequired;
	boolean meshing;
	boolean meshRequest;
	boolean doneMeshing;
	
	Vector2 tex;
	Island island;
	
	public Chunk(Vector3 index, Island island)
	{
		this.index = index;
		this.island = island;
		pos = index.cpy().scl(SIZE);
		
		voxels = new byte[SIZE * SIZE * SIZE];
		for (int i = 0; i < voxels.length; i++)
			voxels[i] = 0;
		
		
		// empty = true;
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
		
		opaqueMeshData = new FloatArray();
		
		// transp = new Mesh(true, SIZE * SIZE * SIZE * 6 * 4, SIZE * SIZE * SIZE * 36 / 3, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
		// transp.setIndices(indices);
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
	
	public void set(int x, int y, int z, byte id)
	{
		set(x, y, z, id, true);
	}
	
	public void set(int x, int y, int z, byte id, boolean force)
	{
		if (x >= SIZE || x < 0) return;
		if (y >= SIZE || y < 0) return;
		if (z >= SIZE || z < 0) return;
		
		if (!force && voxels[z + y * SIZE + x * SIZE * SIZE] != Voxel.get("AIR").getId()) return;
		
		voxels[z + y * SIZE + x * SIZE * SIZE] = id;
		updateRequired = true;
	}
	
	public byte get(int x, int y, int z)
	{
		if (x >= SIZE || x < 0) return -1;
		if (y >= SIZE || y < 0) return -1;
		if (z >= SIZE || z < 0) return -1;
		
		return voxels[z + y * SIZE + x * SIZE * SIZE];
	}
	
	public int getVoxelCount()
	{
		int c = 0;
		
		for (byte b : voxels)
			if (b != 0) c++;
		
		return c;
	}
	
	public boolean updateMeshes()// , float[] transpMeshData)
	{
		if (doneMeshing)
		{
			opaque.setVertices(opaqueMeshData.items, 0, (opaqueVerts / 6 * 4) * VERTEX_SIZE);
			doneMeshing = false;
			return true;
		}
		
		if (!updateRequired) return true;
		empty = getVoxelCount() == 0;
		
		if (!meshing)
		{
			meshRequest = true;
		}
		
		updateRequired = false;
		
		return false;
	}
	
	public Mesh getOpaqueMesh()
	{
		return opaque;
	}
	
	// public Mesh getTransparentMesh()
	// {
	// return transp;
	// }
	
	public boolean isEmpty()
	{
		return empty;
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
					weight += Voxel.getVoxelForId(get(x, y, z)).getWeight();
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
					uplift += Voxel.getVoxelForId(get(x, y, z)).getUplift();
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
	
	public void getVertices()// , boolean opaque)
	{
		ObjectMap<VoxelFaceKey, VoxelFace> faces = new ObjectMap<VoxelFaceKey, VoxelFace>();
		
		int i = 0;
		int vertexOffset = 0;
		for (int x = 0; x < SIZE; x++)
		{
			for (int y = 0; y < SIZE; y++)
			{
				for (int z = 0; z < SIZE; z++, i++)
				{
					byte voxel = voxels[i];
					if (voxel == 0) continue;
					
					if (island.isSurrounded(x + pos.x, y + pos.y, z + pos.z)) continue;
					
					for (Direction d : Direction.values())
					{
						if (island.get(x + d.dir.x + pos.x, y + d.dir.y + pos.y, z + d.dir.z + pos.z) == 0)
						{
							VoxelFace face = new VoxelFace(d, new Vector3(x + pos.x, y + pos.y, z + pos.z), Voxel.getVoxelForId(voxel).getTextureUV(x, y, z, d));
							faces.put(new VoxelFaceKey(x + (int) pos.x, y + (int) pos.y, z + (int) pos.z, d.ordinal()), face);
							// vertexOffset = face.getVertexData(vertices, vertexOffset);
							// vertexOffset = createFace(x, y, z, voxel, vertices, vertexOffset, d);
						}
					}
				}
			}
		}
		
		faces = Mesher.generateGreedyMesh((int) index.x, (int) index.y, (int) index.z, faces);
		for (VoxelFace vf : faces.values())
		{
			vf.getVertexData(opaqueMeshData, vertexOffset);
		}
	}
	
	@Override
	public void mesh()
	{
		if (meshRequest && !meshing)
		{
			meshing = true;
			opaqueMeshData.clear();
			getVertices();// , true);
			int numVerts = opaqueMeshData.size / VERTEX_SIZE;
			opaqueVerts = numVerts / 4 * 6;
			meshRequest = false;
			doneMeshing = true;
		}
	}
}
