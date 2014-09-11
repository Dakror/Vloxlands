package de.dakror.vloxlands.game.entity.structure;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import de.dakror.vloxlands.ai.state.WorkerState;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.inv.Inventory;
import de.dakror.vloxlands.game.item.tool.ChopTool;
import de.dakror.vloxlands.util.CurserCommand;

/**
 * @author Dakror
 */
public class Lumberjack extends Structure
{
	public Lumberjack(float x, float y, float z)
	{
		super(x, y, z, "structure/PH_tent/PH_tent.g3db");
		
		modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(Color.valueOf("036108")));
		modelInstance.materials.get(1).set(ColorAttribute.createDiffuse(Color.valueOf("274427")));
		
		nodes.add(new StructureNode(NodeType.deposit, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.pickup, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.entry, 0, 0, 2));
		name = "Lumberjack";
		workerName = "Lumberjack";
		workerState = WorkerState.LUMBERJACK;
		workerTool = ChopTool.class;
		
		costs.add(Item.get("WOODEN_LOG"), 10);
		costs.add(Item.get("PEOPLE"), 1);
		
		weight = 1000f;
		workRadius = 30f;
		
		inventory = new Inventory(20);
	}
	
	@Override
	public CurserCommand getDefaultCommand()
	{
		return inventory.getCount() > 0 ? CurserCommand.PICKUP : super.getDefaultCommand();
	}
}
