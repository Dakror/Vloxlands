package de.dakror.vloxlands.game.job;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.world.Island;

/**
 * @author Dakror
 */
public class ClearRegionJob extends Job
{
	Vector3 start, end;
	Array<Vector3> targets;
	Island island;
	
	public ClearRegionJob(Human human, Island island, Vector3 start, Vector3 end, boolean persistent)
	{
		super(human, null, "Clearing a " + (int) (Math.max(start.x, end.x) - Math.min(start.x, end.x) + 1) + " x " + (int) (Math.max(start.y, end.y) - Math.min(start.y, end.y) + 1) + " x " + (int) (Math.max(start.z, end.z) - Math.min(start.z, end.z) + 1) + " region", 1, persistent);
		this.island = island;
		this.start = start;
		this.end = end;
		
		targets = new Array<Vector3>();
	}
}
