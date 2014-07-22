package de.dakror.vloxlands.game.job;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.item.tool.BuildTool;
import de.dakror.vloxlands.util.event.IEvent;

/**
 * @author Dakror
 */
public class BuildJob extends Job
{
	Structure target;
	
	public BuildJob(Human human, Structure target, boolean persistent)
	{
		super(human, "mine" /* build */, "Building " + target.getName(), 10, persistent);
		this.target = target;
		tool = BuildTool.class;
	}
	
	public Structure getTarget()
	{
		return target;
	}
	
	@Override
	public void tick(int tick)
	{}
	
	@Override
	public void onEnd()
	{
		super.onEnd();
		
		target.handleEvent(new IEvent()
		{
			@Override
			public Object getSender()
			{
				return BuildJob.this;
			}
			
			@Override
			public String getName()
			{
				return "onBuild";
			}
		});
	}
}
