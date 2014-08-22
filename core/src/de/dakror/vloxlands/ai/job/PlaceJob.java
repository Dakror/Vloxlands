package de.dakror.vloxlands.ai.job;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.voxel.Voxel;

/**
 * @author Dakror
 */
public class PlaceJob extends Job
{
	Vector3 target;
	Voxel voxel;
	
	public PlaceJob(Human human, Vector3 target, Voxel voxel, boolean persistent)
	{
		super(human, "deposit", "Placing " + voxel.getName(), 1, persistent);
		this.target = target;
		this.voxel = voxel;
	}
	
	@Override
	public void onEnd()
	{
		super.onEnd();
		
		human.getIsland().set(target.x, target.y, target.z, voxel.getId());
	}
	
	public Vector3 getTarget()
	{
		return target;
	}
}
