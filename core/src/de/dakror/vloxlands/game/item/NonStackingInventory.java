package de.dakror.vloxlands.game.item;


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
	public ItemStack get(Item item, int amount)
	{
		if (amount == 0) return null;
		int am = Math.min(amount, storage[item.getId() + 128]);
		count -= am;
		return new ItemStack(item, am);
	}
}
