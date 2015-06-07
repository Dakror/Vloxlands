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
import de.dakror.vloxlands.game.voxel.Voxel;

/**
 * @author Dakror
 */
public class PlaceJob extends Job {
	Vector3 target;
	Voxel voxel;
	
	public PlaceJob(Human human, Vector3 target, Voxel voxel, boolean persistent) {
		super(human, "deposit", "Placing " + voxel.getName(), 1, persistent);
		this.target = target;
		this.voxel = voxel;
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		
		human.getIsland().set(target.x, target.y, target.z, voxel.getId());
	}
	
	public Vector3 getTarget() {
		return target;
	}
}
