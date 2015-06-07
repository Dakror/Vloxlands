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


package de.dakror.vloxlands.generate;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.game.world.World;
import de.dakror.vloxlands.generate.biome.Biome;
import de.dakror.vloxlands.generate.biome.BiomeType;

public class IslandGenerator {
	public static final int MIN = 48;
	public static final int MAX = 64;
	
	public static Island generate(WorldGenerator worldGen) {
		try {
			float yPos = World.MAX_HEIGHT / 2 * MathUtils.random() + World.MAX_HEIGHT / 4;
			int radius = MathUtils.random(MIN, MAX);
			
			BiomeType biome = BiomeType.values()[(int) (MathUtils.random() * BiomeType.values().length)];
			Biome gen = (Biome) Class.forName("de.dakror.vloxlands.generate.biome." + biome.getName().replace(" ", "") + "Biome").newInstance();
			
			Island island = new Island(biome);
			
			worldGen.step();
			island.setPos(new Vector3(0, yPos, 0));
			gen.generate(worldGen, island, radius);
			
			island.grassify();
			island.calculateInitBalance();
			worldGen.step();
			
			return island;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
