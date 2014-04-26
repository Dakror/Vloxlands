package de.dakror.vloxlands.game.entity;

import java.util.UUID;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;

import de.dakror.vloxlands.game.world.Chunk;
import de.dakror.vloxlands.util.Tickable;

/**
 * @author Dakror
 */
public abstract class Entity implements Tickable
{
	public static int MAX_VERTICES = 256;
	FloatArray meshData;
	
	Vector3 pos;
	Vector3 size;
	Vector3 direction;
	
	Mesh mesh;
	
	UUID uuid;
	String name;
	
	float velocity;
	float weight;
	float uplift;
	
	public int verts;
	
	public Entity(float width, float height, float depth, String name)
	{
		uuid = UUID.randomUUID();
		size = new Vector3(width, height, depth);
		this.name = name;
		pos = new Vector3();
		direction = new Vector3();
		
		meshData = new FloatArray();
		
		int len = MAX_VERTICES * 6 * 6 / 3;
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
		
		mesh = new Mesh(true, MAX_VERTICES * 6 * 4, MAX_VERTICES * 36 / 3, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0), VertexAttribute.TexCoords(1) /* how many faces together? */);
		mesh.setIndices(indices);
	}
	
	public Vector3 getDirection()
	{
		return direction;
	}
	
	public void setDirection(Vector3 direction)
	{
		this.direction = direction;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public float getVelocity()
	{
		return velocity;
	}
	
	public void setVelocity(float velocity)
	{
		this.velocity = velocity;
	}
	
	public float getWeight()
	{
		return weight;
	}
	
	public void setWeight(float weight)
	{
		this.weight = weight;
	}
	
	public float getUplift()
	{
		return uplift;
	}
	
	public void setUplift(float uplift)
	{
		this.uplift = uplift;
	}
	
	public Vector3 getPos()
	{
		return pos;
	}
	
	public Vector3 getSize()
	{
		return size;
	}
	
	public UUID getUUID()
	{
		return uuid;
	}
	
	public Mesh getMesh()
	{
		if (meshData.size == 0)
		{
			getVertices(meshData);
			verts = meshData.size / Chunk.VERTEX_SIZE / 4 * 6;
			mesh.setVertices(meshData.items, 0, meshData.size);
		}
		
		return mesh;
	}
	
	protected abstract void getVertices(FloatArray f);
}
