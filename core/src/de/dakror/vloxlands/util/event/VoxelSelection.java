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

import de.dakror.vloxlands.game.query.VoxelPos;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.util.Direction;

/**
 * @author Dakror
 */
public class VoxelSelection {
	public Island island;
	public VoxelPos voxelPos;
	public Direction face;
	public Voxel type;
	
	public VoxelSelection(Island island, VoxelPos voxelPos, Direction face) {
		this.island = island;
		this.voxelPos = voxelPos;
		type = Voxel.getForId(voxelPos.b);
		this.face = face;
	}
}
