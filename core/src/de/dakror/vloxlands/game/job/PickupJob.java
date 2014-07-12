package de.dakror.vloxlands.game.job;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;

/**
 * @author Ichmed
 */
public class PickupJob extends Job
{
	private Structure target;

	public PickupJob(Human human, Structure target, boolean persistent)
	{
		super(human, "fetching", "Fetching items", 1, persistent);
		this.target = target;
	}

	public Structure getTarget()
	{
		return target;
	}

	@Override
	public void tick(int tick)
	{
	}

	@Override
	public void onEnd()
	{
		super.onEnd();

//		human.setCarryingItemStack(target.getInventory().take(human.getCarryingItemStack()));
	}

}
