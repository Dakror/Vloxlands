package de.dakror.vloxlands.game.job;

import de.dakror.vloxlands.game.entity.creature.Human;

/**
 * @author Dakror
 */
public class DestroyVoxelJob extends Job
{
	public DestroyVoxelJob(Human human, String animation, String text, int repeats, boolean persistent)
	{
		super(human, animation, text, repeats, persistent);
	}

	@Override
	public void tick(int tick)
	{}
	
}
