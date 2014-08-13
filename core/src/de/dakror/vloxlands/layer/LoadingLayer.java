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
import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.generate.WorldGenerator;
import de.dakror.vloxlands.util.D;

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
	
	boolean iconsSet;
	String[] icons = { "bomb", "gears", "queue", "sleep", "work" };
	
	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(37 / 256f, 37 / 256f, 37 / 256f, 1);
		
		if (Vloxlands.assets.update())
		{
			if (!iconsSet)
			{
				for (String i : icons)
					Vloxlands.skin.add(i, Vloxlands.assets.get("img/gui/icons/" + i + ".png", Texture.class));
				
				Vloxlands.skin.add("revolverSlot", Vloxlands.assets.get("img/gui/revolverSlot.png", Texture.class));
				Vloxlands.skin.add("revolverSlot_over", Vloxlands.assets.get("img/gui/revolverSlot_over.png", Texture.class));
				Vloxlands.skin.add("revolverSlot_disabled", Vloxlands.assets.get("img/gui/revolverSlot_disabled.png", Texture.class));
				Vloxlands.skin.add("progressBar", Vloxlands.assets.get("img/gui/progressBar.png", Texture.class));
				iconsSet = true;
			}
			if (!worldGen)
			{
				Vloxlands.instance.addLayer(new Game());
				worldGenerator.start();
				worldGen = true;
			}
			else if (worldGenerator.done && percent > 0.99)
			{
				Vloxlands.instance.addLayer(new HudLayer());
				if (D.android()) Vloxlands.instance.addLayer(new DebugLayer());
				Vloxlands.instance.removeLayer(this);
				Game.instance.doneLoading();
				return;
			}
		}
		
		int height = Math.round(256 * percent);
		
		float np = (Vloxlands.assets.getProgress() + worldGenerator.progress) / 2f;
		
		percent = Interpolation.linear.apply(percent, np, Math.max((np - percent) / 5, 0.1f));
		
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
		
		Vloxlands.assets.finishLoading();
		
		stage = new Stage(new ScreenViewport());
		font = new BitmapFont();
		logo = new Image(Vloxlands.assets.get("img/logo/logo256.png", Texture.class));
		blur = Vloxlands.assets.get("img/logo/logo256-blur.png", Texture.class);
		worldGenerator = new WorldGenerator();
		
		stage.addActor(logo);
		
		Vloxlands.assets.load("img/icons.png", Texture.class);
		Vloxlands.assets.load("img/gui/progressBar.png", Texture.class);
		Vloxlands.assets.load("img/gui/revolverSlot.png", Texture.class);
		Vloxlands.assets.load("img/gui/revolverSlot_over.png", Texture.class);
		Vloxlands.assets.load("img/gui/revolverSlot_disabled.png", Texture.class);
		Vloxlands.assets.load("img/gui/revolverSlot_disabled.png", Texture.class);
		for (String i : icons)
			Vloxlands.assets.load("img/gui/icons/" + i + ".png", Texture.class);
		
		// TODO Add all models wanting to be loaded
		Vloxlands.assets.load("models/creature/humanblend/humanblend.g3db", Model.class);
		Vloxlands.assets.load("models/structure/PH_tent_red/PH_tent_red.g3db", Model.class);
		Vloxlands.assets.load("models/structure/PH_tent_green/PH_tent_green.g3db", Model.class);
		Vloxlands.assets.load("models/structure/sapling/sapling.g3db", Model.class);
		// Vloxlands.assets.load("models/sky/sky.g3db", Model.class);
		for (Item item : Item.getAll())
			if (item.isModel() && item.getModel().length() > 0) Vloxlands.assets.load("models/item/" + item.getModel(), Model.class);
	}
}
