package de.dakror.vloxlands.game.job;

import com.badlogic.gdx.Gdx;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.item.tool.Tool;
import de.dakror.vloxlands.util.IInventory;

/**
 * @author Dakror
 */
public class PickupJob extends Job
{
	ItemStack stack;
	IInventory target;
	boolean equip;
	
	public PickupJob(Human human, IInventory target, ItemStack stack, boolean equip, boolean persistent)
	{
		super(human, "ablegen", "Picking up " + (stack.getItem() instanceof Tool ? "tool" : "item"), 1, persistent);
		this.stack = stack;
		this.target = target;
		this.equip = equip;
	}

	public ItemStack getItemStack()
	{
		return stack;
	}
	
	public IInventory getTarget()
	{
		return target;
	}
	
	public boolean isEquip()
	{
		return equip;
	}
	
	@Override
	public void tick(int tick)
	{
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
