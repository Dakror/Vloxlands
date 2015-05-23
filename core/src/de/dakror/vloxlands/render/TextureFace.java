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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.util.Direction;

public class TextureFace extends Face<TextureFace> {
	public Vector2 tex;
	public float texWidth = Voxel.TEXSIZE;
	public float texHeight = Voxel.TEXSIZE;
	
	public TextureFace(Direction dir, Vector3 pos, Vector2 tex) {
		this(dir, pos, tex, 1, 1, 1);
	}
	
	public TextureFace(Direction dir, Vector3 pos, Vector2 tex, float sizeX, float sizeY, float sizeZ) {
		super(dir, pos, sizeX, sizeY, sizeZ);
		this.tex = tex;
	}
	
	public void getVertexData(FloatArray vert) {
		boolean zDir = dir == Direction.WEST || dir == Direction.EAST;
		boolean yDir = dir == Direction.UP || dir == Direction.DOWN;
		
		float tx = (float) Math.ceil(zDir ? sizeX : yDir ? sizeX : sizeZ);
		float ty = (float) Math.ceil(yDir ? sizeZ : sizeY);
		
		float b = Color.toFloatBits(1f, 1f, 1f, 1f);
		
		vert.add(tl.x + pos.x);
		vert.add(tl.y + pos.y);
		vert.add(tl.z + pos.z);
		vert.add(n.x);
		vert.add(n.y);
		vert.add(n.z);
		vert.add(b);
		vert.add(tex.x);
		vert.add(tex.y);
		vert.add(tx);
		vert.add(ty);
		
		vert.add(tr.x + pos.x);
		vert.add(tr.y + pos.y);
		vert.add(tr.z + pos.z);
		vert.add(n.x);
		vert.add(n.y);
		vert.add(n.z);
		vert.add(b);
		vert.add(tex.x + texWidth);
		vert.add(tex.y);
		vert.add(tx);
		vert.add(ty);
		
		vert.add(br.x + pos.x);
		vert.add(br.y + pos.y);
		vert.add(br.z + pos.z);
		vert.add(n.x);
		vert.add(n.y);
		vert.add(n.z);
		vert.add(b);
		vert.add(tex.x + texWidth);
		vert.add(tex.y + texHeight);
		vert.add(tx);
		vert.add(ty);
		
		vert.add(bl.x + pos.x);
		vert.add(bl.y + pos.y);
		vert.add(bl.z + pos.z);
		vert.add(n.x);
		vert.add(n.y);
		vert.add(n.z);
		vert.add(b);
		vert.add(tex.x);
		vert.add(tex.y + texHeight);
		vert.add(tx);
		vert.add(ty);
	}
	
	@Override
	public boolean canCombine(TextureFace o) {
		return o.tex.equals(tex);
	}
}
