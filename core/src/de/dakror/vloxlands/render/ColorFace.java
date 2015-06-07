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


package de.dakror.vloxlands.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.util.Direction;

/**
 * @author Dakror
 */
public class ColorFace extends Face<ColorFace> {
	public Color c;
	
	public ColorFace(Direction dir, Vector3 pos, Color c) {
		this(dir, pos, c, 1, 1, 1);
	}
	
	public ColorFace(Direction dir, Vector3 pos, Color c, float sizeX, float sizeY, float sizeZ) {
		super(dir, pos, sizeX, sizeY, sizeZ);
		this.c = c;
	}
	
	@Override
	public boolean canCombine(ColorFace o) {
		return c.equals(o.c);
	}
}
