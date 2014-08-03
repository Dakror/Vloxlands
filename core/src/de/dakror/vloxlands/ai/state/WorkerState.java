package de.dakror.vloxlands.ai.state;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.NodeType;
import de.dakror.vloxlands.game.job.DepositJob;
import de.dakror.vloxlands.game.job.Job;

/**
 * @author Dakror
 */
public enum WorkerState implements State<Human>
{
	LUMBERJACK
	{
		@Override
		public void enter(Human human)
		{
			super.enter(human);
			
			human.setLocation(null);
		}
		
		@Override
		public void update(Human human)
		{
			super.update(human);
			
			if (human.getCarryingItemStack().isFull())
			{
				human.setJob(StateTools.getHomePath(human, NodeType.deposit), new DepositJob(human, human.getWorkPlace(), false));
			}
			
			if (human.isIdle())
			{	
				
			}
		}
	},
	TIDY_UP
	{
		@Override
		public void enter(final Human human)
		{
			Job job = null;
			if (!human.getCarryingItemStack().isNull())
			{
				job = new DepositJob(human, human.getWorkPlace(), false);
			}
			human.setJob(StateTools.getHomePath(human, NodeType.deposit), job);
		}
		
		@Override
		public void update(Human human)
		{
			if (human.isIdle()) human.changeState(REST);
		}
	},
	REST
	{
		@Override
		public void enter(Human human)
		{
			human.setLocation(human.getWorkPlace());
		}
		
		@Override
		public void update(Human human)
		{
			if (StateTools.isWorkingTime() && !human.getWorkPlace().getInventory().isFull()) human.changeState(human.getWorkPlace().getWorkerState());
		}
	},
	
	;
	
	@Override
	public void enter(Human human)
	{
		if (human.getWorkPlace().getInventory().isFull())
		{
			human.changeState(REST);
		}
	}
	
	@Override
	public void update(Human human)
	{
		if (!StateTools.isWorkingTime())
		{
			if (human.getState() != TIDY_UP && human.getState() != REST) human.changeState(TIDY_UP);
		}
	}
	
	@Override
	public void exit(Human human)
	{}
	
	@Override
	public boolean onMessage(Telegram telegram)
	{
		return false;
	}
	
}
