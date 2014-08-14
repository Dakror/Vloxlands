package de.dakror.vloxlands.game.entity;

import de.dakror.vloxlands.game.item.Item;

public class ItemDrop extends Entity
{
	public final Item item;
	public int amount = 1;
	
	public ItemDrop(float x, float y, float z, Item i)
	{
		super(x, y, z, "models/item/" + i.getModel());
		item = i;
		this.name = "entity." + i.getName();
	}
	
	public ItemDrop(float x, float y, float z, Item i, int amount)
	{
		this(x, y, z, i);
		this.amount = amount;
	}
}
