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

import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.creature.Human;

/**
 * @author Dakror
 */
public class PlaceEntityJob extends Job {
	private Entity entity;
	
	public PlaceEntityJob(Human human, Entity entity, boolean persistent) {
		super(human, "deposit", "Placing " + entity.getName(), 1, persistent);
		this.entity = entity;
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		
		human.getIsland().addEntity(entity, true, false);
	}
}
