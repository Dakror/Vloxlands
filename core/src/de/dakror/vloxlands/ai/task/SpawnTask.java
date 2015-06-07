/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


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
