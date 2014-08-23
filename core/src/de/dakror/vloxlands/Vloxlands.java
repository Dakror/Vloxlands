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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.ReadOnlySerializer;
import com.badlogic.gdx.utils.JsonValue;

import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.layer.DebugLayer;
import de.dakror.vloxlands.layer.Layer;
import de.dakror.vloxlands.layer.LoadingLayer;
import de.dakror.vloxlands.render.DDirectionalShadowLight;
import de.dakror.vloxlands.ui.RevolverSlot;
import de.dakror.vloxlands.ui.skin.DoubleDrawable;
import de.dakror.vloxlands.ui.skin.TilesetDrawable;
import de.dakror.vloxlands.util.Compressor;
import de.dakror.vloxlands.util.D;
import de.dakror.vloxlands.util.base.GameBase;
import de.dakror.vloxlands.util.math.Bits;
import de.dakror.vloxlands.util.math.MathHelper;

public class Vloxlands extends GameBase
{
	public static Vloxlands instance;
	public static AssetManager assets;
	public static Skin skin;
	
	public static boolean showPathDebug;
	public static boolean wireframe;
	
	@Override
	public void create()
	{
		instance = this;
		
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		Config.init();
		
		if (D.android()) RevolverSlot.SIZE = 80;
		
		Entity.loadEntities();
		Voxel.loadVoxels();
		Voxel.buildMeshes();
		Item.loadItems();
		
		setFullscreen(Config.pref.getBoolean("fullscreen"));
		
		assets = new AssetManager();
		skin = new Skin(new TextureAtlas(Gdx.files.internal("skin/RPGConstrUI/uiskin.atlas")))
		{
			@Override
			protected Json getJsonLoader(FileHandle skinFile)
			{
				Json json = super.getJsonLoader(skinFile);
				
				json.setSerializer(TilesetDrawable.class, new ReadOnlySerializer<TilesetDrawable>()
				{
					@SuppressWarnings("rawtypes")
					@Override
					public TilesetDrawable read(Json json, JsonValue jsonData, Class type)
					{
						TextureRegion[] regions = new TextureRegion[9];
						for (int i = 0; i < 9; i++)
						{
							try
							{
								regions[i] = getRegion(json.readValue(TilesetDrawable.values[i], String.class, jsonData));
							}
							catch (Exception e)
							{}
						}
						return new TilesetDrawable(regions);
					}
				});
				
				json.setSerializer(DoubleDrawable.class, new ReadOnlySerializer<DoubleDrawable>()
				{
					@SuppressWarnings("rawtypes")
					@Override
					public DoubleDrawable read(Json json, JsonValue jsonData, Class type)
					{
						Drawable fg = null;
						try
						{
							fg = getDrawable(json.readValue("fg", String.class, jsonData));
						}
						catch (Exception e)
						{}
						Drawable bg = null;
						try
						{
							bg = getDrawable(json.readValue("bg", String.class, jsonData));
						}
						catch (Exception e)
						{}
						return new DoubleDrawable(fg, bg);
					}
				});
				
				return json;
			}
			
			@Override
			public Drawable getDrawable(String name)
			{
				TilesetDrawable tilesetDrawable = optional(name, TilesetDrawable.class);
				if (tilesetDrawable != null) return tilesetDrawable;
				DoubleDrawable doubleDrawable = optional(name, DoubleDrawable.class);
				if (doubleDrawable != null) return doubleDrawable;
				
				return super.getDrawable(name);
			}
		};
		skin.load(Gdx.files.internal("skin/RPGConstrUI/uiskin.json"));
		getMultiplexer().addProcessor(0, new GestureDetector(this));
		getMultiplexer().addProcessor(0, this);
		Gdx.input.setInputProcessor(getMultiplexer());
		
		new Updater();
		
		addLayer(new LoadingLayer());
	}
	
	@Override
	public void render()
	{
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		for (Layer l : layers)
			l.render(Gdx.graphics.getDeltaTime());
	}
	
	@SuppressWarnings("deprecation")
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
		if (keycode == Keys.F3) wireframe = !wireframe;
		if (keycode == Keys.F11)
		{
			setFullscreen(!Gdx.graphics.isFullscreen());
			
			return true;
		}
		
		if (Game.world != null)
		{
			if (keycode == Keys.F4) Game.world.setDataMap(Game.world.getDataMap() + 1);
			if (keycode == Keys.F5) Game.world.setDataMap(Game.world.getDataMap() - 1);
			if (keycode == Keys.F6) saveGame();
			if (keycode == Keys.F7) Game.world.getIslands()[0].pos.y += 5;
			if (keycode == Keys.F9) Config.shadowQuality++;
			if (keycode == Keys.F10) Config.shadowQuality--;
			if (keycode == Keys.F9 || keycode == Keys.F10)
			{
				Config.shadowQuality = Math.max(0, Config.shadowQuality);
				((DDirectionalShadowLight) Game.instance.env.shadowMap).setShadowQuality(Config.shadowQuality);
			}
			if (keycode == Keys.UP) Config.changeGameSpeed(true);
			if (keycode == Keys.DOWN) Config.changeGameSpeed(false);
			if (keycode == Keys.SPACE) Config.paused = !Config.paused;
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
		if (!fullscreen) Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, false);
		else Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
	}
	
	public void saveGame()
	{
		try
		{
			boolean wasNull = false;
			if (Config.savegameName == null)
			{
				wasNull = true;
				Config.savegameName = new SimpleDateFormat("dd.MM.yy HH-mm-ss").format(new Date());
			}
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Bits.putLong(baos, Game.seed);
			Game.world.save(baos);
			FileHandle file = Gdx.files.external(".dakror/Vloxlands/maps/" + Config.savegameName + ".map");
			file.writeBytes(Compressor.compress(baos.toByteArray()), false);
			Gdx.app.log("Vloxlands.saveGame", "Game saved" + (wasNull ? " as " + file.name() + " (" + MathHelper.formatBinarySize(file.length(), 0) + ")." : "."));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
