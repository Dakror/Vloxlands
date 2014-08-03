package de.dakror.vloxlands.ai.state;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.NodeType;
import de.dakror.vloxlands.game.job.DepositJob;
import de.dakror.vloxlands.game.job.Job;

/**
 * @author Dakror
 */
public enum WorkerState implements State<Entity>
{
	LUMBERJACK
	{
		@Override
		public void enter(Entity entity)
		{
			super.enter(entity);
			
			((Human) entity).setLocation(null);
		}
		
		@Override
		public void update(Entity entity)
		{
			super.update(entity);
			
			if (((Human) entity).getCarryingItemStack().isFull())
			{
				((Human) entity).setJob(StateTools.getHomePath((Human) entity, NodeType.deposit), new DepositJob((Human) entity, ((Human) entity).getWorkPlace(), false));
			}
		}
	},
	TIDY_UP
	{
		@Override
		public void enter(final Entity entity)
		{
			Job job = null;
			if (!((Human) entity).getCarryingItemStack().isNull())
			{
				job = new DepositJob((Human) entity, ((Human) entity).getWorkPlace(), false);
			}
			((Human) entity).setJob(StateTools.getHomePath((Human) entity, NodeType.deposit), job);
		}
		
		@Override
		public void update(Entity entity)
		{
			if (((Human) entity).isIdle()) entity.changeState(REST);
		}
	},
	REST
	{
		@Override
		public void enter(Entity entity)
		{
			((Human) entity).setLocation(((Human) entity).getWorkPlace());
		}
		
		@Override
		public void update(Entity entity)
		{
			if (StateTools.isWorkingTime() && !((Human) entity).getWorkPlace().getInventory().isFull()) entity.changeState(((Human) entity).getWorkPlace().getWorkerState());
		}
	},
	
	;
	
	@Override
	public void enter(Entity entity)
	{
		if (((Human) entity).getWorkPlace().getInventory().isFull())
		{
			entity.changeState(REST);
		}
	}
	
	@Override
	public void update(Entity entity)
	{
		if (!StateTools.isWorkingTime())
		{
			if (entity.getState() != TIDY_UP && entity.getState() != REST) entity.changeState(TIDY_UP);
		}
	}
	
	@Override
	public void exit(Entity entity)
	{}
	
	@Override
	public boolean onMessage(Telegram telegram)
	{
		return false;
	}
	
}
