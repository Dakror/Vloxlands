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
 

package de.dakror.vloxlands.util.event;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.world.Island;

/**
 * @author Dakror
 */
public interface SelectionListener {
	public void onVoxelSelection(VoxelSelection vs, boolean lmb);
	
	public void onVoxelRangeSelection(Island island, Vector3 start, Vector3 end, boolean lmb);
	
	public void onStructureSelection(Structure structure, boolean lmb);
	
	public void onCreatureSelection(Creature creature, boolean lmb);
	
	public void onNoSelection(boolean lmb);
}
