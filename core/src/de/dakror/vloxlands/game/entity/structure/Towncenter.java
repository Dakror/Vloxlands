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
