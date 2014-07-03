package de.dakror.vloxlands.generate.biome;

import com.badlogic.gdx.math.MathUtils;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.generate.structure.CrystalGenerator;
import de.dakror.vloxlands.generate.structure.SpikeGenerator;
import de.dakror.vloxlands.generate.structure.TopLayerGenerator;

/**
 * @author Dakror
 */
public class DesertBiome extends Biome
{
	@Override
	public void generate(Island island, int radius)
	{
		byte[] tlRatio = createRatio(new byte[] { Voxel.get("SAND").getId(), Voxel.get("SANDSTONE").getId() }, new int[] { 10, 1 });
		int j = (int) (3 + 3 * MathUtils.random() + radius / 8f);
		new TopLayerGenerator(Island.SIZE / 2, Island.SIZE / 4 * 3, Island.SIZE / 2, radius, j, tlRatio).generate(island);
		
		byte[] sRatio = createRatio(new byte[] { Voxel.get("SANDSTONE").getId(), Voxel.get("STONE").getId(), Voxel.get("SAND").getId() }, new int[] { 5, 2, 1 });
		for (int k = 0; k < radius / 5; k++)
			new SpikeGenerator(Island.SIZE / 2, Island.SIZE / 4 * 3, Island.SIZE / 2, radius, j, sRatio).generate(island);

		new CrystalGenerator(island.pos.y).generate(island);
	}
}
