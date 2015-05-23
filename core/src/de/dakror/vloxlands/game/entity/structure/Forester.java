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

import de.dakror.vloxlands.ai.state.WorkerState;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.inv.Inventory;

/**
 * @author Dakror
 */
public class Forester extends Structure {
	public Forester(float x, float y, float z) {
		super(x, y, z, "structure/house.vxi");
		
		nodes.add(new StructureNode(NodeType.deposit, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.pickup, 0, 0, 1));
		nodes.add(new StructureNode(NodeType.entry, 0, 0, 2));
		name = "Forester";
		workerName = "Forester";
		workerState = WorkerState.FORESTER;
		
		costs.add(Item.get("WOODEN_LOG"), 25);
		costs.add(Item.get("PEOPLE"), 1);
		
		weight = 1000f;
		workRadius = 25f;
		
		inventory = new Inventory(20);
	}
}
