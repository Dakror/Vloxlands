package de.dakror.vloxlands.game.item;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import de.dakror.vloxlands.util.math.Bits;

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
	public boolean contains(Class<?> class1)
	{
		for (Item i : Item.getAll())
			if (i.getClass().equals(class1) && get(i) > 0) return true;
		
		return false;
	}
	
	@Override
	public Item getAnyItemForToolType(Class<?> class1)
	{
		for (Item i : Item.getAll())
			if (i.getClass().equals(class1) && get(i) > 0) return i;
		
		return null;
	}
	
	@Override
	public ItemStack take(Item item, int amount)
	{
		if (amount == 0) return null;
		int am = Math.min(amount, storage[item.getId() + 128]);
		count -= am;
		storage[item.getId() + 128] -= am;
		return new ItemStack(item, am);
	}
	
	@Override
	public void save(ByteArrayOutputStream baos) throws IOException
	{
		Bits.putInt(baos, capacity);
		Bits.putInt(baos, count);
		Bits.putInt(baos, storage.length * 4 /* byte size of storage */);
		
		for (int i = 0; i < storage.length; i++)
			Bits.putInt(baos, storage[i]);
	}
}
