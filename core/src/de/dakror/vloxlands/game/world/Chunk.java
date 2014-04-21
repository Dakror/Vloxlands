package de.dakror.vloxlands.game.world;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.util.Direction;

/**
 * @author Dakror
 */
public class Chunk
{
	public static final int SIZE = 8;
	public static final int VERTEX_SIZE = 8;
	
	public int opaqueVerts;// , transpVerts;
	
	Vector3 index, pos;
	byte[] voxels;
	float weight, uplift;
	
	Mesh opaque;// , transp;
	
	boolean empty;
	boolean updateRequired;
	
	private final int topOffset;
	private final int bottomOffset;
	private final int leftOffset;
	private final int rightOffset;
	private final int frontOffset;
	private final int backOffset;
	
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
		
		topOffset = SIZE;
		bottomOffset = -SIZE;
		leftOffset = -SIZE * SIZE;
		rightOffset = SIZE * SIZE;
		frontOffset = -1;
		backOffset = 1;
		
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
		
		opaque = new Mesh(true, SIZE * SIZE * SIZE * 6 * 4, SIZE * SIZE * SIZE * 36 / 3, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
		opaque.setIndices(indices);
		
		// transp = new Mesh(true, SIZE * SIZE * SIZE * 6 * 4, SIZE * SIZE * SIZE * 36 / 3, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
		// transp.setIndices(indices);
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
	
	public void updateMeshes(float[] opaqueMeshData)// , float[] transpMeshData)
	{
		if (!updateRequired) return;
		
		empty = getVoxelCount() == 0;
		
		int numVerts = calculateVertices(opaqueMeshData);// , true);
		opaqueVerts = numVerts / 4 * 6;
		opaque.setVertices(opaqueMeshData, 0, numVerts * VERTEX_SIZE);
		
		// numVerts = calculateVertices(transpMeshData, false);
		// transpVerts = numVerts / 4 * 6;
		// transp.setVertices(transpMeshData, 0, numVerts * VERTEX_SIZE);
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
	
	public int calculateVertices(float[] vertices)// , boolean opaque)
	{
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
					
					// if (Voxel.getVoxelForId(voxel).isOpaque() != opaque)
					// {
					// i++;
					// continue;
					// }
					
					if (y < SIZE - 1)
					{
						if (voxels[i + topOffset] == 0) vertexOffset = createUp(x, y, z, voxel, vertices, vertexOffset);
					}
					else vertexOffset = createUp(x, y, z, voxel, vertices, vertexOffset);
					
					if (y > 0)
					{
						if (voxels[i + bottomOffset] == 0) vertexOffset = createDown(x, y, z, voxel, vertices, vertexOffset);
					}
					else vertexOffset = createDown(x, y, z, voxel, vertices, vertexOffset);
					
					if (x > 0)
					{
						if (voxels[i + leftOffset] == 0) vertexOffset = createWest(x, y, z, voxel, vertices, vertexOffset);
					}
					else vertexOffset = createWest(x, y, z, voxel, vertices, vertexOffset);
					
					if (x < SIZE - 1)
					{
						if (voxels[i + rightOffset] == 0) vertexOffset = createEast(x, y, z, voxel, vertices, vertexOffset);
					}
					else vertexOffset = createEast(x, y, z, voxel, vertices, vertexOffset);
					
					if (z > 0)
					{
						if (voxels[i + frontOffset] == 0) vertexOffset = createNorth(x, y, z, voxel, vertices, vertexOffset);
					}
					else vertexOffset = createNorth(x, y, z, voxel, vertices, vertexOffset);
					
					if (z < SIZE - 1)
					{
						if (voxels[i + backOffset] == 0) vertexOffset = createSouth(x, y, z, voxel, vertices, vertexOffset);
					}
					else vertexOffset = createSouth(x, y, z, voxel, vertices, vertexOffset);
				}
			}
		}
		return vertexOffset / VERTEX_SIZE;
	}
	
	public int createUp(int x, int y, int z, byte id, float[] vertices, int vertexOffset)
	{
		tex = Voxel.getVoxelForId(id).getTextureUV(x, y, z, Direction.UP);
		
		vertices[vertexOffset++] = pos.x + x;
		vertices[vertexOffset++] = pos.y + y + 1;
		vertices[vertexOffset++] = pos.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = tex.x;
		vertices[vertexOffset++] = tex.y;
		
		vertices[vertexOffset++] = pos.x + x + 1;
		vertices[vertexOffset++] = pos.y + y + 1;
		vertices[vertexOffset++] = pos.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = tex.x + Voxel.TEXSIZE;
		vertices[vertexOffset++] = tex.y;
		
		vertices[vertexOffset++] = pos.x + x + 1;
		vertices[vertexOffset++] = pos.y + y + 1;
		vertices[vertexOffset++] = pos.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = tex.x + Voxel.TEXSIZE;
		vertices[vertexOffset++] = tex.y + Voxel.TEXSIZE;
		
		vertices[vertexOffset++] = pos.x + x;
		vertices[vertexOffset++] = pos.y + y + 1;
		vertices[vertexOffset++] = pos.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = tex.x;
		vertices[vertexOffset++] = tex.y + Voxel.TEXSIZE;
		return vertexOffset;
	}
	
	public int createDown(int x, int y, int z, byte id, float[] vertices, int vertexOffset)
	{
		tex = Voxel.getVoxelForId(id).getTextureUV(x, y, z, Direction.DOWN);
		
		vertices[vertexOffset++] = pos.x + x;
		vertices[vertexOffset++] = pos.y + y;
		vertices[vertexOffset++] = pos.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = tex.x;
		vertices[vertexOffset++] = tex.y;
		
		vertices[vertexOffset++] = pos.x + x;
		vertices[vertexOffset++] = pos.y + y;
		vertices[vertexOffset++] = pos.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = tex.x;
		vertices[vertexOffset++] = tex.y + Voxel.TEXSIZE;
		
		vertices[vertexOffset++] = pos.x + x + 1;
		vertices[vertexOffset++] = pos.y + y;
		vertices[vertexOffset++] = pos.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = tex.x + Voxel.TEXSIZE;
		vertices[vertexOffset++] = tex.y + Voxel.TEXSIZE;
		
		vertices[vertexOffset++] = pos.x + x + 1;
		vertices[vertexOffset++] = pos.y + y;
		vertices[vertexOffset++] = pos.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = tex.x + Voxel.TEXSIZE;
		vertices[vertexOffset++] = tex.y;
		return vertexOffset;
	}
	
	public int createWest(int x, int y, int z, byte id, float[] vertices, int vertexOffset)
	{
		tex = Voxel.getVoxelForId(id).getTextureUV(x, y, z, Direction.WEST);
		
		vertices[vertexOffset++] = pos.x + x;
		vertices[vertexOffset++] = pos.y + y;
		vertices[vertexOffset++] = pos.z + z;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = tex.x;
		vertices[vertexOffset++] = tex.y;
		
		vertices[vertexOffset++] = pos.x + x;
		vertices[vertexOffset++] = pos.y + y + 1;
		vertices[vertexOffset++] = pos.z + z;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = tex.x;
		vertices[vertexOffset++] = tex.y + Voxel.TEXSIZE;
		
		vertices[vertexOffset++] = pos.x + x;
		vertices[vertexOffset++] = pos.y + y + 1;
		vertices[vertexOffset++] = pos.z + z + 1;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = tex.x + Voxel.TEXSIZE;
		vertices[vertexOffset++] = tex.y + Voxel.TEXSIZE;
		
		vertices[vertexOffset++] = pos.x + x;
		vertices[vertexOffset++] = pos.y + y;
		vertices[vertexOffset++] = pos.z + z + 1;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = tex.x + Voxel.TEXSIZE;
		vertices[vertexOffset++] = tex.y;
		return vertexOffset;
	}
	
	public int createEast(int x, int y, int z, byte id, float[] vertices, int vertexOffset)
	{
		tex = Voxel.getVoxelForId(id).getTextureUV(x, y, z, Direction.EAST);
		
		vertices[vertexOffset++] = pos.x + x + 1;
		vertices[vertexOffset++] = pos.y + y;
		vertices[vertexOffset++] = pos.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = tex.x;
		vertices[vertexOffset++] = tex.y;
		
		vertices[vertexOffset++] = pos.x + x + 1;
		vertices[vertexOffset++] = pos.y + y;
		vertices[vertexOffset++] = pos.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = tex.x + Voxel.TEXSIZE;
		vertices[vertexOffset++] = tex.y;
		
		vertices[vertexOffset++] = pos.x + x + 1;
		vertices[vertexOffset++] = pos.y + y + 1;
		vertices[vertexOffset++] = pos.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = tex.x + Voxel.TEXSIZE;
		vertices[vertexOffset++] = tex.y + Voxel.TEXSIZE;
		
		vertices[vertexOffset++] = pos.x + x + 1;
		vertices[vertexOffset++] = pos.y + y + 1;
		vertices[vertexOffset++] = pos.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = tex.x + Voxel.TEXSIZE;
		vertices[vertexOffset++] = tex.y;
		return vertexOffset;
	}
	
	public int createNorth(int x, int y, int z, byte id, float[] vertices, int vertexOffset)
	{
		tex = Voxel.getVoxelForId(id).getTextureUV(x, y, z, Direction.NORTH);
		
		vertices[vertexOffset++] = pos.x + x;
		vertices[vertexOffset++] = pos.y + y;
		vertices[vertexOffset++] = pos.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = tex.x;
		vertices[vertexOffset++] = tex.y;
		
		vertices[vertexOffset++] = pos.x + x + 1;
		vertices[vertexOffset++] = pos.y + y;
		vertices[vertexOffset++] = pos.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = tex.x + Voxel.TEXSIZE;
		vertices[vertexOffset++] = tex.y;
		
		vertices[vertexOffset++] = pos.x + x + 1;
		vertices[vertexOffset++] = pos.y + y + 1;
		vertices[vertexOffset++] = pos.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = tex.x + Voxel.TEXSIZE;
		vertices[vertexOffset++] = tex.y + Voxel.TEXSIZE;
		
		vertices[vertexOffset++] = pos.x + x;
		vertices[vertexOffset++] = pos.y + y + 1;
		vertices[vertexOffset++] = pos.z + z;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 1;
		vertices[vertexOffset++] = tex.x;
		vertices[vertexOffset++] = tex.y + Voxel.TEXSIZE;
		return vertexOffset;
	}
	
	public int createSouth(int x, int y, int z, byte id, float[] vertices, int vertexOffset)
	{
		tex = Voxel.getVoxelForId(id).getTextureUV(x, y, z, Direction.SOUTH);
		
		vertices[vertexOffset++] = pos.x + x;
		vertices[vertexOffset++] = pos.y + y;
		vertices[vertexOffset++] = pos.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = tex.x;
		vertices[vertexOffset++] = tex.y;
		
		vertices[vertexOffset++] = pos.x + x;
		vertices[vertexOffset++] = pos.y + y + 1;
		vertices[vertexOffset++] = pos.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = tex.x;
		vertices[vertexOffset++] = tex.y + Voxel.TEXSIZE;
		
		vertices[vertexOffset++] = pos.x + x + 1;
		vertices[vertexOffset++] = pos.y + y + 1;
		vertices[vertexOffset++] = pos.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = tex.x + Voxel.TEXSIZE;
		vertices[vertexOffset++] = tex.y + Voxel.TEXSIZE;
		
		vertices[vertexOffset++] = pos.x + x + 1;
		vertices[vertexOffset++] = pos.y + y;
		vertices[vertexOffset++] = pos.z + z + 1;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = tex.x + Voxel.TEXSIZE;
		vertices[vertexOffset++] = tex.y;
		return vertexOffset;
	}
}
