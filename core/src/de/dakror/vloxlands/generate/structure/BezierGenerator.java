package de.dakror.vloxlands.generate.structure;

import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.generate.Generator;

/**
 * @author Dakror
 */
public class BezierGenerator extends Generator
{
	int radius, topLayers, x, y, z;
	byte[] ratio;
	float[] bezier;

	public BezierGenerator(int x, int y, int z, int radius, int topLayers, byte[] ratio, float[] bezier)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.topLayers = topLayers;
		this.ratio = ratio;
		this.bezier = bezier;
	}

	@Override
	public void generate(Island island)
	{
		generateBezier(island, bezier, x, z, radius, y, topLayers, ratio, true);
	}
}
