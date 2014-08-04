package de.dakror.vloxlands.game.job;

import de.dakror.vloxlands.game.entity.creature.Human;

/**
 * Only a pseodo job. Not used in real job queue
 *
 * @author Dakror
 */
public class IdleJob extends Job
{
	public IdleJob(Human human)
	{
		super(human, "idle", "Idling...", -1, false);
	}
}
