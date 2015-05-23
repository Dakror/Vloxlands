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
 

package de.dakror.vloxlands.ai.job;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.item.tool.ChopTool;
import de.dakror.vloxlands.game.voxel.Voxel;

/**
 * @author Dakror
 */
public class ChopJob extends Job {
	final Vector3 target = new Vector3();
	
	int height;
	
	public ChopJob(Human human, Vector3 target, int height, boolean persistent) {
		super(human, "mine" /* chop */, "Chopping a tree", Voxel.get("WOOD").getMining(), persistent);
		this.target.set(target);
		this.height = height;
		tool = ChopTool.class;
	}
	
	public Vector3 getTarget() {
		return target;
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		human.getIsland().set(target.x, target.y + height - 1, target.z, Voxel.get("AIR").getId());
		
		if (human.getCarryingItemStack().isNull()) human.setCarryingItemStack(new ItemStack(Item.getForId(Voxel.get("WOOD").getItemdrop()), 2));
		else human.getCarryingItemStack().add(2);
	}
}
