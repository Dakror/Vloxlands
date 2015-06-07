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


package de.dakror.vloxlands.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.game.world.Island;

/**
 * @author Dakror
 */
public class IslandInfo extends Group {
	public IslandInfo() {
		setBounds(0, 0, 500, 80);
		setOrigin(getX(), getY());
		
		final Label biome = new Label("", Vloxlands.skin);
		addActor(biome);
		final Label delta = new Label("", Vloxlands.skin);
		addActor(delta);
		addActor(biome);
		final Label height = new Label("", Vloxlands.skin);
		addActor(height);
		
		addAction(new Action() {
			@Override
			public boolean act(float d) {
				biome.setText(Game.instance.activeIsland.getBiome().getName() + " Island");
				biome.setPosition((500 - biome.getTextBounds().width) / 2f, 60);
				
				delta.setText("Moving by " + String.format("%2.2f", Game.instance.activeIsland.getDeltaPerSecond()) + " m/s");
				delta.setPosition(10, 20);
				
				height.setText("Currrent Height: " + String.format("%2.2f", Game.instance.activeIsland.pos.y + Island.SIZE / 2) + " m");
				height.setPosition(490 - height.getTextBounds().width, 20);
				return false;
			}
		});
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		setPosition((Gdx.graphics.getWidth() - getWidth()) / 2, Gdx.graphics.getHeight() - getHeight());
		
		Drawable bg = Vloxlands.skin.getDrawable("paper_container");
		bg.draw(batch, getX(), getY(), getWidth(), getHeight());
		super.draw(batch, parentAlpha);
	}
}
