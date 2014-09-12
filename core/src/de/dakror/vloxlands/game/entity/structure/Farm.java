package de.dakror.vloxlands.game.entity.structure;

import de.dakror.vloxlands.ai.state.WorkerState;
import de.dakror.vloxlands.game.entity.statics.Wheat;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.inv.Inventory;
import de.dakror.vloxlands.game.item.tool.FarmTool;

/**
 * @author Dakror
 */
public class Farm extends Structure
{
	public Farm(float x, float y, float z)
	{
		super(x, y, z, "structure/towncenter.vxi");
		
		nodes.add(new StructureNode(NodeType.deposit, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.pickup, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.entry, 0, 0, 2));
		name = "Farm";
		workerName = "Farmer";
		workerTool = FarmTool.class;
		workerState = WorkerState.FARMER;
		
		costs.add(Item.get("WOODEN_LOG"), 20);
		costs.add(Item.get("PEOPLE"), 1);
		
		weight = 1000f;
		workRadius = 15f;
		
		inventory = new Inventory(50);
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		if (!isBuilt())
		{
			for (int i = 0; i < 4; i++)
			{
				for (int j = 0; j < 4; j++)
				{
					float x = i - 5 + voxelPos.x;
					float y = voxelPos.y;
					float z = j - 5 + voxelPos.z;
					if (island.isSpaceAbove(x, y, z, 2) && island.get(x, y, z) != 0)
					{
						island.addEntity(new Wheat(x, y, z), false, false);
					}
				}
			}
		}
	}
}
