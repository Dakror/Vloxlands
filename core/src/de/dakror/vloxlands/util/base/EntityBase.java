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


package de.dakror.vloxlands.util.base;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.util.event.SelectionListener;
import de.dakror.vloxlands.util.event.VoxelSelection;
import de.dakror.vloxlands.util.interf.Tickable;

/**
 * @author Dakror
 */
public abstract class EntityBase implements Tickable, Disposable, SelectionListener {
	@Override
	public void onVoxelSelection(VoxelSelection vs, boolean lmb) {}
	
	@Override
	public void onStructureSelection(Structure structure, boolean lmb) {}
	
	@Override
	public void onCreatureSelection(Creature creature, boolean lmb) {}
	
	@Override
	public void onVoxelRangeSelection(Island island, Vector3 start, Vector3 end, boolean lmb) {}
	
	@Override
	public void onNoSelection(boolean lmb) {}
	
	@Override
	public void dispose() {}
	
	@Override
	public void tick(int tick) {}
}
