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

import de.dakror.vloxlands.ai.state.HelperState;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.util.interf.provider.InventoryProvider;

/**
 * @author Dakror
 */
public class DepositJob extends Job {
	private Structure target;
	
	public DepositJob(Human human, Structure target, boolean persistent) {
		super(human, "deposit", "Depositing carried items", 1, persistent);
		this.target = target;
	}
	
	public InventoryProvider getTarget() {
		return target;
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		
		if (!target.isBuilt()) {
			target.getInventory().take(human.getCarryingItemStack().getItem(), human.getCarryingItemStack().getAmount());
			human.setCarryingItemStack(new ItemStack());
			if (target.getInventory().getCount() == 0 && target.getCosts().getCount() > 0) target.broadcast(HelperState.BUILD);
			
		} else human.setCarryingItemStack(target.getInventory().add(human.getCarryingItemStack()));
	}
}
