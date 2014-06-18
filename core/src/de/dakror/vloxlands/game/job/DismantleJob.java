package de.dakror.vloxlands.game.job;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;

/**
 * @author Dakror
 */
public class DismantleJob extends Job
{
	private Structure target;
	
	public DismantleJob(Human human, Structure target, boolean persistent)
	{
		super(human, "walk", "Dismantling " + target.getName(), 10, persistent);
		this.target = target;
	}
	
	@Override
	public void tick(int tick)
	{}
	
	@Override
	public void onEnd()
	{
		super.onEnd();
		target.kill();
	}
}
