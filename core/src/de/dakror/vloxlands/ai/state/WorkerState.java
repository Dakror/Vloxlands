package de.dakror.vloxlands.ai.state;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.ai.job.ChopJob;
import de.dakror.vloxlands.ai.job.DepositJob;
import de.dakror.vloxlands.ai.job.Job;
import de.dakror.vloxlands.ai.job.RemoveLeavesJob;
import de.dakror.vloxlands.ai.path.AStar;
import de.dakror.vloxlands.ai.path.BFS;
import de.dakror.vloxlands.ai.path.Path;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.NodeType;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.util.event.Event;

/**
 * @author Dakror
 */
public enum WorkerState implements State<Human>
{
	LUMBERJACK
	{
		int lastTargetInitialMetadata = 0;
		int lastTargetMetadata = 0;
		final Vector3 lastTarget = new Vector3(-1, 0, 0);
		final float range = 30;
		byte wood;
		
		@Override
		public void enter(Human human)
		{
			super.enter(human);
			
			wood = Voxel.get("WOOD").getId();
			
			if (chop(human)) human.setLocation(null);
			else human.changeState(REST);
		}
		
		public boolean chop(final Human human)
		{
			if (human.getWorkPlace().getInventory().isFull()) return false;
			Path path = null;
			
			if (lastTarget.x == -1)
			{
				path = BFS.findClosestVoxel(human.getVoxelBelow(), wood, range, true, human);
				if (path == null) return false;
				
				lastTarget.set(path.getGhostTarget());
				lastTargetMetadata = lastTargetInitialMetadata = getTreeHeight(human, lastTarget);
			}
			else
			{
				path = AStar.findPath(human.getVoxelBelow(), lastTarget, human, range, true);
				if (path == null)
				{
					lastTarget.x = -1;
					return false;
				}
			}
			
			RemoveLeavesJob rmj = new RemoveLeavesJob(human, lastTarget, lastTargetInitialMetadata, false);
			ChopJob cj = new ChopJob(human, lastTarget, lastTargetMetadata, false);
			cj.setEndEvent(new Event()
			{
				@Override
				public void trigger()
				{
					afterChop(human);
				}
			});
			
			human.setJob(path, rmj);
			human.queueJob(null, cj);
			
			return true;
		}
		
		public void afterChop(Human human)
		{
			lastTargetMetadata--;
			if (lastTargetMetadata < 0) lastTarget.x = -1;
			
			human.changeState(BRING_STUFF_HOME);
		}
		
		public int getTreeHeight(Human human, Vector3 pos)
		{
			int height = 0;
			for (int y = (int) pos.y; y < Island.SIZE; y++)
			{
				if (human.getIsland().get(pos.x, y, pos.z) == wood) height++;
				else break;
			}
			
			return height;
		}
	},
	BRING_STUFF_HOME
	{
		@Override
		public void enter(final Human human)
		{
			Job job = null;
			if (!human.getCarryingItemStack().isNull())
			{
				job = new DepositJob(human, human.getWorkPlace(), false);
			}
			
			Path p = StateTools.getHomePath(human, NodeType.deposit);
			human.setJob(p, job);
		}
		
		@Override
		public void update(Human human)
		{
			if (human.isIdle())
			{
				if (!StateTools.isWorkingTime()) human.changeState(REST);
				else human.getStateMachine().revertToPreviousState();
			}
		}
	},
	REST
	{
		long lastCheck = 0;
		
		@Override
		public void enter(Human human)
		{
			human.setLocation(human.getWorkPlace());
		}
		
		@Override
		public void update(Human human)
		{
			if (System.currentTimeMillis() - lastCheck >= 5000)
			{
				if (StateTools.isWorkingTime() && !human.getWorkPlace().getInventory().isFull()) human.changeState(human.getWorkPlace().getWorkerState());
				
				lastCheck = System.currentTimeMillis();
			}
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
			if (human.getState() != BRING_STUFF_HOME && human.getState() != REST) human.changeState(BRING_STUFF_HOME);
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
