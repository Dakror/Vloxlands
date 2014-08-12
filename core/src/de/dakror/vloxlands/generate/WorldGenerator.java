package de.dakror.vloxlands.generate;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.Game;
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
		for (int i = 0; i < Game.world.getWidth(); i++)
		{
			for (int j = 0; j < Game.world.getDepth(); j++)
			{
				Island island = IslandGenerator.generate(this);
				island.setPos(new Vector3(i * Island.SIZE, island.pos.y, j * Island.SIZE));
				Game.world.addIsland(i, j, island);
			}
		}
		done = true;
	}
	
	public void step()
	{
		int total = Game.world.getWidth() * Game.world.getDepth() * 6;
		progress += 1f / total;
	}
}
