package de.dakror.vloxlands.generate.structure;

import com.badlogic.gdx.math.MathUtils;

import de.dakror.vloxlands.game.query.VoxelPos;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.generate.Generator;

/**
 * @author Dakror
 */
public class TreeGenerator extends Generator
{
	public static final float[] BEZIER = new float[] { 1, 0, 1, 1, 0.5f, 0.7f, 0, 0 };
	int min, max;
	
	public TreeGenerator(int min, int max)
	{
		this.min = min;
		this.max = max;
	}
	
	@Override
	public void generate(Island island)
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
				
				int height = (int) (Math.random() * 5 + 5);
				
				byte wood = Voxel.get("WOOD").getId();
				
				for (int k = 0; k < height; k++)
					island.set(x, vp.y + 1 + k, z, wood);
				
				generateBezier(island, BEZIER, x, z, 5, (int) (vp.y + 1 + height * 1.5f), (int) (height * 1.4f), new byte[] { Voxel.get("LEAVES").getId() }, false);
			}
		}
	}
}
