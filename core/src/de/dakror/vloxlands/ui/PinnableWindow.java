package de.dakror.vloxlands.ui;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.ui.skin.DWindow;

/**
 * @author Dakror
 */
public class PinnableWindow extends DWindow
{
	CheckBox pin;
	
	public PinnableWindow(String title, Skin skin)
	{
		super(title, skin);
		
		pin = new CheckBox("", Vloxlands.skin);
		TextButton x = new TextButton("X", Vloxlands.skin, "image");
		x.addListener(new HidingClickListener(this));
		getButtonTable().add(pin).height(getPadTop()).width(getPadTop());
		getButtonTable().add(x).height(getPadTop()).width(getPadTop()).padRight(4).padTop(4);
	}
	
	public boolean setShown(boolean visible)
	{
		if (pin.isChecked() && !visible) return false;
		setVisible(visible);
		
		return true;
	}
}
