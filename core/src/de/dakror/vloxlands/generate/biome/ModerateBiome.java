package de.dakror.vloxlands.generate.biome;

import com.badlogic.gdx.math.MathUtils;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.generate.Beziers;

/**
 * @author Dakror
 */
public class ModerateBiome extends Biome
{
	protected int treeMin = 5;
	protected int treeMax = 10;
	protected int boulderMin = 5;
	protected int boulderMax = 10;
	protected float spikeFactor = 1;

	@Override
	public void generate(Island island, int radius)
	{
		byte[] tlRatio = createRatio(new byte[] { Voxel.get("DIRT").getId(), Voxel.get("STONE").getId() }, new int[] { 30, 1 });
		int j = (int) (3 + 3 * MathUtils.random() + radius / 8f);
		generateBezier(island, Beziers.TOPLAYER, Island.SIZE / 2, Island.SIZE / 2, radius, Island.SIZE / 4 * 3, j, tlRatio, true);
		
		byte[] sRatio = createRatio(new byte[] { Voxel.get("STONE").getId(), Voxel.get("DIRT").getId() }, new int[] { 5, 1 });
		generateSpikes(island, (int) (radius * spikeFactor), Island.SIZE / 4 * 3, radius, j, sRatio);

		generateTrees(island, treeMin, treeMax);
		generateBoulders(island, Island.SIZE / 4 * 3, radius, boulderMin, boulderMax, 3, 6, 4, 7, new byte[] { Voxel.get("STONE").getId() });

		generateCrystals(island);
	}
}
