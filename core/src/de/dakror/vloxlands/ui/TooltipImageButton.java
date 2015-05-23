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

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import de.dakror.vloxlands.util.interf.provider.ResourceListProvider;

/**
 * @author Dakror
 */
public class TooltipImageButton extends ImageButton {
	protected Tooltip tooltip;
	
	public TooltipImageButton(ImageButtonStyle style) {
		super(style);
		pad(12);
		tooltip = new Tooltip("", "", this);
	}
	
	public TooltipImageButton(ImageButtonStyle style, ResourceListProvider provider) {
		super(style);
		pad(12);
		
		tooltip = new ResourceListTooltip("", "", provider, this);
	}
	
	public Tooltip getTooltip() {
		return tooltip;
	}
}
