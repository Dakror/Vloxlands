package de.dakror.vloxlands.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author Dakror
 */
public class HidingClickListener extends ClickListener
{
	Actor actor;
	
	public HidingClickListener(Actor actor)
	{
		this.actor = actor;
	}
	
	@Override
	public void clicked(InputEvent event, float x, float y)
	{
		actor.setVisible(false);
	}
}
