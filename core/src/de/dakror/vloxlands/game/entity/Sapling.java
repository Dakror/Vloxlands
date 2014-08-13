package de.dakror.vloxlands.game.entity;

/**
 * @author Dakror
 */
public class Sapling extends Entity
{
	public Sapling(float x, float y, float z)
	{
		super(x, y, z, "models/entities/sapling/sapling.g3db");
		
		name = "Sapling";
		weight = 1f;
	}
}
