package de.dakror.vloxlands.game.entity.structure;

import de.dakror.vloxlands.game.item.NonStackingInventory;

/**
 * @author Dakror
 */
public class Towncenter extends Warehouse
{
	public Towncenter(float x, float y, float z)
	{
		super(x, y, z);
		
		name = "Towncenter";
		inventory = new NonStackingInventory(300);
		confirmDismante = true;
	}
}