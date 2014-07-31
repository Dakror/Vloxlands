package de.dakror.vloxlands.ai.state;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

import de.dakror.vloxlands.game.entity.Entity;

public enum HumanState implements State<Entity>
{
	idle
	{
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
	},
	transport
	{
		@Override
		public void enter(Entity entity)
		{
			System.out.println("enter");
		}
		
		@Override
		public void update(Entity entity)
		{}
		
		@Override
		public void exit(Entity entity)
		{}
		
		@Override
		public boolean onMessage(Telegram telegram)
		{
			System.out.println("onMessage");
			return false;
		}
	},
	;
}