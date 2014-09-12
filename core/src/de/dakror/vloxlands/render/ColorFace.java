package de.dakror.vloxlands.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.util.Direction;

/**
 * @author Dakror
 */
public class ColorFace extends Face<ColorFace>
{
	public Color c;
	
	public ColorFace(Direction dir, Vector3 pos, Color c)
	{
		this(dir, pos, c, 1, 1, 1);
	}
	
	public ColorFace(Direction dir, Vector3 pos, Color c, float sizeX, float sizeY, float sizeZ)
	{
		super(dir, pos, sizeX, sizeY, sizeZ);
		this.c = c;
	}
	
	@Override
	public boolean canCombine(ColorFace o)
	{
		return c.equals(o.c);
	}
}
