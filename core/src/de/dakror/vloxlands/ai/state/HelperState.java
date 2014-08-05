package de.dakror.vloxlands.ai.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.ai.MessageType;
import de.dakror.vloxlands.ai.job.BuildJob;
import de.dakror.vloxlands.ai.job.DepositJob;
import de.dakror.vloxlands.ai.job.DismantleJob;
import de.dakror.vloxlands.ai.job.PickupJob;
import de.dakror.vloxlands.ai.path.AStar;
import de.dakror.vloxlands.ai.path.Path;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.NodeType;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.entity.structure.Warehouse;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.query.PathBundle;
import de.dakror.vloxlands.game.query.Query;
import de.dakror.vloxlands.layer.GameLayer;
import de.dakror.vloxlands.util.event.BroadcastPayload;

public enum HelperState implements State<Human>
{
	BUILD
	{
		@Override
		public void enter(Human human)
		{
			Structure target = (Structure) human.stateParams.get(0);
			BuildJob bj = new BuildJob(human, target, false);
			
			Vector3 pathStart = human.getVoxelBelow();
			boolean queue = StateTools.equipTool(human, bj.getTool(), false, pathStart);
			Path p = AStar.findPath(pathStart, target.getStructureNode(pathStart, NodeType.build).pos.cpy().add(target.getVoxelPos()), human, NodeType.build.useGhostTarget);
			
			if (queue) human.queueJob(p, bj);
			else human.setJob(p, bj);
		}
	},
	DISMANTLE
	{
		@Override
		public void enter(Human human)
		{
			Structure target = (Structure) human.stateParams.get(0);
			
			DismantleJob dj = new DismantleJob(human, target, false);
			Vector3 pathStart = human.getVoxelBelow();
			boolean queue = StateTools.equipTool(human, dj.getTool(), false, pathStart);
			
			Path p = AStar.findPath(pathStart, target.getStructureNode(pathStart, NodeType.build).pos.cpy().add(target.getVoxelPos()), human, NodeType.build.useGhostTarget);
			if (!queue) human.setJob(p, dj);
			else human.queueJob(p, dj);
		}
	},
	EMPTY_INVENTORY
	{},
	GET_RESOURCES_FOR_BUILD
	{
		@Override
		public void enter(Human human)
		{
			getNextResource(human);
		}
		
		void getNextResource(Human human)
		{
			Structure target = (Structure) human.stateParams.get(0);
			
			ItemStack is = target.getBuildInventory().getFirst();
			if (is.isNull())
			{
				human.changeState(IDLE);
				return;
			}
			
			Vector3 pathStart = human.getVoxelBelow();
			boolean queue = false;
			if (human.getCarryingItemStack().isNull() || human.getCarryingItemStack().getItem().getId() != is.getItem().getId())
			{
				PickupJob pj = new PickupJob(human, null, is, false, false);
				
				queue = StateTools.equipTool(human, pj.getTool(), queue, pathStart);
				
				PathBundle pb = GameLayer.world.query(new Query(human).searchClass(Warehouse.class).structure(true).stack(is).node(NodeType.pickup).start(pathStart).capacityForTransported(true).transport(human.getCarryingItemStack()).island(0));
				if (pb != null)
				{
					target.getBuildInventory().manageNext();
					pj.setTarget(pb.structure);
					
					if (!human.getCarryingItemStack().isNull())
					{
						DepositJob dj = new DepositJob(human, pb.structure, false);
						if (queue) human.queueJob(pb.path, dj);
						else
						{
							human.setJob(pb.path, dj);
							queue = true;
						}
						
						human.queueJob(null, pj);
					}
					else
					{
						if (queue) human.queueJob(pb.path, pj);
						else
						{
							human.setJob(pb.path, pj);
							queue = true;
						}
					}
					
					pathStart = pb.path.getLast();
				}
				else
				{
					Gdx.app.error("HelperState.GET_RESOURCES_FOR_BUILD.getNextResource", "Didn't find a Warehouse containing the needed resources on island: 0!");
				}
			}
			
			Path p = AStar.findPath(pathStart, target.getStructureNode(pathStart, NodeType.deposit).pos.cpy().add(target.getVoxelPos()), human, NodeType.deposit.useGhostTarget);
			if (p != null)
			{
				DepositJob dj = new DepositJob(human, target, false);
				if (queue) human.queueJob(p, dj);
				else human.setJob(p, dj);
			}
			else
			{
				Gdx.app.error("HelperState.GET_RESOURCES_FOR_BUILD.getNextResource", "Didn't find a path to target structure!");
			}
		}
		
		@Override
		public void update(Human human)
		{
			if (human.isIdle()) getNextResource(human);
		}
	},
	IDLE
	{
		@Override
		public boolean onMessage(Human human, Telegram telegram)
		{
			if (telegram.message == MessageType.STRUCTURE_BROADCAST.ordinal())
			{
				BroadcastPayload payload = (BroadcastPayload) telegram.extraInfo;
				if (payload.handled) return false;
				
				payload.handled = true;
				MessageDispatcher.getInstance().dispatchMessage(0, human, (Structure) payload.params[0], MessageType.STRUCTURE_BROADCAST_HANDLED.ordinal(), payload.state);
				human.changeState(payload.state, payload.params);
				
				return true;
			}
			
			return false;
		}
	},
	WALK_TO_TARGET
	{
		@Override
		public void enter(Human human)
		{
			Path p = AStar.findPath(human.getVoxelBelow(), (Vector3) human.stateParams.get(0), human, false);
			if (p != null) human.setJob(p, null);
		}
	},
	
	;
	
	@Override
	public void enter(Human human)
	{}
	
	@Override
	public void exit(Human human)
	{}
	
	@Override
	public void update(Human human)
	{
		if (human.isIdle() && human.getState() != IDLE) human.changeState(IDLE);
	}
	
	@Override
	public boolean onMessage(Human human, Telegram telegram)
	{
		return false;
	}
}
