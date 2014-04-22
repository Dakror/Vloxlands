package de.dakror.vloxlands.generate.structure;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.generate.Generator;

public class TopLayerGenerator extends Generator
{
	public static final float[] BEZIER = { 1.0F, 0.6F, 0.3F, 1.0F, 0.7F, 0.4F, 0.0F, 0.5F };
	int radius;
	int topLayers;
	int x;
	int y;
	int z;
	
	public TopLayerGenerator(int x, int y, int z, int radius, int topLayers)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.topLayers = topLayers;
	}
	
	@Override
	public void generate(Island island)
	{
		generateBezier(island, BEZIER, x,  z,radius, y, topLayers, Generator.createRatio(new byte[] { Voxel.get("DIRT").getId(), Voxel.get("STONE").getId() }, new int[] { 30, 1 }), true);
	}
}
