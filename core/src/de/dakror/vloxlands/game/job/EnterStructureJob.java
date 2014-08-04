package de.dakror.vloxlands.game.job;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;

/**
 * @author Dakror
 */
public class EnterStructureJob extends Job
{
	Structure target;
	
	public EnterStructureJob(Human human, Structure target, boolean persistent)
	{
		super(human, null, "Entering " + target.getName(), 1, persistent);
		this.target = target;
	}
	
	@Override
	public void tick(int tick)
	{}
	
	@Override
	public void onEnd()
	{
		super.onEnd();
		
		human.setLocation(target);
	}
}
