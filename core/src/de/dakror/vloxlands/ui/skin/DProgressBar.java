package de.dakror.vloxlands.ui.skin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

/**
 * Gently copied from the original libGDX ProgressBar.<br>
 * Modified to work as typical ProgressBar :D
 * 
 * @author Dakror
 */
public class DProgressBar extends Widget
{
	public static Color lossTint = Color.GRAY;
	
	float min, max, stepSize, value, animateFromValue;
	float animateDuration, animateTime;
	float position;
	boolean showLoss;
	float[] snapValues;
	float threshold;
	boolean shiftIgnoresSnap;
	Interpolation animateInterpolation = Interpolation.linear;
	
	Drawable bg;
	TextureRegion progress;
	
	public DProgressBar(float min, float max, float value, Skin skin)
	{
		this.min = min;
		this.max = max;
		this.value = value;
		stepSize = 1;
		
		bg = skin.getDrawable("outline");
		progress = skin.getRegion("progressBar");
	}
	
	@Override
	public void act(float delta)
	{
		super.act(delta);
		animateTime -= delta;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		float x = getX();
		float y = getY();
		float width = getWidth();
		float height = getMinHeight();
		float value = getVisualValue() / (max - min);
		
		bg.draw(batch, x, y, width, height);
		if (showLoss)
		{
			
			Color c = batch.getColor();
			batch.setColor(lossTint);
			batch.draw(progress.getTexture(), x + 8, y + 8, (width - 16) * animateFromValue, height - 16, progress.getRegionX(), progress.getRegionY(), Math.round(progress.getRegionWidth() * animateFromValue), progress.getRegionHeight(), false, false);
			batch.setColor(c);
		}
		
		batch.draw(progress.getTexture(), x + 8, y + 8, (width - 16) * value, height - 16, progress.getRegionX(), progress.getRegionY(), Math.round(progress.getRegionWidth() * value), progress.getRegionHeight(), false, false);
	}
	
	public void setAnimateDuration(float duration)
	{
		animateDuration = duration;
	}
	
	public void setAnimateInterpolation(Interpolation animateInterpolation)
	{
		if (animateInterpolation == null) throw new IllegalArgumentException("animateInterpolation cannot be null.");
		this.animateInterpolation = animateInterpolation;
	}
	
	public float getMin()
	{
		return min;
	}
	
	public float getMax()
	{
		return max;
	}
	
	@Override
	public float getMinHeight()
	{
		return bg.getMinHeight();
	}
	
	@Override
	public float getMinWidth()
	{
		return bg.getMinWidth();
	}
	
	public boolean setValue(float value)
	{
		value = clamp(Math.round(value / stepSize) * stepSize);
		if (!shiftIgnoresSnap || (!Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && !Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT))) value = snap(value);
		float oldValue = this.value;
		if (value == oldValue) return false;
		float oldVisualValue = getVisualValue();
		this.value = value;
		ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
		boolean cancelled = fire(changeEvent);
		if (cancelled) this.value = oldValue;
		else if (animateDuration > 0)
		{
			animateFromValue = oldVisualValue;
			animateTime = animateDuration;
		}
		Pools.free(changeEvent);
		return !cancelled;
	}
	
	public void setSnapToValues(float[] values, float threshold)
	{
		snapValues = values;
		this.threshold = threshold;
	}
	
	public float getVisualValue()
	{
		if (animateTime > 0) return animateInterpolation.apply(animateFromValue, value, 1 - animateTime / animateDuration);
		return value;
	}
	
	public void setShowLoss(boolean showLoss)
	{
		this.showLoss = showLoss;
	}
	
	protected float clamp(float value)
	{
		return MathUtils.clamp(value, min, max);
	}
	
	private float snap(float value)
	{
		if (snapValues == null) return value;
		for (int i = 0; i < snapValues.length; i++)
		{
			if (Math.abs(value - snapValues[i]) <= threshold) return snapValues[i];
		}
		return value;
	}
}
