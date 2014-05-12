package de.dakror.vloxlands.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.generate.WorldGenerator;

/**
 * @author Dakror
 */
public class LoadingScreen implements Screen
{
	Stage stage;
	Image logo;
	Texture blur;
	
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
				worldGenerator.start();
				worldGen = true;
			}
			else if (worldGenerator.done)
			{
				Vloxlands.currentGame.doneLoading();
				Vloxlands.currentGame.setScreen(null);
			}
		}
		
		int height = Math.round(256 * percent);
		percent = Interpolation.linear.apply(percent, (Vloxlands.assets.getProgress() + worldGenerator.progress) / 2f, 0.01f);
		
		stage.act();
		stage.draw();
		stage.getSpriteBatch().begin();
		stage.getSpriteBatch().draw(blur, logo.getX(), logo.getY(), 0, height, 256, 256 - height);
		stage.getSpriteBatch().end();
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
		Vloxlands.assets.load("img/logo/logo256.png", Texture.class);
		Vloxlands.assets.load("img/logo/logo256-blur.png", Texture.class);
		
		Vloxlands.assets.finishLoading();
		
		stage = new Stage();
		logo = new Image(Vloxlands.assets.get("img/logo/logo256.png", Texture.class));
		blur = Vloxlands.assets.get("img/logo/logo256-blur.png", Texture.class);
		worldGenerator = new WorldGenerator();
		
		stage.addActor(logo);
		
		
		// TODO: Add all models wanting to be loaded
		Vloxlands.assets.load("models/humanblend/humanblend.g3db", Model.class);
		Vloxlands.assets.load("models/tent/tent.g3db", Model.class);
		Vloxlands.assets.load("models/sky/sky.g3db", Model.class);
	}
	
	@Override
	public void hide()
	{}
	
	@Override
	public void pause()
	{}
	
	@Override
	public void resume()
	{}
	
	@Override
	public void dispose()
	{}
}
