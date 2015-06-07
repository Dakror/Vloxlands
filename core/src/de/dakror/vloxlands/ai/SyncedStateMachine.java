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


package de.dakror.vloxlands.ai;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;

/**
 * @author Dakror
 */
public class SyncedStateMachine<E> extends DefaultStateMachine<E> {
	boolean newStateEntered;
	
	public SyncedStateMachine(E owner) {
		super(owner);
	}
	
	public SyncedStateMachine(E owner, State<E> initialState) {
		super(owner, initialState);
	}
	
	public SyncedStateMachine(E owner, State<E> initialState, State<E> globalState) {
		super(owner, initialState, globalState);
	}
	
	@Override
	public void changeState(State<E> newState) {
		newStateEntered = false;
		super.changeState(newState);
		newStateEntered = true;
	}
	
	@Override
	public void update() {
		// Execute the global state (if any)
		if (globalState != null) globalState.update(owner);
		
		// Execute the current state (if any)
		if (currentState != null && newStateEntered) currentState.update(owner);
	}
}
