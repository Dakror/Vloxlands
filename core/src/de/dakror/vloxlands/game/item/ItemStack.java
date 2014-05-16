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
	
	public int setAmount(int amount)
	{
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
	
	public Item getItem()
	{
		return item;
	}
}
