package de.dakror.vloxlands.ai.state;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.ai.path.AStar;
import de.dakror.vloxlands.ai.path.Path;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.NodeType;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.entity.structure.Warehouse;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.item.tool.ChopTool;
import de.dakror.vloxlands.game.job.EnterStructureJob;
import de.dakror.vloxlands.game.job.PickupJob;
import de.dakror.vloxlands.game.query.PathBundle;
import de.dakror.vloxlands.game.query.Query;
import de.dakror.vloxlands.layer.GameLayer;

/**
 * @author Dakror
 */
public class StateTools
{
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
	
	public static void initForWorkplace(Human human, Class<?> tool)
	{
		Vector3 pathStart = human.getVoxelBelow();
		boolean queue = StateTools.equipTool(human, ChopTool.class, false, pathStart);
		
		Structure workPlace = human.getWorkPlace();
		EnterStructureJob esj = new EnterStructureJob(human, workPlace, false);
		
		if (queue)
		{
			Path p = AStar.findPath(pathStart, workPlace.getStructureNode(pathStart, NodeType.entry).pos.cpy().add(workPlace.getVoxelPos()), human, NodeType.entry.useGhostTarget);
			human.queueJob(p, esj);
		}
		else human.setJob(null, esj);
	}
	
	public static Path getHomePath(Human human, NodeType nodeType)
	{
		return AStar.findPath(human.getVoxelBelow(), human.getWorkPlace().getStructureNode(human.getVoxelBelow(), nodeType).pos.cpy().add(human.getWorkPlace().getVoxelPos()), human, nodeType.useGhostTarget);
	}
	
	public static boolean isWorkingTime()
	{
		return GameLayer.time > 0;
	}
}
