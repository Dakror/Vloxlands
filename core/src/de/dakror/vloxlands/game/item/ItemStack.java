package de.dakror.vloxlands.game.item;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.util.Savable;
import de.dakror.vloxlands.util.event.ItemStackListener;

/**
 * @author Dakror
 */
public class ItemStack implements Savable
{
	Item item;
	int amount;
	
	public Array<ItemStackListener> listeners = new Array<ItemStackListener>();
	
	public ItemStack()
	{
		this(Item.get("NOTHING"), 0);
	}
	
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
			
			dispatchStackChanged();
			return amount - item.getStack();
		}
		if (amount < 1) this.amount = 1;
		
		dispatchStackChanged();
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
	
	public void setItem(Item item)
	{
		this.item = item;
	}
	
	public Item getItem()
	{
		return item;
	}
	
	public boolean isNull()
	{
		return amount == 0;
	}
	
	public void set(ItemStack o)
	{
		amount = o.amount;
		item = o.item;
		listeners.addAll(o.listeners);
		dispatchStackChanged();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ItemStack)) return false;
		return item.getId() == ((ItemStack) obj).getItem().getId() && amount == ((ItemStack) obj).getAmount();
	}
	
	public boolean canAdd(ItemStack stack)
	{
		if (stack.getItem().getId() != item.getId()) return false;
		return amount + stack.getAmount() <= item.getStack();
	}
	
	public boolean canAddWithOverflow(ItemStack stack)
	{
		if (stack.getItem().getId() != item.getId()) return false;
		return !isFull();
	}
	
	@Override
	public String toString()
	{
		if (isNull()) return "Null";
		return amount + "x " + item.getName();
	}
	
	private void dispatchStackChanged()
	{
		for (ItemStackListener isl : listeners)
			isl.onStackChanged();
	}
	
	public void addListener(ItemStackListener listener)
	{
		listeners.insert(0, listener);
	}
	
	public void removeListener(ItemStackListener listener)
	{
		listeners.removeValue(listener, true);
	}
	
	@Override
	public void save(ByteArrayOutputStream baos) throws IOException
	{
		baos.write(item.getId());
		baos.write(amount);
	}
}
