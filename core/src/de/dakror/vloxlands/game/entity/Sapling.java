package de.dakror.vloxlands.game.entity;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.ai.state.StateTools;
import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.generate.Generator;

/**
 * @author Dakror
 */
public class Sapling extends Entity
{
	int growTicksLeft;
	Vector3 voxelPos;
	
	public Sapling(float x, float y, float z)
	{
		super(x + 0.25f, y - 0.5f, z + 0.25f, "models/entities/sapling/sapling.g3db");
		
		voxelPos = new Vector3(x, y, z);
		name = "Sapling";
		weight = 1f;
		
		growTicksLeft = (int) (Game.dayInTicks * 2.5f);
	}
	
	@Override
	public void tick(int tick)
	{
		super.tick(tick);
		
		if (StateTools.isWorkingTime()) growTicksLeft--; // only grows in sunlight, so <code>initial time * 2</code> = real time is takes
		
		if (growTicksLeft <= 0)
		{
			Generator.generateTree(island, (int) voxelPos.x, (int) voxelPos.y - 1, (int) voxelPos.z);
			markedForRemoval = true;
		}
	}
}
