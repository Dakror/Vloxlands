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
	public static final Voxel[] CRYSTALS = { Voxel.get("STRONG_CRYSTAL"), Voxel.get("MEDIUM_CRYSTAL"), Voxel.get("WEAK_CRYSTAL") };
	float y;
	
	public CrystalGenerator(float y)
	{
		this.y = y;
	}
	
	@Override
	public void generate(Island island)
	{
		island.calculateWeight();
		
		float weightNeededToUplift = island.getWeight() / World.calculateUplift(y);
		
		while (weightNeededToUplift > 100)
		{
			int index = (int) (MathUtils.random() * CRYSTALS.length);
			weightNeededToUplift -= createCrystalVein(island, index, weightNeededToUplift);
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
	
	/**
	 * @return uplifted
	 */
	private float createCrystalVein(Island island, int index, float maximum)
	{
		int type = 0;// (int) (MathUtils.random() * 3);
		int width = 0, height = 0, depth = 0;
		
		Vector3 c = pickRandomNaturalVoxel(island);
		
		float uplifted = 0;
		
		switch (type)
		{
			case 0: // qubic
			{
				depth = height = width = (int) (MathUtils.random() * 3 + (index + 1));
				
				float maxDistance = (float) (width * Math.sqrt(3)) / 2;
				
				for (int i = (int) (c.x - width * .5f); i < c.x + width * .5f; i++)
				{
					for (int j = (int) (c.y - height * .5f); j < c.y + height * .5f; j++)
					{
						for (int k = (int) (c.z - depth * .5f); k < c.z + depth * .5f; k++)
						{
							if (MathUtils.random() * maxDistance > Vector3.dst(i, j, k, c.x, c.y, c.z))
							{
								if (uplifted + CRYSTALS[index].getUplift() >= maximum) return uplifted;
								
								uplifted += CRYSTALS[index].getUplift();
								if (island.get(i, j, k) != Voxel.get("AIR").getId()) uplifted += Voxel.getVoxelForId(island.get(i, j, k)).getWeight();
								
								island.set(i, j, k, CRYSTALS[index].getId());
							}
						}
					}
				}
				break;
			}
		}
		return uplifted;
	}
}
