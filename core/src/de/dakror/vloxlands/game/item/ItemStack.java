package de.dakror.vloxlands.game.item;

/**
 * @author Dakror
 */
public class ItemStack
{
	Item item;
	int amount;
	
	public ItemStack(Item item, int amount)
	{
		this.item = item;
		this.amount = amount;
	}
	
	public int getAmount()
	{
		return amount;
	}
	
	public void setAmount(int amount)
	{
		this.amount = amount;
		if (amount > item.getStack()) this.amount = item.getStack();
		if (amount < 1) this.amount = 1;
	}
	
	public Item getItem()
	{
		return item;
	}
}
