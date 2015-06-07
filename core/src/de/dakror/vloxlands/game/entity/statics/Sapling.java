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


package de.dakror.vloxlands.game.entity.statics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.dakror.vloxlands.ai.state.StateTools;
import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.generate.Generator;
import de.dakror.vloxlands.util.math.Bits;

/**
 * @author Dakror
 */
public class Sapling extends StaticEntity {
	int growTicksLeft;
	
	public Sapling(float x, float y, float z) {
		super(x + 0.25f, y - 0.5f, z + 0.25f, "entities/sapling/sapling.g3db");
		
		name = "Sapling";
		weight = 1f;
		
		growTicksLeft = (int) (Game.dayInTicks * 2.5f);
	}
	
	@Override
	public void tick(int tick) {
		super.tick(tick);
		
		if (StateTools.isWorkingTime()) growTicksLeft--; // only grows in sunlight,
																											// so <code>initial time *
																											// 2</code> = real time is
																											// takes
		
		if (growTicksLeft <= 0) {
			Generator.generateTree(island, (int) voxelPos.x, (int) voxelPos.y - 1, (int) voxelPos.z);
			markedForRemoval = true;
		}
	}
	
	@Override
	public void save(ByteArrayOutputStream baos) throws IOException {
		super.save(baos);
		
		Bits.putInt(baos, growTicksLeft);
	}
}
