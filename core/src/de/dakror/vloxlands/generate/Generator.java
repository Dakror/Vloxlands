package de.dakror.vloxlands.generate;

import java.io.ByteArrayOutputStream;

import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Chunk;
import de.dakror.vloxlands.game.world.Island;

public abstract class Generator
{
	public static byte[] createRatio(byte[] keys, int[] vals)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i = 0; i < keys.length; i++)
		{
			for (int j = 0; j < vals[i]; j++)
			{
				baos.write(keys[i]);
			}
		}
		return baos.toByteArray();
	}
	
	public static void fillHorizontalCircle(Island island, int x, int y, int z, float radius, byte[] b, boolean force)
	{
		for (int i = 0; i < Island.SIZE; i++) // x axis
		{
			for (int j = 0; j < Island.SIZE; j++) // z axis
			{
				if (Vector2.dst(i, j, x, z) < radius) island.set(i, y, j, b[((int) (MathUtils.random() * b.length))], force);
			}
		}
	}
	
	public static void generateBezier(Island island, float[] c, int x, int z, int radius, int off, int h, byte[] b, boolean force)
	{
		Vector2 p0 = new Vector2(c[0], c[1]);
		Vector2 p1 = new Vector2(c[2], c[3]);
		Vector2 p2 = new Vector2(c[4], c[5]);
		Vector2 p3 = new Vector2(c[6], c[7]);
		for (int i = 0; i < h; i++)
		{
			float t = i / (float) h;
			
			float rad = (float) Math.floor(radius * Bezier.cubic(new Vector2(), t, p0, p1, p2, p3, new Vector2()).y);
			fillHorizontalCircle(island, x, off - i, z, rad, b, force);
		}
	}
	
	public static Vector2 getHighestBezierValue(float[] c)
	{
		Vector2 p0 = new Vector2(c[0], c[1]);
		Vector2 p1 = new Vector2(c[2], c[3]);
		Vector2 p2 = new Vector2(c[4], c[5]);
		Vector2 p3 = new Vector2(c[6], c[7]);
		float y = 0;
		float x = 0;
		for (float i = 0; i < 1; i += 0.01f)
		{
			Vector2 v = Bezier.cubic(new Vector2(), i, p0, p1, p2, p3, new Vector2());
			if (v.y > y)
			{
				x = i;
				y = v.y;
			}
		}
		
		return new Vector2(x, y);
	}
	
	public static Vector2 getRandomCircleInCircle(Vector2 center, int radius, int rad2)
	{
		Vector2 v = new Vector2();
		do
			v = new Vector2(Math.round(MathUtils.random() * radius * 2 - radius + center.x), Math.round(MathUtils.random() * radius * 2 - radius + center.y));
		while (v.dst(center) > radius - rad2);
		
		return v;
	}
	
	public static boolean hasNaturalVoxel(Chunk c)
	{
		Array<Voxel> naturalVoxels = new Array<Voxel>();
		naturalVoxels.add(Voxel.get("STONE"));
		naturalVoxels.add(Voxel.get("DIRT"));
		
		for (Voxel b : naturalVoxels)
			if (c.getResource(b.getId()) > 0) return true;
		
		return false;
	}
	
	public static Vector3 pickRandomNaturalChunk(Island island)
	{
		int i = 0;
		int chunks = island.getChunks().length;
		
		do
			i = MathUtils.random(chunks - 1);
		while (island.getChunk(i) == null || !hasNaturalVoxel(island.getChunk(i)));
		
		return island.getChunk(i).index;
	}
	
	public static Vector3 pickRandomNaturalVoxel(Island island)
	{
		Array<Byte> naturalVoxels = new Array<Byte>();
		naturalVoxels.add(Voxel.get("STONE").getId());
		naturalVoxels.add(Voxel.get("DIRT").getId());
		
		Vector3 c = pickRandomNaturalChunk(island);
		Chunk chunk = island.getChunk(c.x, c.y, c.z);
		
		Array<Vector3> chunkVoxels = new Array<Vector3>();
		
		for (int i = 0; i < Chunk.SIZE; i++)
		{
			for (int j = 0; j < Chunk.SIZE; j++)
			{
				for (int k = 0; k < Chunk.SIZE; k++)
				{
					byte id = chunk.get(i, j, k);
					if (naturalVoxels.contains(id, false)) chunkVoxels.add(new Vector3(i + c.x * Chunk.SIZE, j + c.y * Chunk.SIZE, k + c.z * Chunk.SIZE));
				}
			}
		}
		
		return chunkVoxels.get((int) (MathUtils.random() * chunkVoxels.size));
	}
	
	public abstract void generate(Island island);
}
