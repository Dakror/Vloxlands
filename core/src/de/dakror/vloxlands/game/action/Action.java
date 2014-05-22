package de.dakror.vloxlands.game.action;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.util.Tickable;

/**
 * @author Dakror
 */
public abstract class Action implements Tickable
{
	String animation;
	int repeats;
	boolean active;
	boolean done;
	
	Human human;
	
	public Action(Human human, String animation, int repeats)
	{
		this.human = human;
		this.animation = animation;
		this.repeats = repeats;
		
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
}
