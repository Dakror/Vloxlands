package de.dakror.vloxlands.generate.structure;

import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.generate.Generator;

public class TopLayerGenerator extends Generator
{
	public static final float[] BEZIER = { 1.0F, 0.6F, 0.3F, 1.0F, 0.7F, 0.4F, 0.0F, 0.5F };
	int radius, topLayers, x, y, z;
	byte[] ratio;

	public TopLayerGenerator(int x, int y, int z, int radius, int topLayers, byte[] ratio)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.topLayers = topLayers;
		this.ratio = ratio;
	}

	@Override
	public void generate(Island island)
	{
		generateBezier(island, BEZIER, x, z, radius, y, topLayers, ratio, true);
	}
}
