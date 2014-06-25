package de.dakror.vloxlands.generate.structure;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.generate.Generator;

public class SpikeGenerator extends Generator
{
	public static final float[] BEZIER = { 1.0F, 1.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.0F, 0.0F };
	int radius;
	int topLayers;
	int x;
	int y;
	int z;
	
	public SpikeGenerator(int x, int y, int z, int radius, int topLayers)
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
		int MAXRAD = (int) (radius * 0.3f + 5);
		int rad = Math.round(MathUtils.random() * (radius * 0.3f)) + 3;
		
		Vector2 highest = getHighestBezierValue(TopLayerGenerator.BEZIER);
		int radiusAt0 = (int) (highest.y * radius);
		
		Vector2 pos = getRandomCircleInCircle(new Vector2(x, z), radiusAt0, rad);
		
		int h = (int) (0.3f * ((MAXRAD - rad) * (radiusAt0 - pos.cpy().sub(new Vector2(x, z)).len()) + topLayers));
		h = Math.min(h, Island.SIZE - topLayers - 10);
		
		island.set((int) pos.x, -1 + y, (int) pos.y, Voxel.get("STONE").getId());
		
		generateBezier(island, BEZIER, (int) pos.x, (int) pos.y /* Z */, rad, (int) (y - highest.x * topLayers), h, createRatio(new byte[] { Voxel.get("STONE").getId(), Voxel.get("DIRT").getId() }, new int[] { 5, 1 }), false);
	}
}
