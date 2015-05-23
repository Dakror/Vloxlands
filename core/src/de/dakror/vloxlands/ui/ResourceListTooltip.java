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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.inv.ResourceList;
import de.dakror.vloxlands.util.interf.provider.ResourceListProvider;

/**
 * @author Dakror
 */
public class ResourceListTooltip extends Tooltip {
	ResourceListProvider provider;
	
	public ResourceListTooltip(String title, String description, ResourceListProvider provider, Actor parent) {
		super(title, description, parent);
		this.provider = provider;
	}
	
	@Override
	public void setDescription(String s) {
		if (provider == null) return;
		clear();
		Label l = new Label(s, Vloxlands.skin);
		l.setWrap(true);
		add(l).width(200);
		
		ResourceList rl = provider.getCosts();
		
		l = new Label("Costs", Vloxlands.skin, "bold");
		row();
		add(l).width(200);
		
		Texture tex = Vloxlands.assets.get("img/icons.png", Texture.class);
		
		for (Byte b : provider.getCosts().getAll()) {
			Item item = Item.getForId(b);
			addItem(tex, provider.getCosts().get(b) + "", item.getIconX(), item.getIconY());
		}
		
		if (rl.hasRequirements()) {
			l = new Label("Requirements", Vloxlands.skin);
			row();
			add(l).width(200);
			if (rl.getMinPopulation() > 0) addItem(tex, "Min. " + rl.getMinPopulation(), 3, 6);
			if (rl.getMinBuildings() > 0) addItem(tex, "Min. " + rl.getMinPopulation() + " same", 1, 5);
			
			if (rl.getMaxPopulation() > 0) addItem(tex, "Max. " + rl.getMaxPopulation(), 3, 6);
			if (rl.getMaxBuildings() > 0) addItem(tex, "Max. " + rl.getMaxBuildings() + " same", 1, 5);
		}
		
		pack();
		setHeight(getHeight() + 16);
	}
	
	protected void addItem(Texture texture, String text, int iconX, int iconY) {
		TextureRegion region = new TextureRegion(texture, iconX * Item.SIZE, iconY * Item.SIZE, Item.SIZE, Item.SIZE);
		
		Table t = new Table(Vloxlands.skin);
		Image img = new Image(region);
		t.add(img).padLeft(-12);
		Label l = new Label(text, Vloxlands.skin);
		l.setWrap(true);
		l.setAlignment(Align.right, Align.right);
		t.add(l).padLeft(-8).width(174).right();
		row();
		add(t).width(200);
	}
}
