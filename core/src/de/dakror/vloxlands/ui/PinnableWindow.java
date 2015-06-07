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

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.ui.skin.DWindow;

/**
 * @author Dakror
 */
public class PinnableWindow extends DWindow {
	CheckBox pin;
	
	public PinnableWindow(String title, Skin skin) {
		super(title, skin);
		pin = new CheckBox("", Vloxlands.skin);
		TextButton x = new TextButton("X", Vloxlands.skin, "image");
		x.addListener(new HidingClickListener(this));
		getButtonTable().add(pin).height(getPadTop()).width(getPadTop());
		getButtonTable().add(x).size(40).padRight(4);
	}
	
	@Override
	public void initStyle() {
		padLeft(16f);
		padTop(50f);
		padBottom(10);
		padRight(10);
	}
	
	public boolean setShown(boolean visible) {
		if (pin.isChecked() && !visible) return false;
		setVisible(visible);
		
		return true;
	}
}
