package de.dakror.vloxlands.util.event;

import com.badlogic.gdx.ai.fsm.State;

import de.dakror.vloxlands.game.entity.creature.Human;

/**
 * @author Dakror
 */
public class BroadcastPayload {
	public State<Human> state;
	public Object[] params;
	public boolean handled;
	
	public BroadcastPayload(State<Human> state, Object... params) {
		this.state = state;
		this.params = params;
	}
}
