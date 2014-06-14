package de.dakror.vloxlands.game.job;

import de.dakror.vloxlands.ai.Path;
import de.dakror.vloxlands.game.entity.creature.Human;

/**
 * @author Dakror
 */
public class WalkJob extends Job
{
	Path path;
	
	public WalkJob(Path path, Human human)
	{
		super(human, "walk", "Walking " + Math.round(path.length() * 100) / 100f + "m", -1, false);
		this.path = path;
	}
	
	@Override
	public void tick(int tick)
	{}
	
	public Path getPath()
	{
		return path;
	}
	
	public void setPath(Path p)
	{
		path = p;
	}
}
