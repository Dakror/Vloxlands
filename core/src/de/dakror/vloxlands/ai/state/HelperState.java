package de.dakror.vloxlands.ai.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.ai.MessageType;
import de.dakror.vloxlands.ai.path.AStar;
import de.dakror.vloxlands.ai.path.Path;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.NodeType;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.entity.structure.Warehouse;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.job.BuildJob;
import de.dakror.vloxlands.game.job.DepositJob;
import de.dakror.vloxlands.game.job.DismantleJob;
import de.dakror.vloxlands.game.job.PickupJob;
import de.dakror.vloxlands.game.query.PathBundle;
import de.dakror.vloxlands.game.query.Query;
import de.dakror.vloxlands.layer.GameLayer;

public enum HelperState implements State<Human>
{
	IDLE
	{},
	GET_RESOURCES_FOR_BUILD
	{
		Structure structure;
		boolean force = false;
		
		@Override
		public void update(Human human)
		{
			if (structure == null) return;
			if (human.isIdle() || force)
			{
				ItemStack is = structure.getBuildInventory().getFirst();
				if (is.isNull())
				{
					if (structure.isBuilt()) human.changeState(IDLE);
					else human.changeState(BUILD, structure);
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
						Gdx.app.error("HumanState.GET_RESOURCES_FOR_BUILD.update", "Didn't find a Warehouse containing the needed resources on island: 0!");
					}
				}
				
				Path p = AStar.findPath(pathStart, structure.getStructureNode(pathStart, NodeType.deposit).pos.cpy().add(structure.getVoxelPos()), human, NodeType.deposit.useGhostTarget);
				if (p != null)
				{
					DepositJob dj = new DepositJob(human, structure, false);
					if (queue) human.queueJob(p, dj);
					else human.setJob(p, dj);
				}
				else
				{
					Gdx.app.error("HumanState.GET_RESOURCES_FOR_BUILD.update", "Didn't find a path to target structure!");
				}
				
				force = false;
			}
		}
		
		@Override
		public boolean onMessage(Telegram telegram)
		{
			switch (MessageType.values()[telegram.message])
			{
				case PARAM0:
				{
					if (structure != (Structure) telegram.extraInfo)
					{
						structure = (Structure) telegram.extraInfo;
						force = true;
					}
					return true;
				}
				default:
					return false;
			}
		}
	},
	WALK_TO_TARGET
	{
		@Override
		public boolean onMessage(Telegram telegram)
		{
			switch (MessageType.values()[telegram.message])
			{
				case PARAM0:
				{
					Human h = (Human) telegram.sender;
					Path p = AStar.findPath(h.getVoxelBelow(), (Vector3) telegram.extraInfo, h, false);
					if (p != null) h.setJob(p, null);
					return true;
				}
				default:
					return false;
			}
		}
	},
	BUILD
	{
		Structure target;
		
		@Override
		public void update(Human human)
		{
			if (human.isIdle())
			{
				target.addWorker(human);
				human.changeState(IDLE);
			}
		}
		
		@Override
		public boolean onMessage(Telegram telegram)
		{
			switch (MessageType.values()[telegram.message])
			{
				case PARAM0:
				{
					target = (Structure) telegram.extraInfo;
					BuildJob bj = new BuildJob(((Human) telegram.sender), target, false);
					Vector3 pathStart = ((Human) telegram.sender).getVoxelBelow();
					boolean queue = StateTools.equipTool(((Human) telegram.sender), bj.getTool(), false, pathStart);
					Path p = AStar.findPath(pathStart, target.getStructureNode(pathStart, NodeType.build).pos.cpy().add(target.getVoxelPos()), ((Human) telegram.sender), NodeType.build.useGhostTarget);
					
					if (queue) ((Human) telegram.sender).queueJob(p, bj);
					else ((Human) telegram.sender).setJob(p, bj);
					return true;
				}
				default:
					return false;
			}
		}
	},
	DISMANTLE
	{
		Structure target;
		
		@Override
		public boolean onMessage(Telegram telegram)
		{
			switch (MessageType.values()[telegram.message])
			{
				case PARAM0:
				{
					target = (Structure) telegram.extraInfo;
					return true;
				}
				case PARAM1:
				{
					DismantleJob dj = new DismantleJob((Human) telegram.receiver, target, false);
					Vector3 pathStart = ((Human) telegram.receiver).getVoxelBelow();
					boolean queue = StateTools.equipTool((Human) telegram.receiver, dj.getTool(), false, pathStart);
					
					if (!queue) ((Human) telegram.receiver).setJob((Path) telegram.extraInfo, dj);
					else
					{
						Path p = AStar.findPath(pathStart, target.getStructureNode(pathStart, NodeType.build).pos.cpy().add(target.getVoxelPos()), ((Human) telegram.sender), NodeType.build.useGhostTarget);
						((Human) telegram.receiver).queueJob(p, dj);
					}
					return true;
				}
				default:
					return false;
			}
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
	public boolean onMessage(Telegram telegram)
	{
		switch (MessageType.values()[telegram.message])
		{
			case DISMANTLE_ME:
			{
				if (telegram.sender instanceof Structure)
				{
					((Human) telegram.receiver).changeState(DISMANTLE, telegram.sender, telegram.extraInfo);
					return true;
				}
				else return false;
			}
			default:
				return false;
		}
	}
}
