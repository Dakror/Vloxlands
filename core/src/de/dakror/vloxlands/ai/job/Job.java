package de.dakror.vloxlands.ai.job;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;

import de.dakror.vloxlands.Config;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.util.event.Callback;
import de.dakror.vloxlands.util.interf.Tickable;

/**
 * Jobs to be queued and done by Humans
 * 
 * @author Dakror
 */
public abstract class Job implements Tickable
{
	String animation;
	String text;
	int repeats;
	boolean active;
	boolean done;
	boolean persistent;
	
	float ticksLeft;
	float duration;
	Class<?> tool;
	
	Human human;
	
	Callback endEvent;
	
	public Job(Human human, String animation, String text, int repeats, boolean persistent)
	{
		this.human = human;
		this.text = text;
		this.animation = animation;
		this.repeats = repeats;
		this.persistent = persistent;
		
		active = false;
	}
	
	public boolean isUsingTool()
	{
		return tool != null;
	}
	
	public Class<?> getTool()
	{
		return tool;
	}
	
	public void queue()
	{}
	
	public void trigger(int tick)
	{
		AnimationDesc ad = human.getAnimationController().animate(animation, repeats, Config.getGameSpeed(), null, 0.2f);
		if (ad != null)
		{
			duration = ticksLeft = ad.duration * 60f / Config.getGameSpeed();
		}
		else done = true;
		active = true;
	}
	
	@Override
	public void tick(int tick)
	{
		ticksLeft -= 1f / Config.getGameSpeed();
		if (ticksLeft <= 0)
		{
			if (repeats > -1) repeats = repeats > 0 ? repeats - 1 : 0;
			
			onAnimationFinished();
			
			if (repeats == 0) done = true;
			else ticksLeft = duration;
		}
	}
	
	public void setDone()
	{
		done = true;
	}
	
	protected void onAnimationFinished()
	{}
	
	public void onEnd()
	{
		human.getAnimationController().animate(null, 0.2f); // TODO replace with idle animation
	}
	
	public void triggerEndEvent()
	{
		if (endEvent != null) endEvent.trigger();
	}
	
	public void setEndEvent(Callback event)
	{
		endEvent = event;
	}
	
	public void resetState()
	{
		done = false;
		active = false;
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
	
	public void setPersistent(boolean b)
	{
		persistent = b;
	}
	
	public String getText()
	{
		return text;
	}
	
	@Override
	public String toString()
	{
		return text;
	}
}
