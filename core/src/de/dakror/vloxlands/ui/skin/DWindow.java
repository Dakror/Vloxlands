package de.dakror.vloxlands.ui.skin;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

/**
 * @author Dakror
 */
public class DWindow extends Window {
	public DWindow(String title, Skin skin) {
		this(title, skin.get(WindowStyle.class));
		setSkin(skin);
	}
	
	public DWindow(String title, Skin skin, String styleName) {
		this(title, skin.get(styleName, WindowStyle.class));
		setSkin(skin);
	}
	
	public DWindow(String title, WindowStyle style) {
		super(title, style);
		initStyle();
	}
	
	public void initStyle() {
		padLeft(12f);
		padTop(40f);
	}
	
	@Override
	public void pack() {
		super.pack();
		
		setSize(getWidth() + 12, getHeight() + 4);
	}
}
