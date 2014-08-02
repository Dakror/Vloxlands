package de.dakror.vloxlands.game.job;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;

import de.dakror.vloxlands.Config;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.util.Tickable;
import de.dakror.vloxlands.util.event.Event;

/**
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
	
	int startTick;
	long startTime;
	int gameSpeedAtStart;
	int durationInTicks;
	Class<?> tool;
	
	Human human;
	
	Event endEvent;
	
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
	
	public void trigger(int tick)
	{
		gameSpeedAtStart = Config.getGameSpeed();
		startTick = tick;
		startTime = System.currentTimeMillis();
		AnimationDesc ad = human.getAnimationController().animate(animation, repeats, Config.getGameSpeed(), human, 0.2f);
		if (ad != null) durationInTicks = (int) Math.ceil(ad.duration * 60);
		else done = true;
		active = true;
	}
	
	public void update(float delta)
	{
		if (Config.getGameSpeed() != gameSpeedAtStart)
		{
			float timePassed = (startTime - System.currentTimeMillis()) / 1000.0f * gameSpeedAtStart;
			human.getAnimationController().setAnimation(animation, timePassed, -1, repeats, Config.getGameSpeed(), human);
		}
	}
	
	public void onEnd()
	{
		human.getAnimationController().animate(null, 0.2f); // TODO replace with idle animation
	}
	
	public void triggerEndEvent()
	{
		if (endEvent != null) endEvent.trigger();
	}
	
	public void setEndEvent(Event event)
	{
		endEvent = event;
	}
	
	public void setDone()
	{
		done = true;
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
