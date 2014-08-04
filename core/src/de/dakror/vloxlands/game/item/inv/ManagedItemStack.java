package de.dakror.vloxlands.game.item.inv;

import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;

/**
 * @author Dakror
 */
public class ManagedItemStack extends ItemStack
{
	boolean managed;
	
	public ManagedItemStack()
	{}
	
	public ManagedItemStack(Item item, int amount)
	{
		super(item, amount);
	}
}
