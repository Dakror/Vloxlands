package de.dakror.vloxlands.generate;

import java.io.ByteArrayOutputStream;

import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.game.query.VoxelPos;
import de.dakror.vloxlands.game.query.VoxelStats;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Chunk;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.game.world.World;

public abstract class Generator
{
	public abstract void generate(WorldGenerator worldGen, Island island);
	
	public static Array<Byte> getNaturalTypes()
	{
		Array<Byte> naturalVoxels = new Array<Byte>();
		naturalVoxels.add(Voxel.get("SANDSTONE").getId());
		naturalVoxels.add(Voxel.get("STONE").getId());
		naturalVoxels.add(Voxel.get("DIRT").getId());
		return naturalVoxels;
	}
	
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
		int rad = (int) Math.ceil(radius);
		for (int i = -rad; i <= rad; i++)
		{
			for (int j = -rad; j <= rad; j++)
			{
				int x1 = x + i;
				int z1 = z + j;
				if (Vector2.dst(i, j, 0, 0) < radius) island.set(x1, y, z1, b[((int) (MathUtils.random() * b.length))], force, false);
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
		if (c == null) return false;
		
		for (byte b : getNaturalTypes())
			if (c.getResource(b) > 0) return true;
		
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
		Array<Byte> naturalTypes = getNaturalTypes();
		
		Vector3 c = pickRandomNaturalChunk(island);
		Chunk chunk = island.getChunk(c.x, c.y, c.z);
		
		Array<Vector3> chunkVoxels = new Array<Vector3>();
		
		for (int i = 0; i < Chunk.SIZE; i++)
		{
			for (int j = 0; j < Chunk.SIZE; j++)
			{
				for (int k = 0; k < Chunk.SIZE; k++)
				{
					if (chunkVoxels.size > 50) break;
					
					byte id = chunk.get(i, j, k);
					if (naturalTypes.contains(id, false)) chunkVoxels.add(new Vector3(i + c.x * Chunk.SIZE, j + c.y * Chunk.SIZE, k + c.z * Chunk.SIZE));
				}
			}
		}
		
		return chunkVoxels.get((int) (MathUtils.random() * chunkVoxels.size));
	}
	
	public static VoxelStats generateVein(Island island, VoxelStats maximum, float min, float max, byte[] b)
	{
		int size = (int) MathUtils.random(min, max);
		
		Vector3 c = pickRandomNaturalVoxel(island);
		VoxelStats vs = new VoxelStats();
		
		float maxDistance = (float) (size * Math.sqrt(3)) / 2;
		
		for (int i = (int) (c.x - size * .5f); i < c.x + size * .5f; i++)
		{
			for (int j = (int) (c.y - size * .5f); j < c.y + size * .5f; j++)
			{
				for (int k = (int) (c.z - size * .5f); k < c.z + size * .5f; k++)
				{
					if (MathUtils.random() * maxDistance > Vector3.dst(i, j, k, c.x, c.y, c.z))
					{
						byte bv = b[(int) (MathUtils.random() * b.length)];
						Voxel v = Voxel.getForId(bv);
						if (vs.uplift + v.getUplift() >= maximum.uplift && maximum.weight > 0) return vs;
						if (vs.weight + v.getWeight() >= maximum.weight && maximum.weight > 0) return vs;
						
						
						vs.uplift += v.getUplift();
						vs.weight += v.getWeight();
						
						byte b2 = island.get(i, j, k);
						if (b2 != Voxel.get("AIR").getId()) vs.uplift += Voxel.getForId(b2).getWeight(); // balances uplift-weight in case of v.getUplift() > 0
						
						island.set(i, j, k, bv, true, false);
					}
				}
			}
		}
		
		return vs;
	}
	
	// -- structures -- //
	
	public static void generateTrees(Island island, int min, int max)
	{
		int width = MathUtils.random(min, max);
		int depth = MathUtils.random(min, max);
		int size = MathUtils.ceil(Island.SIZE / (float) Math.min(width, depth));
		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < depth; j++)
			{
				int x = i * size + MathUtils.random(-size / 2, size / 2);
				int z = j * size + MathUtils.random(-size / 2, size / 2);
				
				VoxelPos vp = island.getHighestVoxel(x, z);
				if (vp.y <= 0 || vp.b != Voxel.get("DIRT").getId()) continue;
				
				generateTree(island, x, vp.y, z);
			}
		}
	}
	
	/**
	 * @param y the block below the tree
	 */
	public static void generateTree(Island island, int x, int y, int z)
	
	{
		int height = (int) (MathUtils.random() * 5 + 5);
		
		byte wood = Voxel.get("WOOD").getId();
		
		for (int k = 0; k < height; k++)
			island.set(x, y + 1 + k, z, wood, true, false);
		
		generateBezier(island, Beziers.TREE, x, z, 5, (int) (y + 1 + height * 1.5f), (int) (height * 1.4f), new byte[] { Voxel.get("LEAVES").getId() }, false);
	}
	
	public static void generateSpikes(Island island, int amount, int y, int radius, int topLayers, byte[] ratio)
	{
		for (int i = 0; i < amount; i++)
		{
			int MAXRAD = (int) (radius * 0.3f + 5);
			int rad = Math.round(MathUtils.random() * (radius * 0.3f)) + 3;
			
			Vector2 highest = getHighestBezierValue(Beziers.TOPLAYER);
			int radiusAt0 = (int) (highest.y * radius);
			
			Vector2 m = new Vector2(Island.SIZE / 2, Island.SIZE / 2);
			
			Vector2 pos = getRandomCircleInCircle(m, radiusAt0, rad);
			
			int h = (int) (0.3f * ((MAXRAD - rad) * (radiusAt0 - pos.cpy().sub(m).len()) + topLayers));
			h = Math.min(h, Island.SIZE - topLayers - 10);
			
			island.set((int) pos.x, -1 + y, (int) pos.y, Voxel.get("STONE").getId(), true, false);
			
			generateBezier(island, Beziers.SPIKE, (int) pos.x, (int) pos.y /* Z */, rad, (int) (y - highest.x * topLayers), h, ratio, false);
		}
	}
	
	public static void generateBoulders(Island island, int y, int radius, int min, int max, int minRad, int maxRad, int minHeight, int maxHeight, byte[] b)
	{
		int boulders = MathUtils.random(min, max);
		int highest = (int) (getHighestBezierValue(Beziers.BOULDER).y * radius);
		for (int i = 0; i < boulders; i++)
		{
			int rad = MathUtils.random(minRad, maxRad);
			int height = MathUtils.random(minHeight, maxHeight);
			Vector2 pos = getRandomCircleInCircle(new Vector2(Island.SIZE / 2, Island.SIZE / 2), highest, rad);
			generateBezier(island, Beziers.BOULDER, (int) pos.x, (int) pos.y, rad, y + height / 2 + 1, height, b, true);
		}
	}
	
	public static void generateCrystals(Island island)
	{
		final Voxel[] CRYSTALS = { Voxel.get("BLUE_CRYSTAL"), Voxel.get("RED_CRYSTAL"), Voxel.get("YELLOW_CRYSTAL") };
		
		island.calculateWeight();
		
		float weightNeededToUplift = island.getWeight() / World.calculateRelativeUplift(island.pos.y);
		
		while (weightNeededToUplift > 100)
		{
			int index = (int) (MathUtils.random() * CRYSTALS.length);
			weightNeededToUplift -= generateVein(island, new VoxelStats(0, weightNeededToUplift), index + 1, index + 4, new byte[] { CRYSTALS[index].getId() }).uplift;
		}
		
		int[] amounts = new int[CRYSTALS.length];
		for (int i = 0; i < amounts.length; i++)
		{
			amounts[i] = (int) (weightNeededToUplift / CRYSTALS[i].getUplift());
			weightNeededToUplift %= CRYSTALS[i].getUplift();
		}
		
		for (int j = 0; j < amounts.length; j++)
		{
			for (int i = 0; i < amounts[j]; i++)
			{
				Vector3 v = pickRandomNaturalVoxel(island);
				island.set((int) v.x, (int) v.y, (int) v.z, CRYSTALS[j].getId(), true, false);
			}
		}
	}
}
