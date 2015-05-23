/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
 

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
import de.dakror.vloxlands.generate.WorldGenerator;
import de.dakror.vloxlands.util.InternalAssetManager;
import de.dakror.vloxlands.util.InternalAssetManager.FileNameExtensionFilter;
import de.dakror.vloxlands.util.InternalAssetManager.FileNode;

/**
 * @author Dakror
 */
public class LoadingLayer extends Layer {
	Image logo;
	Texture blur;
	BitmapFont font;
	
	float percent;
	
	WorldGenerator worldGenerator;
	
	boolean worldGen;
	
	boolean iconsSet;
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(37 / 256f, 37 / 256f, 37 / 256f, 1);
		
		if (Vloxlands.assets.update()) {
			if (!iconsSet) {
				for (FileNode fn : InternalAssetManager.listFiles("img/gui", true))
					Vloxlands.skin.add(fn.file.nameWithoutExtension(), Vloxlands.assets.get(fn.file.path(), Texture.class));
				iconsSet = true;
			}
			if (!worldGen) {
				Vloxlands.instance.addLayer(new Game());
				worldGenerator.start();
				worldGen = true;
			} else if (worldGenerator.done && percent > 0.99) {
				Vloxlands.instance.addLayer(new HudLayer());
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
	public void resize(int width, int height) {
		logo.setX((Gdx.graphics.getWidth() - 256) / 2);
		logo.setY((Gdx.graphics.getHeight() - 256) / 2);
	}
	
	@Override
	public void show() {
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
		
		InternalAssetManager.scheduleDirectory(Vloxlands.assets, "img", Texture.class, true);
		InternalAssetManager.scheduleDirectory(Vloxlands.assets, "models", Model.class, new FileNameExtensionFilter("g3db", "vxi"), true);
	}
}
