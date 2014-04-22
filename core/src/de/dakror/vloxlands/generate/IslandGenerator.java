package de.dakror.vloxlands.generate;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.generate.structure.SpikeGenerator;
import de.dakror.vloxlands.generate.structure.TopLayerGenerator;

public class IslandGenerator
{
	public static final int MAX = 48;
	public static final int MIN = 32;
	
	public static Island generate()
	{
		float yPos = 256 * MathUtils.random();
		int radius = MathUtils.random(32, 48);
		int j = (int) (3 + 3 * MathUtils.random() + radius / 8f);
		
		Island island = new Island();
		island.setPos(new Vector3(0, yPos, 0));
		
		new TopLayerGenerator(32, 48, 32, radius, j).generate(island);
		for (int k = 0; k < radius; k++)
		{
			new SpikeGenerator(32, 48, 32, radius, j).generate(island);
		}
		
		island.grassify();
		island.calculateInitBalance();
		return island;
	}
}
