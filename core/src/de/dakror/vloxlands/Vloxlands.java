package de.dakror.vloxlands;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.layer.DebugLayer;
import de.dakror.vloxlands.layer.GameLayer;
import de.dakror.vloxlands.layer.Layer;
import de.dakror.vloxlands.layer.LoadingLayer;
import de.dakror.vloxlands.util.Compressor;
import de.dakror.vloxlands.util.base.GameBase;
import de.dakror.vloxlands.util.math.MathHelper;

public class Vloxlands extends GameBase
{
	public static Vloxlands currentGame;
	public static AssetManager assets;
	public static Skin skin;
	
	long last;
	int tick;
	
	public static boolean showPathDebug;
	
	@Override
	public void create()
	{
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		currentGame = this;
		
		Config.init();
		setFullscreen(Config.pref.getBoolean("fullscreen"));
		Voxel.loadVoxels();
		Item.loadItems();
		
		assets = new AssetManager();
		skin = new Skin(Gdx.files.internal("skin/default/uiskin.json"));
		
		getMultiplexer().addProcessor(0, new GestureDetector(this));
		getMultiplexer().addProcessor(0, this);
		Gdx.input.setInputProcessor(getMultiplexer());
		
		addLayer(new LoadingLayer());
	}
	
	@Override
	public void render()
	{
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		for (Layer l : layers)
			l.render(Gdx.graphics.getDeltaTime());
		
		if (last == 0) last = System.currentTimeMillis();
		
		if (System.currentTimeMillis() - last >= 16) // ~60 a sec
		{
			tick++;
			
			for (Layer l : layers)
				l.tick(tick);
			last = System.currentTimeMillis();
		}
	}
	
	@Override
	public boolean keyUp(int keycode)
	{
		if (keycode == Keys.F1)
		{
			Config.debug = !Config.debug;
			toggleLayer(new DebugLayer());
			return true;
		}
		if (keycode == Keys.F2) showPathDebug = !showPathDebug;
		if (keycode == Keys.F11)
		{
			setFullscreen(!Gdx.graphics.isFullscreen());
			
			return true;
		}
		if (keycode == Keys.F6 && GameLayer.world != null)
		{
			try
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				GameLayer.world.save(baos);
				FileHandle file = Gdx.files.external(".dakror/Vloxlands/maps/" + new SimpleDateFormat("dd.MM.yy HH-mm-ss").format(new Date()) + ".map");
				file.writeBytes(Compressor.compress(baos.toByteArray()), false);
				Gdx.app.log("Vloxlands.keyUp", "Game saved as " + file.name() + " (" + MathHelper.formatBinarySize(file.length(), 0) + ")");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	@Override
	public void pause()
	{
		Config.savePrefs();
	}
	
	public void setFullscreen(boolean fullscreen)
	{
		if (!fullscreen) Gdx.graphics.setDisplayMode(1280, 720, false);
		else Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
	}
}
