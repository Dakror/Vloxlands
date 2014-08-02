package de.dakror.vloxlands.ai.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.ai.MessageType;
import de.dakror.vloxlands.ai.path.AStar;
import de.dakror.vloxlands.ai.path.Path;
import de.dakror.vloxlands.game.entity.Entity;
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

public enum HelperState implements State<Entity>
{
	IDLE
	{},
	GET_RESOURCES_FOR_BUILD
	{
		Structure structure;
		boolean force = false;
		
		@Override
		public void update(Entity entity)
		{
			if (structure == null) return;
			if (((Human) entity).isIdle() || force)
			{
				ItemStack is = structure.getBuildInventory().getFirst();
				if (is.isNull())
				{
					if (structure.isBuilt()) entity.changeState(IDLE);
					else entity.changeState(BUILD, structure);
					return;
				}
				
				Vector3 pathStart = ((Human) entity).getVoxelBelow();
				
				PickupJob pj = new PickupJob((Human) entity, null, is, false, false);
				
				boolean queue = equipTool((Human) entity, pj.getTool(), false, pathStart);
				
				PathBundle pb = GameLayer.world.query(new Query((Human) entity).searchClass(Warehouse.class).structure(true).stack(is).node(NodeType.pickup).start(pathStart).island(0));
				if (pb != null)
				{
					pj.setTarget(pb.structure);
					
					if (queue) ((Human) entity).queueJob(pb.path, pj);
					else ((Human) entity).setJob(pb.path, pj);
				}
				else
				{
					Gdx.app.error("HumanState.GET_RESOURCES_FOR_BUILD.update", "Didn't find a Warehouse containing the needed resources on island: 0!");
				}
				
				Path p = AStar.findPath(pb.path.getLast(), structure.getStructureNode(pb.path.getLast(), NodeType.deposit).pos.cpy().add(structure.getVoxelPos()), (Human) entity, NodeType.deposit.useGhostTarget);
				if (p != null)
				{
					DepositJob dj = new DepositJob((Human) entity, structure, false);
					((Human) entity).queueJob(p, dj);
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
					structure = (Structure) telegram.extraInfo;
					force = true;
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
		public void update(Entity entity)
		{
			if (((Human) entity).isIdle())
			{
				target.addWorker((Human) entity);
				entity.changeState(IDLE);
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
					boolean queue = equipTool(((Human) telegram.sender), bj.getTool(), false, pathStart);
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
					boolean queue = equipTool((Human) telegram.receiver, dj.getTool(), false, pathStart);
					
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
	public void enter(Entity entity)
	{}
	
	@Override
	public void exit(Entity entity)
	{}
	
	@Override
	public void update(Entity entity)
	{
		if (((Human) entity).isIdle()) entity.changeState(IDLE);
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
	
	public static boolean equipTool(Human human, Class<?> tool, boolean queue, Vector3 pathStart)
	{
		if (tool == null && human.getTool().isNull()) return false;
		
		if (tool == null && !human.getTool().isNull())
		{
			PathBundle pb = GameLayer.world.query(new Query(human).searchClass(Warehouse.class).structure(true).capacityForTransported(true).transport(human.getTool()).node(NodeType.deposit).island(0));
			if (pb != null)
			{
				PickupJob pj = new PickupJob(human, pb.structure, new ItemStack(), true, false);
				if (!queue) human.setJob(pb.path, pj);
				else human.queueJob(pb.path, pj);
				
				if (pb.path.getLast() != null) pathStart.set(pb.path.getLast());
				
				return true;
			}
		}
		else
		{
			if (human.getTool().isNull() || !(human.getTool().getItem().getClass().isAssignableFrom(tool)))
			{
				PathBundle pb = GameLayer.world.query(new Query(human).searchClass(Warehouse.class).structure(true).tool(tool).node(NodeType.pickup).island(0));
				if (pb != null)
				{
					PickupJob pj = new PickupJob(human, pb.structure, new ItemStack(pb.structure.getInventory().getAnyItemForToolType(tool), 1), true, false);
					if (!queue) human.setJob(pb.path, pj);
					else human.queueJob(pb.path, pj);
					
					if (pb.path.getLast() != null) pathStart.set(pb.path.getLast());
					
					return true;
				}
			}
		}
		
		return false;
	}
}
