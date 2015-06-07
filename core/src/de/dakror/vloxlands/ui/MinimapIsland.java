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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.world.Island;

/**
 * @author Dakror
 */
public class MinimapIsland extends Actor {
	Island island;
	boolean active;
	
	public MinimapIsland(Island island) {
		this.island = island;
	}
	
	@Override
	public Actor hit(float x, float y, boolean touchable) {
		if (touchable && getTouchable() != Touchable.enabled) return null;
		return x >= getWidth() / 4 && x < getWidth() / 4 * 3 && y >= 0 && y < getHeight() ? this : null;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (island.fbo != null) {
			if (active) {
				Drawable d = Vloxlands.skin.getDrawable("outline");
				d.draw(batch, (int) (getX() + getWidth() / 4), (int) getY(), (int) getWidth() / 2, (int) getHeight());
			}
			batch.draw(island.fbo.getColorBufferTexture(), getX(), getY(), getWidth(), getHeight(), 0, 0, island.fbo.getColorBufferTexture().getWidth(), island.fbo.getColorBufferTexture().getHeight(), false, true);
		}
	}
}
