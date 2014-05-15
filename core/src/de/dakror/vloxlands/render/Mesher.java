package de.dakror.vloxlands.render;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import de.dakror.vloxlands.game.world.Chunk;
import de.dakror.vloxlands.render.Face.FaceKey;
import de.dakror.vloxlands.util.Direction;

/**
 * @author Dakror
 */
public class Mesher
{
	public static final Vector3[] directions = { Vector3.X, Vector3.Y, Vector3.Z };
	
	public static void generateGreedyMesh(int cx, int cy, int cz, ObjectMap<FaceKey, Face> faces)
	{
		if (faces.size == 0) return;
		
		FaceKey faceKey = new FaceKey(0, 0, 0, 0);
		
		
		for (Vector3 direction : directions)
		{
			Array<FaceKey> removedByMe = new Array<FaceKey>();
			for (Direction dir : Direction.values())
			{
				if (!canFace(dir, direction)) continue;
				
				Face activeFace = null;
				int activeI = 0;
				int activeJ = 0;
				for (int i = 0; i < Chunk.SIZE; i++)
				{
					for (int j = 0; j < Chunk.SIZE; j++)
					{
						for (int k = 0; k < Chunk.SIZE; k++)
						{
							int x = direction.x == 1 ? k : direction.z == 1 ? i : j;
							int y = direction.y == 1 ? k : direction.z == 1 ? j : i;
							int z = direction.z == 1 ? k : direction.x == 1 ? j : i;
							
							Face face = faces.get(faceKey.set(x + cx * Chunk.SIZE, y + cy * Chunk.SIZE, z + cz * Chunk.SIZE, dir.ordinal()));
							if (face == null)
							{
								if (!removedByMe.contains(faceKey, false))
								{
									activeI = -1;
									activeJ = -1;
									activeFace = null;
								}
								continue;
							}
							
							if (activeFace != null && i == activeI && j == activeJ && face.tex.equals(activeFace.tex) && face.isSameSize(activeFace, direction))
							{
								activeFace.increaseSize(direction.cpy().scl(face.sizeX, face.sizeY, face.sizeZ));
								removedByMe.add(new FaceKey(0, 0, 0, 0).set(faceKey));
								faces.remove(faceKey);
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
		Mesh mesh = new Mesh(true, 24, 36, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0), VertexAttribute.TexCoords(1) /* how many faces together? */);
		
		float[] cubeVerts = { 0, 0, 0, 0, 0, size, size, 0, size, size, 0, 0, 0, size, 0, 0, size, size, size, size, size, size, size, 0, 0, 0, 0, 0, size, 0, size, size, 0, size, 0, 0, 0, 0, size, 0, size, size, size, size, size, size, 0, size, 0, 0, 0, 0, 0, size, 0, size, size, 0, size, 0, size, 0, 0, size, 0, size, size, size, size, size, size, 0, };
		
		float[] cubeNormals = { 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, };
		
		float[] cubeTex = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, };
		
		float[] vertices = new float[24 * 10];
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
		Mesh mesh = new Mesh(true, 24, 36, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0), VertexAttribute.TexCoords(1) /* how many faces together? */);
		
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
