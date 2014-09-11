package de.dakror.vloxlands.game.entity.structure;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import de.dakror.vloxlands.ai.state.WorkerState;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.inv.Inventory;
import de.dakror.vloxlands.game.item.tool.MineTool;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.util.CurserCommand;

/**
 * @author Dakror
 */
public class Mine extends Structure
{
	byte activeOre;
	
	public Mine(float x, float y, float z)
	{
		super(x, y, z, "structure/PH_tent/PH_tent.g3db");
		
		modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(Color.valueOf("3D3D3D")));
		modelInstance.materials.get(1).set(ColorAttribute.createDiffuse(Color.valueOf("666666")));
		
		nodes.add(new StructureNode(NodeType.deposit, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.pickup, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.entry, 0, 0, 2));
		name = "Mine";
		workerName = "Mine";
		workerState = WorkerState.MINER;
		workerTool = MineTool.class;
		
		costs.add(Item.get("WOODEN_LOG"), 10);
		costs.add(Item.get("PEOPLE"), 1);
		
		activeOre = Voxel.get("STONE").getId();
		
		weight = 1000f;
		workRadius = 30f;
		
		inventory = new Inventory(20);
	}
	
	@Override
	public CurserCommand getDefaultCommand()
	{
		return inventory.getCount() > 0 ? CurserCommand.PICKUP : super.getDefaultCommand();
	}
	
	public byte getActiveOre()
	{
		return activeOre;
	}
	
	public void setActiveOre(byte activeOre)
	{
		this.activeOre = activeOre;
	}
}
