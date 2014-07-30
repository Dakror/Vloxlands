package de.dakror.vloxlands.game.job;

import com.badlogic.gdx.Gdx;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.item.tool.Tool;
import de.dakror.vloxlands.util.InventoryProvider;

/**
 * @author Dakror
 */
public class PickupJob extends Job
{
	ItemStack stack;
	InventoryProvider target;
	boolean equip;
	
	public PickupJob(Human human, InventoryProvider target, ItemStack stack, boolean equip, boolean persistent)
	{
		super(human, "deposit", human.getTool().isNull() && equip || !equip ? "Picking up " + (stack.getItem() instanceof Tool ? "tool" : "item") : "Changing tool", 1, persistent);
		this.stack = stack;
		this.target = target;
		this.equip = equip;
	}
	
	public ItemStack getItemStack()
	{
		return stack;
	}
	
	public InventoryProvider getTarget()
	{
		return target;
	}
	
	public boolean isEquip()
	{
		return equip;
	}
	
	@Override
	public void tick(int tick)
	{}
	
	@Override
	public void onEnd()
	{
		super.onEnd();
		
		if (!human.getTool().isNull() && equip)
		{
			target.getInventory().add(human.getTool());
			human.setTool(null);
		}
		
		target.getInventory().take(stack.getItem(), stack.getAmount());
		
		if (stack.getItem() instanceof Tool && equip && human.getTool().isNull())
		{
			human.setTool(stack.getItem());
		}
		else if (human.getCarryingItemStack().isNull() || human.getCarryingItemStack().canAdd(stack))
		{
			if (human.getCarryingItemStack().isNull()) human.setCarryingItemStack(stack);
			else human.getCarryingItemStack().add(stack.getAmount());
		}
		else
		{
			Gdx.app.error("PickupJob.tick", "Welp, this Human can't pickup those items! Putting 'em back.");
			target.getInventory().add(stack);
		}
	}
}
