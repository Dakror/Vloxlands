package de.dakror.vloxlands.generate.biome;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.generate.Beziers;

/**
 * @author Dakror
 */
public class DesertBiome extends Biome
{
	@Override
	public void generate(Island island, int radius)
	{
		int j = (int) (15 + 3 * MathUtils.random() + radius / 8f);
		generateBezier(island, Beziers.BOWL, Island.SIZE / 2, Island.SIZE / 2, radius, Island.SIZE / 4 * 3, j, new byte[] { Voxel.get("SANDSTONE").getId() }, true);
		generateBezier(island, Beziers.BOWL, Island.SIZE / 2, Island.SIZE / 2, radius - 1, Island.SIZE / 4 * 3 + 1, j, new byte[] { Voxel.get("SAND").getId() }, true);
		fillHorizontalCircle(island, Island.SIZE / 2, Island.SIZE / 4 * 3 + 1, Island.SIZE / 2, radius, new byte[] { Voxel.get("AIR").getId() }, true);

		int boulders = MathUtils.random(radius / 4, radius / 2);
		int radiusAt0 = (int) (getHighestBezierValue(Beziers.BOULDER).y * radius);
		for (int i = 0; i < boulders; i++)
		{
			int rad = MathUtils.random(3, 6);
			int height = MathUtils.random(4, 7);
			Vector2 pos = getRandomCircleInCircle(new Vector2(Island.SIZE / 2, Island.SIZE / 2), radiusAt0, rad);
			generateBezier(island, Beziers.BOULDER, (int) pos.x, (int) pos.y, rad, Island.SIZE / 4 * 3 + height / 2 + 1, height, new byte[] { Voxel.get("SANDSTONE").getId() }, true);
		}
		
		byte[] sRatio = createRatio(new byte[] { Voxel.get("SANDSTONE").getId(), Voxel.get("STONE").getId(), Voxel.get("SAND").getId() }, new int[] { 5, 2, 1 });
		for (int k = 0; k < radius / 5; k++)
			generateSpikes(island, Island.SIZE / 2, Island.SIZE / 4 * 3, Island.SIZE / 2, radius, j, sRatio);

		generateCrystals(island);
	}
}
