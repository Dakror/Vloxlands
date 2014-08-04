package de.dakror.vloxlands.game.job;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.item.tool.BuildTool;
import de.dakror.vloxlands.util.event.Payload;

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
		target.handleEvent(new Payload()
		{
			
			@Override
			public Object getSender()
			{
				return DismantleJob.this;
			}
			
			@Override
			public String getName()
			{
				return "onDismantle";
			}
		});
	}
}
