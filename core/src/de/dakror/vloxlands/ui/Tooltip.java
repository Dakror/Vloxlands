package de.dakror.vloxlands.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import de.dakror.vloxlands.Vloxlands;

/**
 * @author Dakror
 */
public class Tooltip extends Window
{
	final Vector2 tmp = new Vector2();
	
	Actor parent;
	int offset = 10;
	
	public Tooltip(String title, String description, Actor parent)
	{
		super(title, Vloxlands.skin);
		setDescription(description);
		setVisible(false);
		
		addListener(new InputListener()
		{
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
			{
				tmp.set(x, y);
				fromActor.localToStageCoordinates(tmp);
				setPosition(tmp.x + offset, tmp.y + offset);
				setVisible(true);
				toFront();
			}
			
			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
			{
				setVisible(false);
			}
		});
	}
	
	public void setDescription(String s)
	{
		clear();
		Label l = new Label(s, Vloxlands.skin);
		add(l);
		pack();
	}
}
