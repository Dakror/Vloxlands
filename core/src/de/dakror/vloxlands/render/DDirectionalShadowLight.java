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

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.BufferUtils;

/**
 * @author Dakror
 */
@SuppressWarnings("deprecation")
public class DDirectionalShadowLight extends DirectionalShadowLight {
	int maxSize;
	
	public DDirectionalShadowLight(int shadowQuality, float shadowViewportWidth, float shadowViewportHeight, float shadowNear, float shadowFar) {
		super(Gdx.graphics.getWidth(), Gdx.graphics.getWidth(), shadowViewportWidth, shadowViewportHeight, shadowNear, shadowFar);
		
		IntBuffer ib = BufferUtils.newIntBuffer(16);
		Gdx.gl.glGetIntegerv(GL20.GL_MAX_VIEWPORT_DIMS, ib);
		maxSize = ib.get(0);
		
		setShadowQuality(shadowQuality);
	}
	
	public void setShadowQuality(int shadowQuality) {
		int size = Math.min(maxSize, Gdx.graphics.getWidth() * (shadowQuality + 1));
		
		fbo = new FrameBuffer(Format.RGBA8888, size, size, true);
		
		Gdx.app.log("DDirectionalShadowLight.setShadowQuality", "Setting Shadow quality to " + shadowQuality);
	}
}
