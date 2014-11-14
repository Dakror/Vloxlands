package de.dakror.vloxlands.ai.task;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.structure.NodeType;
import de.dakror.vloxlands.game.item.inv.ResourceList;

/**
 * @author Dakror
 */
public class SpawnTask extends Task {
	byte entityId;
	
	public SpawnTask(String name, String title, String description, Vector2 icon, int duration, ResourceList costs, int entityId) {
		super(name, title, description, icon, duration, costs, null);
		this.entityId = (byte) entityId;
	}
	
	@Override
	public void exit() {
		Vector3 pos = origin.getStructureNode(null, NodeType.spawn).pos.cpy().add(origin.getVoxelPos());
		origin.getIsland().addEntity(Entity.getForId(entityId, pos.x, pos.y, pos.z), true, false);
	}
}
