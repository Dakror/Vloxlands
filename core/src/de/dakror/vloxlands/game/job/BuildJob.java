package de.dakror.vloxlands.game.job;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.item.tool.BuildTool;

/**
 * @author Dakror
 */
public class BuildJob extends Job
{
	Structure target;
	
	public BuildJob(Human human, Structure target, boolean persistent)
	{
		super(human, "mine" /* build */, "Building " + target.getName(), -1, persistent);
		this.target = target;
		tool = BuildTool.class;
	}
	
	public Structure getTarget()
	{
		return target;
	}
	
	@Override
	public void tick(int tick)
	{
		if ((tick - startTick) % durationInTicks == 0 && tick > startTick)
		{
			if (target.progressBuild()) done = true;
		}
	}
}
