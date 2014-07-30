package de.dakror.vloxlands.game.job;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.util.InventoryProvider;

/**
 * @author Dakror
 */
public class DepositJob extends Job
{
	private InventoryProvider target;
	
	public DepositJob(Human human, InventoryProvider target, boolean persistent)
	{
		super(human, "deposit", "Depositing carried items", 1, persistent);
		this.target = target;
	}
	
	public InventoryProvider getTarget()
	{
		return target;
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
