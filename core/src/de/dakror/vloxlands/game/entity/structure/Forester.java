package de.dakror.vloxlands.game.entity.structure;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

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
		super(x, y, z, "models/structure/PH_tent/PH_tent.g3db");
		
		modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(Color.valueOf("06C010")));
		modelInstance.materials.get(1).set(ColorAttribute.createDiffuse(Color.valueOf("497F49")));
		
		nodes.add(new StructureNode(NodeType.deposit, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.pickup, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.entry, 0, 0, 2));
		name = "Forester";
		workerName = "Forester";
		workerState = WorkerState.FORESTER;
		
		resourceList.add(Item.get("WOODEN_LOG"), 25);
		resourceList.setCostPopulation(1);
		
		inventory = new Inventory(0);
	}
}
