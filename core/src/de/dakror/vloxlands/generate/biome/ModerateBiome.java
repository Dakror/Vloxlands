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
public class ModerateBiome extends Biome
{
	@Override
	public void generate(Island island, int radius)
	{
		int j = (int) (3 + 3 * MathUtils.random() + radius / 8f);
		new TopLayerGenerator(Island.SIZE / 2, Island.SIZE / 4 * 3, Island.SIZE / 2, radius, j).generate(island);
		for (int k = 0; k < radius; k++)
			new SpikeGenerator(Island.SIZE / 2, Island.SIZE / 4 * 3, Island.SIZE / 2, radius, j).generate(island);

		// clear top layer from stone
		for (int i = 0; i < Island.SIZE; i++)
			for (int k = 0; k < Island.SIZE; k++)
				if (island.get(i, Island.SIZE / 4 * 3, k) == Voxel.get("STONE").getId()) island.set(i, Island.SIZE / 4 * 3, k, Voxel.get("DIRT").getId());

		new CrystalGenerator(island.pos.y).generate(island);
	}
}
