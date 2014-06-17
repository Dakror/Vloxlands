package de.dakror.vloxlands.game.item;

import java.util.Arrays;

/**
 * @author Dakror
 */
public class NonStackingInventory extends Inventory
{
	int[] storage;
	
	public NonStackingInventory(int capacity)
	{
		super(capacity);
		storage = new int[Item.ITEMS];
	}
	
	public NonStackingInventory()
	{
		this(10);
	}
	
	@Override
	protected void addStack(ItemStack stack, int amount)
	{
		storage[stack.getItem().getId() + 128] += amount;
		count += amount;
	}
	
	@Override
	public int hashCode()
	{
		return Arrays.hashCode(storage);
	}
	
	@Override
	public int get(Item item)
	{
		return storage[item.getId() + 128];
	}
	
	@Override
	public boolean contains(ItemStack stack)
	{
		if (stack.getAmount() == 0) return get(stack.getItem()) > 0;
		else return get(stack.getItem()) == stack.getAmount();
	}
	
	@Override
	public ItemStack take(Item item, int amount)
	{
		if (amount == 0) return null;
		int am = Math.min(amount, storage[item.getId() + 128]);
		count -= am;
		return new ItemStack(item, am);
	}
}
