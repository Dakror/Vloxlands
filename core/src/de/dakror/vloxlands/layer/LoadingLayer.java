package de.dakror.vloxlands.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.generate.WorldGenerator;

/**
 * @author Dakror
 */
public class LoadingLayer extends Layer
{
	Image logo;
	Texture blur;
	BitmapFont font;
	
	float percent;
	
	WorldGenerator worldGenerator;
	
	boolean worldGen;
	
	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(37 / 256f, 37 / 256f, 37 / 256f, 1);
		
		if (Vloxlands.assets.update())
		{
			if (!worldGen)
			{
				Vloxlands.currentGame.addLayer(new GameLayer());
				worldGenerator.start();
				worldGen = true;
			}
			else if (worldGenerator.done)
			{
				Vloxlands.currentGame.addLayer(new HudLayer());
				Vloxlands.currentGame.removeLayer(this);
				GameLayer.instance.doneLoading();
				return;
			}
		}
		
		int height = Math.round(256 * percent);
		percent = Interpolation.linear.apply(percent, (Vloxlands.assets.getProgress() + worldGenerator.progress) / 2f, 0.01f);
		
		stage.act();
		stage.draw();
		stage.getBatch().begin();
		stage.getBatch().draw(blur, logo.getX(), logo.getY(), 0, height, 256, 256 - height);
		
		String number = Math.round(percent * 100) + "";
		if (number.length() == 1) number = " " + number;
		if (number.length() == 2) number = " " + number;
		String string = number + "% - " + (!worldGen ? "Loading resources" : "Generating world");
		TextBounds tb = font.getBounds(string);
		font.draw(stage.getBatch(), string, (Gdx.graphics.getWidth() - tb.width) / 2, logo.getY() + 270);
		stage.getBatch().end();
	}
	
	@Override
	public void resize(int width, int height)
	{
		logo.setX((Gdx.graphics.getWidth() - 256) / 2);
		logo.setY((Gdx.graphics.getHeight() - 256) / 2);
	}
	
	@Override
	public void show()
	{
		modal = true;
		Vloxlands.assets.load("img/logo/logo256.png", Texture.class);
		Vloxlands.assets.load("img/logo/logo256-blur.png", Texture.class);
		Vloxlands.assets.load("img/icons.png", Texture.class);
		Vloxlands.assets.load("img/gui/gears.png", Texture.class);
		Vloxlands.assets.load("img/gui/bomb.png", Texture.class);
		Vloxlands.assets.load("img/gui/sleep.png", Texture.class);
		Vloxlands.assets.load("img/gui/revolverSlot.png", Texture.class);
		Vloxlands.assets.load("img/gui/revolverSlot_disabled.png", Texture.class);
		
		Vloxlands.assets.finishLoading();
		
		Vloxlands.skin.add("gears", Vloxlands.assets.get("img/gui/gears.png", Texture.class));
		Vloxlands.skin.add("bomb", Vloxlands.assets.get("img/gui/bomb.png", Texture.class));
		Vloxlands.skin.add("sleep", Vloxlands.assets.get("img/gui/sleep.png", Texture.class));
		Vloxlands.skin.add("revolverSlot", Vloxlands.assets.get("img/gui/revolverSlot.png", Texture.class));
		Vloxlands.skin.add("revolverSlot_disabled", Vloxlands.assets.get("img/gui/revolverSlot_disabled.png", Texture.class));
		
		stage = new Stage(new ScreenViewport());
		font = new BitmapFont();
		logo = new Image(Vloxlands.assets.get("img/logo/logo256.png", Texture.class));
		blur = Vloxlands.assets.get("img/logo/logo256-blur.png", Texture.class);
		worldGenerator = new WorldGenerator();
		
		stage.addActor(logo);
		
		// TODO: Add all models wanting to be loaded
		Vloxlands.assets.load("models/humanblend/humanblend.g3db", Model.class);
		Vloxlands.assets.load("models/tent/tent.g3db", Model.class);
		Vloxlands.assets.load("models/sky/sky.g3db", Model.class);
		for (Item item : Item.getAll())
			if (item.isModel() && item.getModel().length() > 0) Vloxlands.assets.load("models/item/" + item.getModel(), Model.class);
	}
}
