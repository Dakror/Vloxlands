package de.dakror.vloxlands.generate.biome;

import com.badlogic.gdx.math.MathUtils;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.generate.Beziers;
import de.dakror.vloxlands.generate.structure.BezierGenerator;
import de.dakror.vloxlands.generate.structure.CrystalGenerator;
import de.dakror.vloxlands.generate.structure.SpikeGenerator;
import de.dakror.vloxlands.generate.structure.TreeGenerator;

/**
 * @author Dakror
 */
public class ModerateBiome extends Biome
{
	protected int treeMin = 5;
	protected int treeMax = 10;
	protected float spikeFactor = 1;

	@Override
	public void generate(Island island, int radius)
	{
		byte[] tlRatio = createRatio(new byte[] { Voxel.get("DIRT").getId(), Voxel.get("STONE").getId() }, new int[] { 30, 1 });
		int j = (int) (3 + 3 * MathUtils.random() + radius / 8f);
		new BezierGenerator(Island.SIZE / 2, Island.SIZE / 4 * 3, Island.SIZE / 2, radius, j, tlRatio, Beziers.TOPLAYER_BEZIER).generate(island);
		
		byte[] sRatio = createRatio(new byte[] { Voxel.get("STONE").getId(), Voxel.get("DIRT").getId() }, new int[] { 5, 1 });
		for (int k = 0; k < radius * spikeFactor; k++)
			new SpikeGenerator(Island.SIZE / 2, Island.SIZE / 4 * 3, Island.SIZE / 2, radius, j, sRatio).generate(island);

		new TreeGenerator(treeMin, treeMax).generate(island);
		
		new CrystalGenerator(island.pos.y).generate(island);
	}
}
