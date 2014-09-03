package de.dakror.vloxlands.ai.job;

import com.badlogic.gdx.ai.msg.MessageDispatcher;

import de.dakror.vloxlands.ai.MessageType;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.item.tool.BuildTool;

/**
 * @author Dakror
 */
public class DismantleJob extends Job
{
	private Structure target;
	
	public DismantleJob(Human human, Structure target, boolean persistent)
	{
		super(human, "mine" /* build */, "Dismantling " + target.getName(), 10, persistent);
		this.target = target;
		tool = BuildTool.class;
	}
	
	@Override
	public void onEnd()
	{
		super.onEnd();
		MessageDispatcher.getInstance().dispatchMessage(human, target, MessageType.YOU_ARE_DISMANTLED.ordinal());
	}
}
