package de.dakror.vloxlands.game.job;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.util.Tickable;

/**
 * @author Dakror
 */
public abstract class Job implements Tickable
{
	String animation;
	int repeats;
	boolean active;
	boolean done;
	boolean persistent;
	
	Human human;
	
	public Job(Human human, String animation, int repeats, boolean persistent)
	{
		this.human = human;
		this.animation = animation;
		this.repeats = repeats;
		this.persistent = persistent;
		
		active = false;
	}
	
	public void trigger()
	{
		human.getAnimationController().animate(animation, repeats, human, 0.2f);
		active = true;
	}
	
	public void onEnd()
	{
		human.getAnimationController().animate(null, 0); // TODO: replace with idle animation
	}
	
	public void setDone()
	{
		done = true;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public boolean isDone()
	{
		return done;
	}
	
	public boolean isPersistent()
	{
		return persistent;
	}
}
