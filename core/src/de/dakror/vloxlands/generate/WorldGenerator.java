package de.dakror.vloxlands.generate;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.world.Island;

/**
 * @author Dakror
 */
public class WorldGenerator extends Thread
{
	public float progress;
	public boolean done;
	
	public WorldGenerator()
	{
		setName("WorldGenerator Thread");
	}
	
	@Override
	public void run()
	{
		int total = Vloxlands.world.getWidth() * Vloxlands.world.getDepth();
		for (int i = 0; i < Vloxlands.world.getWidth(); i++)
		{
			for (int j = 0; j < Vloxlands.world.getDepth(); j++)
			{
				Island island = IslandGenerator.generate();
				island.setPos(new Vector3(i * Island.SIZE, island.getPos().y, j * Island.SIZE));
				Vloxlands.world.addIsland(i, j, island);
				
				progress += 1f / total;
			}
		}
		
		done = true;
	}
}
