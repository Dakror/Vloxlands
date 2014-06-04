package de.dakror.vloxlands.ui;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import de.dakror.vloxlands.Vloxlands;

/**
 * @author Dakror
 */
public class PinnableWindow extends Window
{
	CheckBox pin;
	
	public PinnableWindow(String title, Skin skin)
	{
		super(title, skin);
		
		pin = new CheckBox("", Vloxlands.skin);
		TextButton x = new TextButton("X", Vloxlands.skin);
		x.addListener(new HidingClickListener(this));
		getButtonTable().add(pin).height(getPadTop()).width(getPadTop());
		getButtonTable().add(x).height(getPadTop()).width(getPadTop());
	}
	
	public boolean setShown(boolean visible)
	{
		if (pin.isChecked() && !visible) return false;
		setVisibleForce(visible);
		
		return true;
	}
	
	public void setVisibleForce(boolean visible)
	{
		super.setVisible(visible);
	}
}
