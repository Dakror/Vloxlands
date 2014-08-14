package de.dakror.vloxlands.game.entity.statics;

import de.dakror.vloxlands.game.entity.Entity;

/**
 * @author Dakror
 */
public class Wheat extends Entity
{
	public Wheat(float x, float y, float z)
	{
		super(x, y + 0.62f, z + 0.02f, "models/entities/wheat/wheat.g3db");
	}
}
