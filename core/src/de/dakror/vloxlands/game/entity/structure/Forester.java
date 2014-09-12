package de.dakror.vloxlands.game.entity.structure;

import de.dakror.vloxlands.ai.state.WorkerState;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.inv.Inventory;

/**
 * @author Dakror
 */
public class Forester extends Structure
{
	public Forester(float x, float y, float z)
	{
		super(x, y, z, "structure/towncenter.vxi");
		
		nodes.add(new StructureNode(NodeType.deposit, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.pickup, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.entry, 0, 0, 2));
		name = "Forester";
		workerName = "Forester";
		workerState = WorkerState.FORESTER;
		
		costs.add(Item.get("WOODEN_LOG"), 25);
		costs.add(Item.get("PEOPLE"), 1);
		
		weight = 1000f;
		workRadius = 25f;
		
		inventory = new Inventory(20);
	}
}
