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

import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.item.inv.ResourceList;

/**
 * @author Dakror
 */
public class DefaultTask extends Task {
	public DefaultTask(String name, String title, String description, Vector2 icon, int duration, ResourceList costs, ResourceList result) {
		super(name, title, description, icon, duration, costs, result);
	}
	
	@Override
	public void exit() {
		for (Byte b : result.getAll()) {
			origin.getInventory().add(new ItemStack(Item.getForId(b), result.get(b)));
		}
	}
}
