package de.dakror.vloxlands.util;

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
public class DDirectionalShadowLight extends DirectionalShadowLight
{
	int maxSize;
	
	public DDirectionalShadowLight(int shadowQuality, float shadowViewportWidth, float shadowViewportHeight, float shadowNear, float shadowFar)
	{
		super(Gdx.graphics.getWidth(), Gdx.graphics.getWidth(), shadowViewportWidth, shadowViewportHeight, shadowNear, shadowFar);
		
		IntBuffer ib = BufferUtils.newIntBuffer(16);
		Gdx.gl.glGetIntegerv(GL20.GL_MAX_VIEWPORT_DIMS, ib);
		maxSize = ib.get(0);
		
		setShadowQuality(shadowQuality);
	}
	
	public void setShadowQuality(int shadowQuality)
	{
		int size = Math.min(maxSize, Gdx.graphics.getWidth() * (shadowQuality + 1));
		
		fbo = new FrameBuffer(Format.RGBA8888, size, size, true);
		
		Gdx.app.log("DDirectionalShadowLight.setShadowQuality", "Setting Shadow quality to " + shadowQuality);
	}
}
