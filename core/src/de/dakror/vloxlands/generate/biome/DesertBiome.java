package de.dakror.vloxlands.generate.biome;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.generate.Beziers;
import de.dakror.vloxlands.generate.structure.BezierGenerator;
import de.dakror.vloxlands.generate.structure.CrystalGenerator;
import de.dakror.vloxlands.generate.structure.SpikeGenerator;

/**
 * @author Dakror
 */
public class DesertBiome extends Biome
{
	@Override
	public void generate(Island island, int radius)
	{
		int j = (int) (15 + 3 * MathUtils.random() + radius / 8f);
		new BezierGenerator(Island.SIZE / 2, Island.SIZE / 4 * 3, Island.SIZE / 2, radius, j, new byte[] { Voxel.get("SANDSTONE").getId() }, Beziers.BOWL_BEZIER).generate(island);
		new BezierGenerator(Island.SIZE / 2, Island.SIZE / 4 * 3 + 1, Island.SIZE / 2, radius - 1, j, new byte[] { Voxel.get("SAND").getId() }, Beziers.BOWL_BEZIER).generate(island);
		fillHorizontalCircle(island, Island.SIZE / 2, Island.SIZE / 4 * 3 + 1, Island.SIZE / 2, radius, new byte[] { Voxel.get("AIR").getId() }, true);

		int foundlings = MathUtils.random(radius / 4, radius / 2);
		int radiusAt0 = (int) (getHighestBezierValue(Beziers.BOULDER_BEZIER).y * radius);
		for (int i = 0; i < foundlings; i++)
		{
			int rad = MathUtils.random(3, 6);
			int height = MathUtils.random(4, 7);
			Vector2 pos = getRandomCircleInCircle(new Vector2(Island.SIZE / 2, Island.SIZE / 2), radiusAt0, rad);
			new BezierGenerator((int) pos.x, Island.SIZE / 4 * 3 + height / 2 + 1, (int) pos.y, rad, height, new byte[] { Voxel.get("SANDSTONE").getId() }, Beziers.BOULDER_BEZIER).generate(island);
		}
		
		byte[] sRatio = createRatio(new byte[] { Voxel.get("SANDSTONE").getId(), Voxel.get("STONE").getId(), Voxel.get("SAND").getId() }, new int[] { 5, 2, 1 });
		for (int k = 0; k < radius / 5; k++)
			new SpikeGenerator(Island.SIZE / 2, Island.SIZE / 4 * 3, Island.SIZE / 2, radius, j, sRatio).generate(island);

		new CrystalGenerator(island.pos.y).generate(island);
	}
}
