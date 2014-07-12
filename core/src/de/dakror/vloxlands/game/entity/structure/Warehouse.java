package de.dakror.vloxlands.game.entity.structure;

import de.dakror.vloxlands.game.entity.structure.StructureNode.NodeType;
import de.dakror.vloxlands.game.item.NonStackingInventory;
import de.dakror.vloxlands.util.CurserCommand;

/**
 * @author Dakror
 */
public class Warehouse extends Structure
{
	public Warehouse(float x, float y, float z)
	{
		super(x, y, z, "models/tent/tent.g3db");
		
		nodes.add(new StructureNode(NodeType.deposit, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.pickup, 0, 0, 1));
		name = "Warehouse";
		
		inventory = new NonStackingInventory(100);
	}
	
	@Override
	public CurserCommand getDefaultCommand()
	{
		return CurserCommand.DEPOSIT;
	}
}
