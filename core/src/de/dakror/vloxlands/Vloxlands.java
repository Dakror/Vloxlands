package de.dakror.vloxlands;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;

import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.layer.DebugLayer;
import de.dakror.vloxlands.layer.GameLayer;
import de.dakror.vloxlands.layer.Layer;
import de.dakror.vloxlands.layer.LoadingLayer;
import de.dakror.vloxlands.util.base.GameBase;

public class Vloxlands extends GameBase
{
	public static Vloxlands currentGame;
	public static AssetManager assets;
	
	// -- on screen controls -- //
	// Stage stage;
	// OrthographicCamera camera2;
	// Touchpad moveTouchpad;
	// TouchpadStyle touchpadStyle;
	// Skin touchpadSkin;
	// Drawable touchpadBack;
	// Drawable touchpadFront;
	
	long last;
	int tick;
	
	public static boolean debug;
	public static boolean showPathDebug;
	
	@Override
	public void create()
	{
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		currentGame = this;
		
		Voxel.loadVoxels();
		Item.loadItems();
		
		assets = new AssetManager();
		
		// -- stage -- //
		// if (Gdx.app.getType() == ApplicationType.Android)
		// {
		// camera2 = new OrthographicCamera();
		//
		// touchpadSkin = new Skin();
		// touchpadSkin.add("touchpadBack", new Texture("img/gui/touchpadBack.png"));
		// touchpadSkin.add("touchpadFront", new Texture("img/gui/touchpadFront.png"));
		//
		// touchpadStyle = new TouchpadStyle();
		// touchpadBack = touchpadSkin.getDrawable("touchpadBack");
		// touchpadFront = touchpadSkin.getDrawable("touchpadFront");
		//
		// touchpadStyle.background = touchpadBack;
		// touchpadStyle.knob = touchpadFront;
		//
		// int size = (int) (160 * (Gdx.graphics.getHeight() / 720f));
		// int size2 = (int) (100 * (Gdx.graphics.getHeight() / 720f));
		//
		// touchpadStyle.knob.setMinWidth(size2);
		// touchpadStyle.knob.setMinHeight(size2);
		//
		// int delta = 30;
		//
		// moveTouchpad = new Touchpad(10, touchpadStyle);
		// moveTouchpad.setBounds(delta, delta, size, size);
		//
		// stage = new Stage(new ScreenViewport(camera2));
		// stage.addActor(moveTouchpad);
		//
		// getMultiplexer().addProcessor(stage);
		// }
		
		getMultiplexer().addProcessor(new GestureDetector(this));
		getMultiplexer().addProcessor(this);
		Gdx.input.setInputProcessor(getMultiplexer());
		
		addLayer(new GameLayer());
		addLayer(new LoadingLayer());
	}
	
	@Override
	public void render()
	{
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		for (Layer l : layers)
			l.render(Gdx.graphics.getDeltaTime());
		
		// if (Gdx.app.getType() == ApplicationType.Android)
		// {
		// camera2.update();
		//
		// stage.act(Gdx.graphics.getDeltaTime());
		// stage.draw();
		// }
		
		if (last == 0) last = System.currentTimeMillis();
		
		if (System.currentTimeMillis() - last >= 16) // ~60 a sec
		{
			// if (Gdx.app.getType() == ApplicationType.Android)
			// {
			// float delta = Gdx.graphics.getDeltaTime();
			// camera.position.add(camera.direction.cpy().nor().scl(delta * moveTouchpad.getKnobPercentY() * velocity));
			// camera.position.add(camera.direction.cpy().crs(camera.up).nor().scl(delta * moveTouchpad.getKnobPercentX() * velocity));
			// }
			tick++;
			
			for (Layer l : layers)
				l.tick(tick);
			last = System.currentTimeMillis();
		}
	}
	
	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);
		// if (Gdx.app.getType() == ApplicationType.Android) camera2.update();
	}
	
	@Override
	public boolean keyUp(int keycode)
	{
		if (keycode == Keys.F1)
		{
			debug = !debug;
			toggleLayer(new DebugLayer());
		}
		if (keycode == Keys.F2) showPathDebug = !showPathDebug;
		if (keycode == Keys.F11)
		{
			if (Gdx.graphics.isFullscreen()) Gdx.graphics.setDisplayMode(1280, 720, false);
			else Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
		}
		return false;
	}
}
