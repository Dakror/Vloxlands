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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.Config;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.inv.Inventory;
import de.dakror.vloxlands.util.CurserCommand;

/**
 * @author Dakror
 */
public class Sawmill extends Structure {
	public Sawmill(float x, float y, float z) {
		super(x, y, z, "structure/sawmill.vxi");
		
		nodes.add(new StructureNode(NodeType.deposit, 0, 0, 2));
		nodes.add(new StructureNode(NodeType.pickup, 0, 0, 2));
		nodes.add(new StructureNode(NodeType.entry, 0, 0, 2));
		name = "Sawmill";
		workerName = "Sawmill worker";
		// FIXME add worker ai
		// workerTool =;
		
		costs.add(Item.get("WOODEN_LOG"), 25);
		costs.add(Item.get("IRON_INGOT"), 5);
		// costs.add(Item.get("PEOPLE"), 1);
		
		weight = 1500f;
		
		inventory = new Inventory(20);
	}
	
	@Override
	public void render(ModelBatch batch, Environment environment, boolean minimapMode) {
		subs.get(1).transform.translate(0.125f, 0.375f, 0).rotate(Vector3.Z, -50 * Gdx.graphics.getDeltaTime() * Config.getGameSpeed()).translate(-0.125f, -0.375f, 0);
		
		super.render(batch, environment, minimapMode);
	}
	
	@Override
	public CurserCommand getDefaultCommand() {
		return inventory.getCount() > 0 ? CurserCommand.PICKUP : super.getDefaultCommand();
	}
}
