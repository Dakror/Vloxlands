package de.dakror.vloxlands.ai.state;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.ai.path.AStar;
import de.dakror.vloxlands.ai.path.Path;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.NodeType;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.item.tool.ChopTool;
import de.dakror.vloxlands.game.job.EnterStructureJob;

/**
 * @author Dakror
 */
public enum LumberjackState implements State<Entity>
{
	INIT
	{
		@Override
		public void enter(Entity entity)
		{
			Vector3 pathStart = ((Human) entity).getVoxelBelow();
			boolean queue = HelperState.equipTool((Human) entity, ChopTool.class, false, pathStart);
			
			Structure workPlace = ((Human) entity).getWorkPlace();
			EnterStructureJob esj = new EnterStructureJob((Human) entity, workPlace, false);
			
			if (queue)
			{
				Path p = AStar.findPath(pathStart, workPlace.getStructureNode(pathStart, NodeType.entry).pos.cpy().add(workPlace.getVoxelPos()), (Human) entity, NodeType.entry.useGhostTarget);
				((Human) entity).queueJob(p, esj);
			}
			else ((Human) entity).setJob(null, esj);
		}
	},
	
	;
	
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
	
}
