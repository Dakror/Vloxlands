package de.dakror.vloxlands.generate.biome;

import com.badlogic.gdx.math.MathUtils;

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
		
		generateBoulders(island, Island.SIZE / 4 * 3, radius, radius / 4, radius / 2, 3, 6, 4, 7, new byte[] { Voxel.get("SANDSTONE").getId() });
		
		byte[] sRatio = createRatio(new byte[] { Voxel.get("SANDSTONE").getId(), Voxel.get("STONE").getId(), Voxel.get("SAND").getId() }, new int[] { 5, 2, 1 });
		generateSpikes(island, radius / 5, Island.SIZE / 4 * 3, radius, j, sRatio);
		
		generateCrystals(island);
	}
}
