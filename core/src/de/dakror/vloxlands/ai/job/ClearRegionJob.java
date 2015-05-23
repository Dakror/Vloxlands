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
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.world.Island;

/**
 * @author Dakror
 */
public class ClearRegionJob extends Job {
	Vector3 start, end;
	Array<Vector3> targets;
	Island island;
	
	public ClearRegionJob(Human human, Island island, Vector3 start, Vector3 end, boolean persistent) {
		super(human, null, "Clearing a " + (int) (Math.max(start.x, end.x) - Math.min(start.x, end.x) + 1) + " x " + (int) (Math.max(start.y, end.y) - Math.min(start.y, end.y) + 1) + " x " + (int) (Math.max(start.z, end.z) - Math.min(start.z, end.z) + 1) + " region", 1, persistent);
		this.island = island;
		this.start = start;
		this.end = end;
		
		targets = new Array<Vector3>();
	}
}
