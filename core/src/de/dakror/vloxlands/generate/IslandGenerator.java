package de.dakror.vloxlands.generate;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.generate.structure.CrystalGenerator;
import de.dakror.vloxlands.generate.structure.SpikeGenerator;
import de.dakror.vloxlands.generate.structure.TopLayerGenerator;

public class IslandGenerator
{
	public static final int MIN = 64;
	public static final int MAX = 64;
	
	public static Island generate()
	{
		float yPos = 256 * MathUtils.random();
		int radius = MathUtils.random(MIN, MAX);
		
		int j = (int) (3 + 3 * MathUtils.random() + radius / 8f);
		
		Island island = new Island();
		island.setPos(new Vector3(0, yPos, 0));
		
		new TopLayerGenerator(Island.SIZE / 2, Island.SIZE / 4 * 3, Island.SIZE / 2, radius, j).generate(island);
		for (int k = 0; k < radius; k++)
			new SpikeGenerator(Island.SIZE / 2, Island.SIZE / 4 * 3, Island.SIZE / 2, radius, j).generate(island);
		
		// clear top layer from stone
		for (int i = 0; i < Island.SIZE; i++)
			for (int k = 0; k < Island.SIZE; k++)
				if (island.get(i, Island.SIZE / 4 * 3, k) == Voxel.get("STONE").getId()) island.set(i, Island.SIZE / 4 * 3, k, Voxel.get("DIRT").getId());
		
		new CrystalGenerator(yPos).generate(island);
		
		island.grassify();
		island.calculateInitBalance();
		
		return island;
	}
}
