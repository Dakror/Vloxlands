package de.dakror.vloxlands.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.ui.skin.DWindow;

/**
 * @author Dakror
 */
public class Tooltip extends DWindow {
	final Vector2 tmp = new Vector2();
	
	Actor parent;
	
	public Tooltip(String title, String description, final Actor parent) {
		super(title, Vloxlands.skin, "tooltip");
		setTitleAlignment(Align.left);
		setDescription(description);
		setVisible(false);
		
		parent.addListener(new InputListener() {
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if (Tooltip.this.getTitle().length() == 0) return;
				
				Actor a = event.getListenerActor();
				
				float x1 = a.getWidth() + 10;
				tmp.set(x1, a.getHeight() - getHeight());
				a.localToStageCoordinates(tmp);
				
				if (tmp.x + getWidth() > getStage().getWidth()) {
					x1 = -getWidth() - 10;
					tmp.set(x1, a.getHeight() - getHeight());
					a.localToStageCoordinates(tmp);
				}
				
				setPosition(tmp.x, tmp.y);
				setVisible(true);
				toFront();
			}
			
			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				setVisible(false);
			}
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				setVisible(false);
				return false;
			}
		});
	}
	
	public void set(String title, String description) {
		setTitle(title);
		setDescription(description);
	}
	
	public void setDescription(String s) {
		clear();
		Label l = new Label(s, Vloxlands.skin);
		l.setWrap(true);
		add(l).padBottom(6).width(200);
		
		pack();
	}
}
