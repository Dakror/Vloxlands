package de.dakror.vloxlands.game.item;

import de.dakror.vloxlands.ui.ItemSlotActor;

/**
 * @author Dakror
 */
public class ItemStack
{
	private Item item;
	private int amount;
	public ItemSlotActor slot;
	
	public ItemStack(Item item, int amount)
	{
		this.item = item;
		this.amount = amount;
	}
	
	public int getAmount()
	{
		return amount;
	}
	
	public int setAmount(int amount)
	{
		if (this.amount != amount && slot != null) slot.onStackChanged(this);
		
		this.amount = amount;
		if (amount > item.getStack())
		{
			this.amount = item.getStack();
			return amount - item.getStack();
		}
		if (amount < 1) this.amount = 1;
		
		return 0;
	}
	
	public int add(int amount)
	{
		return setAmount(this.amount + amount);
	}
	
	public int sub(int amount)
	{
		return setAmount(this.amount - amount);
	}
	
	public boolean isFull()
	{
		return amount == item.getStack();
	}
	
	public Item getItem()
	{
		return item;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ItemStack)) return false;
		return item.getId() == ((ItemStack) obj).getItem().getId() && amount == ((ItemStack) obj).getAmount();
	}
}
