package de.dakror.vloxlands.game.job;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.util.IInventory;

/**
 * @author Dakror
 */
public class DepositJob extends Job
{
	private IInventory target;
	
	public DepositJob(Human human, IInventory target, boolean persistent)
	{
		super(human, "depositing", "Depositing carried items", 1, persistent);
		this.target = target;
	}
	
	@Override
	public void tick(int tick)
	{}
	
	@Override
	public void onEnd()
	{
		super.onEnd();
		
		human.setCarryingItemStack(target.getInventory().add(human.getCarryingItemStack()));
	}
}
