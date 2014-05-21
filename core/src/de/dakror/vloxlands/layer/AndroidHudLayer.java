package de.dakror.vloxlands.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.dakror.vloxlands.Vloxlands;

/**
 * @author Dakror
 */
public class AndroidHudLayer extends Layer
{
	Touchpad moveTouchpad;
	TouchpadStyle touchpadStyle;
	Skin touchpadSkin;
	Drawable touchpadBack;
	Drawable touchpadFront;
	
	public AndroidHudLayer()
	{
		stage = new Stage(new ScreenViewport());
	}
	
	@Override
	public void show()
	{
		touchpadSkin = new Skin();
		touchpadSkin.add("touchpadBack", new Texture("img/gui/touchpadBack.png"));
		touchpadSkin.add("touchpadFront", new Texture("img/gui/touchpadFront.png"));
		
		touchpadStyle = new TouchpadStyle();
		touchpadBack = touchpadSkin.getDrawable("touchpadBack");
		touchpadFront = touchpadSkin.getDrawable("touchpadFront");
		
		touchpadStyle.background = touchpadBack;
		touchpadStyle.knob = touchpadFront;
		
		int size = (int) (160 * (Gdx.graphics.getHeight() / 720f));
		int size2 = (int) (100 * (Gdx.graphics.getHeight() / 720f));
		
		touchpadStyle.knob.setMinWidth(size2);
		touchpadStyle.knob.setMinHeight(size2);
		
		int delta = 30;
		
		moveTouchpad = new Touchpad(10, touchpadStyle);
		moveTouchpad.setBounds(delta, delta, size, size);
		
		stage.addActor(moveTouchpad);
	}
	
	@Override
	public void render(float delta)
	{
		stage.act(Gdx.graphics.getDeltaTime());
		if (Vloxlands.currentGame.getActiveLayer().equals(this)) stage.draw();
	}
	
	@Override
	public void tick(int tick)
	{
		float delta = Gdx.graphics.getDeltaTime();
		GameLayer.camera.position.add(GameLayer.camera.direction.cpy().nor().scl(delta * moveTouchpad.getKnobPercentY() * GameLayer.velocity));
		GameLayer.camera.position.add(GameLayer.camera.direction.cpy().crs(GameLayer.camera.up).nor().scl(delta * moveTouchpad.getKnobPercentX() * GameLayer.velocity));
	}
}
