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

import com.badlogic.gdx.ai.msg.MessageDispatcher;

import de.dakror.vloxlands.ai.MessageType;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.item.tool.BuildTool;

/**
 * @author Dakror
 */
public class DismantleJob extends Job {
	private Structure target;
	
	public DismantleJob(Human human, Structure target, boolean persistent) {
		super(human, "mine" /* build */, "Dismantling " + target.getName(), 10, persistent);
		this.target = target;
		tool = BuildTool.class;
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		MessageDispatcher.getInstance().dispatchMessage(human, target, MessageType.YOU_ARE_DISMANTLED.ordinal());
	}
}
