package de.dakror.vloxlands.game.entity.structure;

import de.dakror.vloxlands.ai.state.LumberjackState;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.item.Inventory;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.util.CurserCommand;

/**
 * @author Dakror
 */
public class Lumberjack extends Structure
{
	public Lumberjack(float x, float y, float z)
	{
		super(x, y, z, "models/structure/PH_tent_green/PH_tent_green.g3db");
		
		nodes.add(new StructureNode(NodeType.deposit, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.pickup, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.entry, 0, 0, 2));
		name = "Lumberjack";
		workerName = "Lumberjack";
		
		resourceList.add(Item.get("WOODEN_LOG"), 10);
		resourceList.setCostPopulation(1);
		
		inventory = new Inventory(20);
	}
	
	@Override
	protected void onWorkerAdded(Human human)
	{
		human.changeState(LumberjackState.INIT);
	}
	
	@Override
	public CurserCommand getDefaultCommand()
	{
		return CurserCommand.PICKUP;
	}
}
