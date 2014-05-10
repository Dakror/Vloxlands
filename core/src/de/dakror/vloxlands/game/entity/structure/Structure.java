package de.dakror.vloxlands.game.entity.structure;

import de.dakror.vloxlands.game.entity.Entity;

/**
 * @author Dakror
 */
public class Structure extends Entity
{
	public Structure(float x, float y, float z, String model)
	{
		super(x, y, z, model);
		
		// btBoxShape bt = new btBoxShape(new Vector3(3, 1.6f, 3));
		// createPhysics(bt, 5f);
	}
}
