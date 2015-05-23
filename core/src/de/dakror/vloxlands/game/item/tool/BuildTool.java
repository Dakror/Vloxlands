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
 

package de.dakror.vloxlands.game.item.tool;

import com.badlogic.gdx.math.Matrix4;

import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.creature.Human;

/**
 * @author Dakror
 */
public class BuildTool extends Tool {
	@Override
	public void transformInHand(Matrix4 transform, Creature c) {
		if (((Human) c).firstJob() != null && ((Human) c).firstJob().isUsingTool() && ((Human) c).firstJob().getTool().isAssignableFrom(getClass())) super.transformInHand(transform, c, 40);
		else super.transformInHand(transform, c);
	}
}
