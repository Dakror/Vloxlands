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
