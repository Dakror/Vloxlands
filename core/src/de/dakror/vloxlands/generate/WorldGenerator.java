package de.dakror.vloxlands.generate;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.layer.GameLayer;

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
		int total = GameLayer.world.getWidth() * GameLayer.world.getDepth();
		for (int i = 0; i < GameLayer.world.getWidth(); i++)
		{
			for (int j = 0; j < GameLayer.world.getDepth(); j++)
			{
				Island island = IslandGenerator.generate();
				island.setPos(new Vector3(i * Island.SIZE, island.pos.y, j * Island.SIZE));
				GameLayer.world.addIsland(i, j, island);
				
				progress += 1f / total;
			}
		}
		
		done = true;
	}
}
