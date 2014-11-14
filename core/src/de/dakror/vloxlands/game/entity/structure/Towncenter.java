package de.dakror.vloxlands.game.entity.structure;

import com.badlogic.gdx.math.Vector2;

import de.dakror.vloxlands.ai.task.Tasks;
import de.dakror.vloxlands.game.item.inv.NonStackingInventory;
import de.dakror.vloxlands.ui.RevolverSlot;

/**
 * @author Dakror
 */
public class Towncenter extends Warehouse {
	public Towncenter(float x, float y, float z) {
		super(x, y, z, "structure/towncenter.vxi");
		
		name = "Towncenter";
		inventory = new NonStackingInventory(300);
		confirmDismante = true;
		
		costs.setMaxBuildings(1);
		nodes.add(new StructureNode(NodeType.spawn, -2, 0, 1));
		
		weight = 0;
		
		tasks.add(Tasks.human);
	}
	
	@Override
	public void setActions(RevolverSlot parent) {
		super.setActions(parent);
		
		parent.setIcon(new Vector2(1, 5));
		parent.getTooltip().set("Towncenter", "Queue building specific tasks.");
	}
}
