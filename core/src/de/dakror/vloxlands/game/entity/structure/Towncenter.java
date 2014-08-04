package de.dakror.vloxlands.game.entity.structure;

import de.dakror.vloxlands.game.item.inv.NonStackingInventory;

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
		
		resourceList.setMaxBuildings(1);
	}
}
