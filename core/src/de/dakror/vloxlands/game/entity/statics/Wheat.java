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

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.ai.state.StateTools;
import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.game.voxel.MetaTags;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.util.math.Bits;

/**
 * @author Dakror
 */
public class Wheat extends StaticEntity {
	int growTicks;
	int growTicksLeft;
	boolean managed;
	int level;
	int perLevel;
	
	public static int itemsForHarvest = 5;
	
	public Wheat(float x, float y, float z) {
		super(x, y, z, "entities/wheat/wheat0[16].vxi");
		growTicks = growTicksLeft = Game.dayInTicks;
		perLevel = growTicks / 5;
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		
		level = 0;
		island.set(voxelPos.x, voxelPos.y, voxelPos.z, Voxel.get("ACRE").getId());
		island.setMeta(voxelPos.x, voxelPos.y, voxelPos.z, MetaTags.ACRE_PLANT_GROWING);
	}
	
	@Override
	public void tick(int tick) {
		super.tick(tick);
		
		if (level < 4) {
			if (StateTools.isWorkingTime()) growTicksLeft--; // only grows in sunlight, so <code>initial time * 2</code> = real time is takes
			
			if (growTicksLeft % perLevel == 0 && growTicksLeft != growTicks) {
				level++;
				Matrix4 tr = modelInstance.transform.cpy().translate(-blockTrn.x, -blockTrn.y, -blockTrn.z);
				modelInstance = new ModelInstance(Vloxlands.assets.get("models/" + "entities/wheat/wheat" + level + "[16].vxi", Model.class));
				modelInstance.transform.set(tr);
				modelInstance.calculateBoundingBox(boundingBox);
				if (boundingBox.getDimensions().x <= 1 || boundingBox.getDimensions().y <= 1 || boundingBox.getDimensions().z <= 1) {
					blockTrn.set(((float) Math.ceil(boundingBox.getDimensions().x) - boundingBox.getDimensions().x) / 2, 1 - boundingBox.getCenter().y, ((float) Math.ceil(boundingBox.getDimensions().z) - boundingBox.getDimensions().z) / 2);
				}
				blockTrn.add(boundingBox.getDimensions().cpy().scl(0.5f));
				modelInstance.transform.translate(blockTrn);
			}
			
			if (level == 4) island.setMeta(voxelPos.x, voxelPos.y, voxelPos.z, MetaTags.ACRE_PLANT_GROWN);
		}
	}
	
	@Override
	public void save(ByteArrayOutputStream baos) throws IOException {
		super.save(baos);
		
		Bits.putInt(baos, growTicksLeft);
		Bits.putBoolean(baos, managed);
	}
	
	public boolean isManaged() {
		return managed;
	}
	
	public void setManaged(boolean managed) {
		this.managed = managed;
	}
}
