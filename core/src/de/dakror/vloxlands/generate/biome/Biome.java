package de.dakror.vloxlands.generate.biome;

import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.generate.Generator;


/**
 * @author Dakror
 */
public abstract class Biome extends Generator
{
	@Override
	@Deprecated
	public void generate(Island island)
	{}

	public abstract void generate(Island island, int radius);
}
