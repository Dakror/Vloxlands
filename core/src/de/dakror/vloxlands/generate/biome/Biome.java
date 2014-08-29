package de.dakror.vloxlands.generate.biome;

import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.generate.Generator;
import de.dakror.vloxlands.generate.WorldGenerator;

/**
 * @author Dakror
 */
public abstract class Biome extends Generator
{
	@Override
	@Deprecated
	public void generate(WorldGenerator worldGen, Island island)
	{}
	
	public abstract void generate(WorldGenerator worldGen, Island island, int radius);
}
