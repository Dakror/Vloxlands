package de.dakror.vloxlands.game.item;

import com.badlogic.gdx.utils.Array;

/**
 * @author Dakror
 */
public class Inventory
{
	protected Array<ItemStack> stacks;
	protected int capacity;
	protected int count;
	
	public Inventory(int capacity)
	{
		this.capacity = capacity;
		stacks = new Array<ItemStack>();
	}
	
	public Inventory()
	{
		this(10);
	}
	
	public ItemStack add(ItemStack stack)
	{
		if (count + stack.getAmount() > capacity)
		{
			addStack(stack, capacity - count);
			stack.sub(capacity - count);
			return stack;
		}
		else
		{
			addStack(stack, stack.getAmount());
			return new ItemStack();
		}
	}
	
	public int get(Item item)
	{
		int amount = 0;
		for (ItemStack stack : stacks)
			if (stack.getItem().getId() == item.getId()) amount += stack.amount;
		return amount;
	}
	
	public ItemStack take(Item item, int amount)
	{
		if (amount == 0) return null;
		ItemStack is = new ItemStack(item, 0);
		
		for (ItemStack stack : stacks)
		{
			if (stack.getItem().getId() != item.getId()) continue;
			
			if (amount >= stack.getAmount())
			{
				amount -= stack.getAmount();
				is.add(stack.getAmount());
				stacks.removeValue(stack, true);
			}
			else
			{
				is.add(amount);
				stack.sub(amount);
			}
		}
		
		count -= is.getAmount();
		return is;
	}
	
	/**
	 * @param item Item type to get
	 * @param amount amount to get
	 * @return only if at least <code>amount</code> items of type <code>item</code> are inside the inventory a stack, otherwise <code>null</code>
	 */
	public ItemStack takeIfHas(Item item, int amount)
	{
		ItemStack is = take(item, amount);
		if (is.getAmount() == amount) return is;
		
		count += is.getAmount(); // undo-hack
		return new ItemStack();
	}
	
	protected void addStack(ItemStack stack, int amount)
	{
		for (ItemStack s : stacks)
		{
			if (s.getItem().getId() != stack.getItem().getId() || s.isFull()) continue;
			if (amount == 0) break;
			
			amount = s.add(amount);
		}
		
		if (amount != 0) stacks.add(new ItemStack(stack.getItem(), amount));
		
		count += amount;
	}
	
	/**
	 * If amount == 0, then everything gets checked against the item type
	 */
	public boolean contains(ItemStack stack)
	{
		for (ItemStack s : stacks)
		{
			if (stack.getAmount() == 0)
			{
				if (s.getItem().getId() == stack.getItem().getId()) return true;
			}
			else if (s.equals(stack)) return true;
		}
		
		return false;
	}
	
	public boolean isFull()
	{
		return count == capacity;
	}
	
	public int getCapacity()
	{
		return capacity;
	}
	
	public int getCount()
	{
		return count;
	}
	
	public void setCapacity(int capacity)
	{
		this.capacity = capacity;
	}
}
