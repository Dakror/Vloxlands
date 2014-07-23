package de.dakror.vloxlands.generate;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.generate.biome.Biome;
import de.dakror.vloxlands.generate.biome.BiomeType;

public class IslandGenerator
{
	public static final int MIN = 48;
	public static final int MAX = 64;
	
	public static Island generate(WorldGenerator worldGen)
	{
		try
		{
			
			float yPos = 256 * MathUtils.random();
			int radius = MathUtils.random(MIN, MAX);
			
			BiomeType biome = BiomeType.values()[(int) (MathUtils.random() * BiomeType.values().length)];
			Biome gen = (Biome) Class.forName("de.dakror.vloxlands.generate.biome." + biome.getName().replace(" ", "") + "Biome").newInstance();
			
			Island island = new Island(biome);
			
			worldGen.step();
			island.setPos(new Vector3(0, yPos, 0));
			gen.generate(worldGen, island, radius);
			
			island.grassify();
			island.calculateInitBalance();
			worldGen.step();
			
			return island;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
