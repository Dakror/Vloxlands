package de.dakror.vloxlands.game.entity;

import de.dakror.vloxlands.game.item.Item;

public class EntityItem extends Entity
{
	public final Item item;
	public int amount = 1;

	public EntityItem(float x, float y, float z, Item i)
	{
		super(x, y, z, i.getModel());
		item = i;
		this.name = "entity." + i.getName();
	}

	public EntityItem(float x, float y, float z, Item i, int amount)
	{
		this(x, y, z, i);
		this.amount = amount;
	}
}
