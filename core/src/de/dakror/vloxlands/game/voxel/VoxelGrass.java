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


package de.dakror.vloxlands.game.voxel;

import com.badlogic.gdx.math.Vector2;

import de.dakror.vloxlands.util.Direction;

public class VoxelGrass extends Voxel {
	@Override
	protected Vector2 getTexCoord(int x, int y, int z, Direction d) {
		Vector2 v = super.getTexCoord(x, y, z, d);
		if (d == Direction.UP) return v;
		if (d == Direction.DOWN) return Voxel.get("DIRT").getTexCoord(x, y, z, d);
		v.x++;
		return v;
	}
}
