package de.dakror.vloxlands.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;

import de.dakror.vloxlands.game.world.Chunk;
import de.dakror.vloxlands.util.Direction;

/**
 * @author Dakror
 */
public class Mesher
{
	public static final Vector3[] directions = { Vector3.X, Vector3.Y, Vector3.Z };
	
	static long millis;
	static int count;
	
	public static <T extends Face<T>> void generateGreedyMesh(int offsetX, int offsetY, int offsetZ, IntMap<T> faces)
	{
		generateGreedyMesh(offsetX, offsetY, offsetZ, Chunk.SIZE, Chunk.SIZE, Chunk.SIZE, faces);
	}
	
	public static <T extends Face<T>> void generateGreedyMesh(int offsetX, int offsetY, int offsetZ, int width, int height, int depth, IntMap<T> faces)
	{
		if (faces.size == 0) return;
		
		for (Vector3 direction : directions)
		{
			IntArray removedByMe = new IntArray();
			
			for (Direction dir : Direction.values())
			{
				if (!canFace(dir, direction)) continue;
				
				T activeFace = null;
				int activeI = 0;
				int activeJ = 0;
				for (int i = 0; i < width; i++)
				{
					for (int j = 0; j < height; j++)
					{
						for (int k = 0; k < depth; k++)
						{
							int x = direction.x == 1 ? k : direction.z == 1 ? i : j;
							int y = direction.y == 1 ? k : direction.z == 1 ? j : i;
							int z = direction.z == 1 ? k : direction.x == 1 ? j : i;
							
							int hash = Face.getHashCode(x + offsetX, y + offsetY, z + offsetZ, dir.ordinal());
							T face = faces.get(hash);
							
							if (face == null)
							{
								if (!removedByMe.contains(hash))
								{
									activeI = -1;
									activeJ = -1;
									activeFace = null;
								}
								continue;
							}
							
							if (activeFace != null && i == activeI && j == activeJ && face.canCombine(activeFace) && face.isSameSize(activeFace, direction))
							{
								activeFace.increaseSize(direction.x * face.sizeX, direction.y * face.sizeY, direction.z * face.sizeZ);
								removedByMe.add(hash);
								faces.remove(hash);
							}
							else
							{
								activeI = i;
								activeJ = j;
								activeFace = face;
							}
						}
					}
				}
			}
		}
	}
	
	public static boolean canFace(Direction dir, Vector3 direction)
	{
		if (direction.x == 1) return dir.dir.x == 0;
		else if (direction.y == 1) return dir.dir.y == 0;
		else return dir.dir.z == 0;
	}
	
	public static Mesh genCube(float size, float texX, float texY, float texSize)
	{
		Mesh mesh = new Mesh(true, 24, 36, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.ColorPacked(), VertexAttribute.TexCoords(0), VertexAttribute.TexCoords(1) /*
																																																																																												 * how
																																																																																												 * many
																																																																																												 * faces
																																																																																												 * together
																																																																																												 * ?
																																																																																												 */);
		
		float[] cubeVerts = { 0, 0, 0, 0, 0, size, size, 0, size, size, 0, 0, 0, size, 0, 0, size, size, size, size, size, size, size, 0, 0, 0, 0, 0, size, 0, size, size, 0, size, 0, 0, 0, 0, size, 0, size, size, size, size, size, size, 0, size, 0, 0, 0, 0, 0, size, 0, size, size, 0, size, 0, size, 0, 0, size, 0, size, size, size, size, size, size, 0, };
		
		float[] cubeNormals = { 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, };
		
		float[] cubeTex = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, };
		
		float b = Color.toFloatBits(1f, 1f, 1f, 1f);
		
		float[] vertices = new float[24 * 11];
		int pIdx = 0;
		int nIdx = 0;
		int tIdx = 0;
		for (int i = 0; i < vertices.length;)
		{
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = b;
			vertices[i++] = cubeTex[tIdx++] * texSize + texX;
			vertices[i++] = cubeTex[tIdx++] * texSize + texY;
			vertices[i++] = 1;
			vertices[i++] = 1;
		}
		
		short[] indices = { 0, 2, 1, 0, 3, 2, 4, 5, 6, 4, 6, 7, 8, 9, 10, 8, 10, 11, 12, 15, 14, 12, 14, 13, 16, 17, 18, 16, 18, 19, 20, 23, 22, 20, 22, 21 };
		
		mesh.setVertices(vertices);
		mesh.setIndices(indices);
		
		return mesh;
	}
	
	public static Mesh genCubeWireframe(float size)
	{
		Mesh mesh = new Mesh(true, 24, 36, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0), VertexAttribute.TexCoords(1) /*
																																																																												 * how
																																																																												 * many
																																																																												 * faces
																																																																												 * together
																																																																												 * ?
																																																																												 */);
		
		float[] cubeVerts = { 0, 0, 0, 0, 0, size, 0, 0, size, 0, size, size, 0, size, size, 0, size, 0, 0, size, 0, 0, 0, 0, size, 0, 0, size, 0, size, size, 0, size, size, size, size, size, size, size, size, size, 0, size, size, 0, size, 0, 0, size, 0, 0, 0, 0, 0, size, size, 0, 0, size, 0, size, 0, size, 0, 0, size, size, size, size, 0, size, size, };
		
		float[] vertices = new float[cubeVerts.length / 3 * 10];
		int pIdx = 0;
		for (int i = 0; i < vertices.length;)
		{
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = 0;
			vertices[i++] = 1;
			vertices[i++] = 0;
			vertices[i++] = 0;
			vertices[i++] = 0;
			vertices[i++] = 1;
			vertices[i++] = 1;
		}
		mesh.setVertices(vertices);
		
		return mesh;
	}
}
