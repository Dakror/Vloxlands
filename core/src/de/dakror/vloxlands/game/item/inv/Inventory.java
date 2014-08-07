package de.dakror.vloxlands.game.item.inv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.util.Savable;
import de.dakror.vloxlands.util.event.InventoryListener;
import de.dakror.vloxlands.util.math.Bits;

/**
 * @author Dakror
 */
public class Inventory implements Savable
{
	protected Array<ItemStack> stacks;
	protected int capacity;
	protected int count;
	
	public Array<InventoryListener> listeners = new Array<InventoryListener>();
	
	public Inventory(int capacity)
	{
		this.capacity = capacity;
		stacks = new Array<ItemStack>();
	}
	
	public Inventory()
	{
		this(10);
	}
	
	public void clear()
	{
		stacks.clear();
		int oldCount = count;
		count = 0;
		dispatchItemRemoved(oldCount);
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
			if (stack.getItem().getId() == item.getId()) amount += stack.getAmount();
		return amount;
	}
	
	public int get(byte id)
	{
		int amount = 0;
		for (ItemStack stack : stacks)
			if (stack.getItem().getId() == id) amount += stack.getAmount();
		return amount;
	}
	
	public ItemStack getFirst()
	{
		if (stacks.size == 0) return new ItemStack();
		return stacks.first();
	}
	
	public ItemStack take(Item item, int amount)
	{
		if (amount == 0) return null;
		int oldCount = count;
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
		dispatchItemRemoved(oldCount);
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
		int oldCount = count;
		int amount2 = amount;
		for (ItemStack s : stacks)
		{
			if (s.getItem().getId() != stack.getItem().getId() || s.isFull()) continue;
			if (amount == 0) break;
			
			amount = s.add(amount);
		}
		
		if (amount != 0) stacks.add(new ItemStack(stack.getItem(), amount));
		
		count += amount2;
		
		dispatchItemAdded(oldCount);
	}
	
	public boolean contains(ItemStack stack)
	{
		for (ItemStack s : stacks)
			if (s.getItem().getId() == stack.getItem().getId() && s.getAmount() >= stack.getAmount()) return true;
		
		return false;
	}
	
	/**
	 * Searches for Tool type
	 *
	 * @param class1 searched type
	 * @return true if this contains item(s) of searched type
	 */
	public boolean contains(Class<?> class1)
	{
		for (ItemStack s : stacks)
			if (s.getItem().getClass().equals(class1)) return true;
		
		return false;
	}
	
	public Item getAnyItemForToolType(Class<?> class1)
	{
		for (ItemStack s : stacks)
			if (s.getItem().getClass().equals(class1)) return s.getItem();
		
		return null;
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
	
	protected void dispatchItemAdded(int countBefore)
	{
		for (InventoryListener isl : listeners)
			isl.onItemAdded(countBefore, this);
	}
	
	protected void dispatchItemRemoved(int countBefore)
	{
		for (InventoryListener isl : listeners)
			isl.onItemRemoved(countBefore, this);
	}
	
	public void addListener(InventoryListener listener)
	{
		listeners.insert(0, listener);
	}
	
	public void removeListener(InventoryListener listener)
	{
		listeners.removeValue(listener, true);
	}
	
	@Override
	public void save(ByteArrayOutputStream baos) throws IOException
	{
		Bits.putInt(baos, capacity);
		Bits.putInt(baos, count);
		Bits.putInt(baos, stacks.size * 2 /* byte size of all stacks */);
		for (ItemStack is : stacks)
			is.save(baos);
	}
}
