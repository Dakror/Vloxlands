package de.dakror.vloxlands.game.action;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;

/**
 * @author Dakror
 */
public class DumpAction extends Action
{
	private Structure target;
	
	public DumpAction(Human human, Structure target)
	{
		super(human, "walk" /* dump */, 1);
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
