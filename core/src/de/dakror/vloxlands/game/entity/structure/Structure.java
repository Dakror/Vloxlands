package de.dakror.vloxlands.game.entity.structure;

import de.dakror.vloxlands.game.entity.Entity;

/**
 * @author Dakror
 */
public class Structure extends Entity
{
	public Structure(float x, float y, float z, String model)
	{
		super(Math.round(x), Math.round(y), Math.round(z), model);
	}
}
