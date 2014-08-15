package de.dakror.vloxlands.ai.state;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.Config;
import de.dakror.vloxlands.ai.job.ChopJob;
import de.dakror.vloxlands.ai.job.DepositJob;
import de.dakror.vloxlands.ai.job.EnterStructureJob;
import de.dakror.vloxlands.ai.job.Job;
import de.dakror.vloxlands.ai.job.PlaceEntityJob;
import de.dakror.vloxlands.ai.job.RemoveLeavesJob;
import de.dakror.vloxlands.ai.path.AStar;
import de.dakror.vloxlands.ai.path.BFS;
import de.dakror.vloxlands.ai.path.BFSConfig;
import de.dakror.vloxlands.ai.path.Path;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.statics.Sapling;
import de.dakror.vloxlands.game.entity.structure.NodeType;
import de.dakror.vloxlands.game.voxel.MetaTags;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.util.event.Callback;

/**
 * @author Dakror
 */
public enum WorkerState implements State<Human>
{
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
				else human.revertToPreviousState();
			}
		}
	},
	LUMBERJACK
	{
		final float range = 30;
		byte wood;
		
		@Override
		public void enter(Human human)
		{
			super.enter(human);
			
			wood = Voxel.get("WOOD").getId();
			
			human.stateParams.add(0); // lastTargetMetadata
			human.stateParams.add(new Vector3(-1, 0, 0)); // lastTarget
			
			if (chop(human)) human.setLocation(null);
			else human.changeState(REST);
		}
		
		public boolean chop(final Human human)
		{
			if (human.getWorkPlace().getInventory().isFull()) return false;
			Path path = null;
			
			if (((Vector3) human.stateParams.get(1)).x == -1)
			{
				path = BFS.findClosestVoxel(human.getVoxelBelow(), new BFSConfig(human).voxel(wood).range(range).closest(true).notmeta(MetaTags.LUMBERJACK_TARGET).notneighbor(MetaTags.LUMBERJACK_TARGET).neighborrange(2, 2, 2));
				if (path == null) return false;
				
				human.getIsland().setMeta(path.getGhostTarget().x, path.getGhostTarget().y, path.getGhostTarget().z, MetaTags.LUMBERJACK_TARGET);
				((Vector3) human.stateParams.get(1)).set(path.getGhostTarget());
				int height = getTreeHeight(human, path.getGhostTarget());
				human.stateParams.set(0, height);
			}
			else
			{
				path = AStar.findPath(human.getVoxelBelow(), ((Vector3) human.stateParams.get(1)), human, range, true);
				if (path == null)
				{
					((Vector3) human.stateParams.get(1)).x = -1;
					return false;
				}
			}
			
			RemoveLeavesJob rmj = new RemoveLeavesJob(human, ((Vector3) human.stateParams.get(1)), ((Integer) human.stateParams.get(0)), false);
			ChopJob cj = new ChopJob(human, ((Vector3) human.stateParams.get(1)), ((Integer) human.stateParams.get(0)), false);
			cj.setEndEvent(new Callback()
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
			human.stateParams.set(0, (Integer) human.stateParams.get(0) - 1);
			if ((Integer) human.stateParams.get(0) < 0) ((Vector3) human.stateParams.get(0)).x = -1;
			
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
	FORESTER
	{
		final int checkHeight = 7;
		final int checkRadius = 1;
		final int range = 20;
		final int timeout = 60 * 1000;
		
		@Override
		public void enter(final Human human)
		{
			super.enter(human);
			
			human.stateParams.clear();
			
			Vector3 v = null;
			for (int i = 0; i < 3; i++) // try 3 times
			{
				if ((v = pickRandomSpot(human)) != null) break;
			}
			
			if (v != null)
			{
				
				Path p = AStar.findPath(human.getVoxelBelow(), v, human, true);
				if (p == null)
				{
					human.stateParams.add(System.currentTimeMillis());
					return;
				}
				human.setJob(p, new PlaceEntityJob(human, new Sapling(v.x, v.y + 1, v.z), false));
				Job j = new EnterStructureJob(human, human.getWorkPlace(), false);
				j.setEndEvent(new Callback()
				{
					@Override
					public void trigger()
					{
						human.stateParams.add(System.currentTimeMillis());
					}
				});
				human.queueJob(StateTools.getHomePath(human, p.getLast(), NodeType.entry), j);
				
				human.setLocation(null);
			}
			else human.stateParams.add(System.currentTimeMillis());
		}
		
		public Vector3 pickRandomSpot(Human human)
		{
			Vector3 vp = human.getVoxelBelow();
			Vector3 v = new Vector3(MathUtils.random(-range, range), 1337 /* hehe */, MathUtils.random(-range, range));
			
			for (int y = -2; y < 2; y++)
			{
				if (human.getIsland().isSpaceAbove(v.x + vp.x, y + vp.y, v.z + vp.z, human.getHeight()))
				{
					v.y = y;
					break;
				}
			}
			
			if (v.y == 1337)
			{
				return null;
			}
			
			v.add(vp);
			
			byte b = human.getIsland().get(v.x, v.y, v.z);
			
			if (b != Voxel.get("DIRT").getId() && b != Voxel.get("GRASS").getId()) return null;
			
			for (int i = -checkRadius; i <= checkRadius; i++)
				for (int j = -checkRadius; j <= checkRadius; j++)
					for (int k = 1; k < checkHeight; k++)
						if (Voxel.getForId(human.getIsland().get(i + v.x, k + v.y, j + v.z)).isOpaque()) return null;
			
			return v;
		}
		
		@Override
		public void update(Human human)
		{
			super.update(human);
			
			if (human.stateParams.size > 0 && System.currentTimeMillis() - (Long) human.stateParams.get(0) >= timeout / Config.getGameSpeed())
			{
				human.changeState(REST);
			}
		}
	},
	FARMER
	{},
	REST
	{
		@Override
		public void enter(Human human)
		{
			human.stateParams.add(0l);
			human.setLocation(human.getWorkPlace());
		}
		
		@Override
		public void update(Human human)
		{
			if (System.currentTimeMillis() - (Long) human.stateParams.get(0) >= 5000f / Config.getGameSpeed())
			{
				if (StateTools.isWorkingTime() && !human.getWorkPlace().getInventory().isFull()) human.revertToPreviousState();
				else human.stateParams.set(0, System.currentTimeMillis());
			}
		}
	},
	
	;
	
	@Override
	public void enter(Human human)
	{
		if (human.getWorkPlace().getInventory().isFull()) human.changeState(REST);
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
	public boolean onMessage(Human human, Telegram telegram)
	{
		return false;
	}
}
