package de.dakror.vloxlands.generate.structure;

import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.generate.Generator;

/**
 * @author Dakror
 */
public class BezierGenerator extends Generator
{
	int radius, x, y, z, height;
	byte[] ratio;
	float[] bezier;
	
	public BezierGenerator(int x, int y, int z, int radius, int height, byte[] ratio, float[] bezier)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.height = height;
		this.radius = radius;
		this.ratio = ratio;
		this.bezier = bezier;
	}
	
	@Override
	public void generate(Island island)
	{
		generateBezier(island, bezier, x, z, radius, y, height, ratio, true);
	}
}
