package de.dakror.vloxlands.game.entity.structure;

import de.dakror.vloxlands.game.entity.structure.StructureNode.NodeType;


/**
 * @author Dakror
 */
public class Warehouse extends Structure
{
	public Warehouse(float x, float y, float z)
	{
		super(x, y, z, "models/tent/tent.g3db");
		
		nodes.add(new StructureNode(NodeType.dump, 0, 0, 1));
		
		inventory.setCapacity(250);
	}
}