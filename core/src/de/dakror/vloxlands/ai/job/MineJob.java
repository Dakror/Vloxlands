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

import com.badlogic.gdx.Gdx;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.item.tool.MineTool;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.util.event.VoxelSelection;

/**
 * @author Dakror
 */
public class MineJob extends Job {
	private VoxelSelection target;
	
	public MineJob(Human human, VoxelSelection target, boolean persistent) {
		super(human, "mine", (persistent ? "Auto. m" : "M") + "ining " + target.type.getName(), target.type.getMining(), persistent);
		this.target = target;
		tool = MineTool.class;
	}
	
	public VoxelSelection getTarget() {
		return target;
	}
	
	@Override
	public void trigger(int tick) {
		animation = "mine" + (target.voxelPos.y - (human.getVoxelBelow().y + 1) == 0 ? "" : target.voxelPos.y - (human.getVoxelBelow().y + 1) < 0 ? "_lower" : "_upper");
		
		super.trigger(tick);
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		
		target.island.set(target.voxelPos.x, target.voxelPos.y, target.voxelPos.z, Voxel.get("AIR").getId());
		
		if (target.type.hasItemdrop()) {
			if (human.getCarryingItemStack().isNull()) human.setCarryingItemStack(new ItemStack(Item.getForId(target.type.getItemdrop()), 1));
			else human.getCarryingItemStack().add(1);
		} else Gdx.app.error("MineJob.onEnd", "Voxel " + target.type.getName() + " has no ItemDrop!");
	}
}
