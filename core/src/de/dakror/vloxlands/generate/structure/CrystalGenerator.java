package de.dakror.vloxlands.generate.structure;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.game.world.World;
import de.dakror.vloxlands.generate.Generator;

/**
 * @author Dakror
 */
public class CrystalGenerator extends Generator
{
	public static final Voxel[] CRYSTALS = { Voxel.get("BLUE_CRYSTAL"), Voxel.get("RED_CRYSTAL"), Voxel.get("YELLOW_CRYSTAL") };
	float y;

	public CrystalGenerator(float y)
	{
		this.y = y;
	}

	@Override
	public void generate(Island island)
	{
		island.calculateWeight();

		float weightNeededToUplift = island.getWeight() / World.calculateRelativeUplift(y);

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

		placeCrystals(island, amounts, (int) y);
	}

	private void placeCrystals(Island island, int[] amounts, int y)
	{
		for (int j = 0; j < amounts.length; j++)
		{
			for (int i = 0; i < amounts[j]; i++)
			{
				Vector3 v = pickRandomNaturalVoxel(island);
				island.set((int) v.x, (int) v.y, (int) v.z, CRYSTALS[j].getId());
			}
		}
	}
}
