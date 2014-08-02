package de.dakror.vloxlands.ai.state;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

import de.dakror.vloxlands.game.entity.Entity;

/**
 * @author Dakror
 */
public enum LumberjackState implements State<Entity>
{
	;
	
	@Override
	public void enter(Entity entity)
	{}
	
	@Override
	public void update(Entity entity)
	{}
	
	@Override
	public void exit(Entity entity)
	{}
	
	@Override
	public boolean onMessage(Telegram telegram)
	{
		return false;
	}
	
}
