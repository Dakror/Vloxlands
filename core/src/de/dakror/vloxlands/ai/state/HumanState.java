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
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.entity.structure.StructureNode.NodeType;
import de.dakror.vloxlands.game.entity.structure.Warehouse;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.job.DepositJob;
import de.dakror.vloxlands.game.job.Job;
import de.dakror.vloxlands.game.job.PickupJob;
import de.dakror.vloxlands.game.query.PathBundle;
import de.dakror.vloxlands.game.query.Query;
import de.dakror.vloxlands.layer.GameLayer;

public enum HumanState implements State<Entity>
{
	IDLE
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
	GET_RESOURCES_FOR_BUILD
	{
		Structure structure;
		boolean force = false;
		
		@Override
		public void enter(Entity entity)
		{}
		
		@Override
		public void update(Entity entity)
		{
			if (structure == null) return;
			if (((Human) entity).isIdle() || force)
			{
				ItemStack is = structure.getBuildInventory().getFirst();
				if (is.isNull())
				{
					entity.changeState(IDLE);
					return;
				}
				
				Vector3 pathStart = ((Human) entity).getVoxelBelow();
				
				PickupJob pj = new PickupJob((Human) entity, null, is, false, false);
				
				boolean queue = equipCorrectToolForJob((Human) entity, pj, false, pathStart);
				
				PathBundle pb = GameLayer.world.query(new Query((Human) entity).searchClass(Warehouse.class).structure(true).stack(is).node(NodeType.pickup).start(pathStart).island(0));
				if (pb != null)
				{
					pj.setTarget(structure);
					
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
		public void exit(Entity entity)
		{}
		
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
	
	;
	
	public boolean equipCorrectToolForJob(Human human, Job job, boolean queue, Vector3 pathStart)
	{
		if (!job.isUsingTool() && human.getTool().isNull()) return false;
		
		if (!job.isUsingTool() && !human.getTool().isNull())
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
			if (human.getTool().isNull() || !(human.getTool().getItem().getClass().isAssignableFrom(job.getTool())))
			{
				PathBundle pb = GameLayer.world.query(new Query(human).searchClass(Warehouse.class).structure(true).tool(job.getTool()).node(NodeType.pickup).island(0));
				if (pb != null)
				{
					PickupJob pj = new PickupJob(human, pb.structure, new ItemStack(pb.structure.getInventory().getAnyItemForToolType(job.getTool()), 1), true, false);
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
