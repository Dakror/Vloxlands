package de.dakror.vloxlands.game.job;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;

/**
 * @author Dakror
 */
public class DumpJob extends Job
{
	private Structure target;
	
	public DumpJob(Human human, Structure target, boolean persistent)
	{
		super(human, "walk" /* dump */, 1, persistent);
		this.target = target;
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
		
		human.setCarryingItemStack(target.getInventory().add(human.getCarryingItemStack()));
	}
}
