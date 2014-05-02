package de.dakror.vloxlands.render;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap;

import de.dakror.vloxlands.game.world.Chunk;
import de.dakror.vloxlands.render.Face.FaceKey;
import de.dakror.vloxlands.util.Direction;

/**
 * @author Dakror
 */
public class Mesher
{
	public static ObjectMap<FaceKey, Face> generateGreedyMesh(int cx, int cy, int cz, ObjectMap<FaceKey, Face> originalMap)
	{
		// TODO rework algorithm
		ObjectMap<FaceKey, Face> strips0 = new ObjectMap<FaceKey, Face>();
		
		if (originalMap.size == 0) return originalMap;
		
		// greedy-mode along Z - axis
		for (int x = 0; x < Chunk.SIZE; x++)
		{
			for (int y = 0; y < Chunk.SIZE; y++)
			{
				Face[] activeStrips = new Face[Direction.values().length];
				for (int z = 0; z < Chunk.SIZE; z++)
				{
					for (int i = 0; i < activeStrips.length; i++)
					{
						
						int posX = cx * Chunk.SIZE + x;
						int posY = cy * Chunk.SIZE + y;
						int posZ = cz * Chunk.SIZE + z;
						
						FaceKey key = new FaceKey(posX, posY, posZ, i);
						Face val = originalMap.get(key);
						
						if (activeStrips[i] != null)
						{
							if (val == null)
							{
								strips0.put(new FaceKey(activeStrips[i]), activeStrips[i]);
								activeStrips[i] = null;
							}
							else if (val.tex.equals(activeStrips[i].tex))
							{
								activeStrips[i].increaseSize(0, 0, 1);
							}
							else
							{
								strips0.put(new FaceKey(activeStrips[i]), activeStrips[i]);
								activeStrips[i] = new Face(Direction.values()[i], new Vector3(posX, posY, posZ), val.tex.cpy());
							}
						}
						else if (val != null)
						{
							activeStrips[i] = new Face(Direction.values()[i], new Vector3(posX, posY, posZ), val.tex.cpy());
						}
					}
				}
				for (int i = 0; i < activeStrips.length; i++)
					if (activeStrips[i] != null) strips0.put(new FaceKey(activeStrips[i]), activeStrips[i]);
			}
		}
		
		ObjectMap<FaceKey, Face> strips1 = new ObjectMap<FaceKey, Face>();
		
		// greedy-mode along X - axis
		for (int y = 0; y < Chunk.SIZE; y++)
		{
			Face[] activeStrips = new Face[Direction.values().length];
			for (int z = 0; z < Chunk.SIZE; z++)
			{
				for (int x = 0; x < Chunk.SIZE; x++)
				{
					for (int i = 0; i < activeStrips.length; i++)
					{
						int posX = cx * Chunk.SIZE + x;
						int posY = cy * Chunk.SIZE + y;
						int posZ = cz * Chunk.SIZE + z;
						
						FaceKey key = new FaceKey(posX, posY, posZ, i);
						Face val = strips0.get(key);
						
						if (val != null)
						{
							if (activeStrips[i] == null)
							{
								activeStrips[i] = new Face(val);
							}
							else
							{
								if (val.tex.equals(activeStrips[i].tex) && val.sizeZ == activeStrips[i].sizeZ && val.pos.z == activeStrips[i].pos.z)
								{
									activeStrips[i].increaseSize(1, 0, 0);
								}
								else
								{
									strips1.put(new FaceKey(activeStrips[i]), activeStrips[i]);
									
									activeStrips[i] = new Face(val);
								}
							}
						}
						else if (activeStrips[i] != null)
						{
							strips1.put(new FaceKey(activeStrips[i]), activeStrips[i]);
							activeStrips[i] = null;
						}
					}
				}
			}
			for (int i = 0; i < activeStrips.length; i++)
				if (activeStrips[i] != null) strips1.put(new FaceKey(activeStrips[i]), activeStrips[i]);
		}
		
		ObjectMap<FaceKey, Face> strips2 = new ObjectMap<FaceKey, Face>();
		
		// greedy-mode along Y - axis
		for (int x = 0; x < Chunk.SIZE; x++)
		{
			Face[] activeStrips = new Face[Direction.values().length];
			for (int z = 0; z < Chunk.SIZE; z++)
			{
				for (int y = 0; y < Chunk.SIZE; y++)
				{
					for (int i = 0; i < activeStrips.length; i++)
					{
						int posX = cx * Chunk.SIZE + x;
						int posY = cy * Chunk.SIZE + y;
						int posZ = cz * Chunk.SIZE + z;
						
						FaceKey key = new FaceKey(posX, posY, posZ, i);
						Face val = strips1.get(key);
						
						if (val != null)
						{
							if (activeStrips[i] == null)
							{
								activeStrips[i] = new Face(val);
							}
							else
							{
								if (val.tex.equals(activeStrips[i].tex) && val.sizeZ == activeStrips[i].sizeZ && val.sizeX == activeStrips[i].sizeX && val.pos.x == activeStrips[i].pos.x && val.pos.z == activeStrips[i].pos.z)
								{
									activeStrips[i].increaseSize(0, 1, 0);
								}
								else
								{
									strips2.put(new FaceKey(activeStrips[i]), activeStrips[i]);
									
									activeStrips[i] = new Face(val);
								}
							}
						}
						else if (activeStrips[i] != null)
						{
							strips2.put(new FaceKey(activeStrips[i]), activeStrips[i]);
							activeStrips[i] = null;
						}
					}
				}
			}
			for (int i = 0; i < activeStrips.length; i++)
				if (activeStrips[i] != null) strips2.put(new FaceKey(activeStrips[i]), activeStrips[i]);
		}
		
		return strips2;
	}
	
	public static Mesh genCube(float size)
	{
		Mesh mesh = new Mesh(true, 24, 36, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0), VertexAttribute.TexCoords(1) /* how many faces together? */);
		
		float[] cubeVerts = { 0, 0, 0, 0, 0, size, size, 0, size, size, 0, 0, 0, size, 0, 0, size, size, size, size, size, size, size, 0, 0, 0, 0, 0, size, 0, size, size, 0, size, 0, 0, 0, 0, size, 0, size, size, size, size, size, size, 0, size, 0, 0, 0, 0, 0, size, 0, size, size, 0, size, 0, size, 0, 0, size, 0, size, size, size, size, size, size, 0, };
		
		float[] cubeNormals = { 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, };
		
		float[] vertices = new float[24 * 10];
		int pIdx = 0;
		int nIdx = 0;
		for (int i = 0; i < vertices.length;)
		{
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = 0;
			vertices[i++] = 0;
			vertices[i++] = 1;
			vertices[i++] = 1;
		}
		
		short[] indices = { 0, 2, 1, 0, 3, 2, 4, 5, 6, 4, 6, 7, 8, 9, 10, 8, 10, 11, 12, 15, 14, 12, 14, 13, 16, 17, 18, 16, 18, 19, 20, 23, 22, 20, 22, 21 };
		
		mesh.setVertices(vertices);
		mesh.setIndices(indices);
		
		return mesh;
	}
}
