package de.dakror.vloxlands.game.item.inv;

import de.dakror.vloxlands.game.item.ItemStack;

/**
 * @author Dakror
 */
public class ManagedInventory extends Inventory
{
	public ManagedInventory(int capacity)
	{
		super(capacity);
	}
	
	@Override
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
		
		if (amount != 0) stacks.add(new ManagedItemStack(stack.getItem(), amount));
		
		count += amount2;
		
		dispatchItemAdded(oldCount);
	}
	
	@Override
	public ItemStack getFirst()
	{
		if (stacks.size == 0) return new ItemStack();
		for (ItemStack is : stacks)
			if (!((ManagedItemStack) is).managed) return is;
		
		return new ItemStack();
	}
	
	public void manageNext()
	{
		if (stacks.size == 0) return;
		for (ItemStack is : stacks)
		{
			if (!((ManagedItemStack) is).managed)
			{
				((ManagedItemStack) is).managed = true;
				break;
			}
		}
	}
}
