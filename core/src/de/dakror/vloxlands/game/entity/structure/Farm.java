package de.dakror.vloxlands.game.entity.structure;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import de.dakror.vloxlands.ai.state.WorkerState;
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
		super(x, y, z, "models/structure/PH_tent/PH_tent.g3db");
		
		modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(Color.valueOf("D19751")));
		modelInstance.materials.get(1).set(ColorAttribute.createDiffuse(Color.valueOf("6B4B2A")));
		
		nodes.add(new StructureNode(NodeType.deposit, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.pickup, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.entry, 0, 0, 2));
		name = "Farm";
		workerName = "Farmer";
		workerTool = FarmTool.class;
		workerState = WorkerState.FARMER;
		
		resourceList.add(Item.get("WOODEN_LOG"), 20);
		resourceList.setCostPopulation(1);
		
		inventory = new Inventory(50);
	}
}
