package de.dakror.vloxlands.game.job;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.util.InventoryProvider;

/**
 * @author Dakror
 */
public class DepositJob extends Job
{
	private Structure target;
	
	public DepositJob(Human human, Structure target, boolean persistent)
	{
		super(human, "deposit", "Depositing carried items", 1, persistent);
		this.target = target;
	}
	
	public InventoryProvider getTarget()
	{
		return target;
	}
	
	@Override
	public void onEnd()
	{
		super.onEnd();
		
		if (!target.isBuilt())
		{
			target.getInventory().take(human.getCarryingItemStack().getItem(), human.getCarryingItemStack().getAmount());
			human.setCarryingItemStack(new ItemStack());
		}
		else human.setCarryingItemStack(target.getInventory().add(human.getCarryingItemStack()));
	}
}
