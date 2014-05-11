package de.dakror.vloxlands.game.entity.creature;

import de.dakror.vloxlands.game.entity.Entity;

/**
 * @author Dakror
 */
public class Creature extends Entity
{
	protected boolean airborne;
	protected float climbHeight;
	protected float speed;
	protected float rotateSpeed = 20;
	
	public Creature(float x, float y, float z, String model)
	{
		super(x, y, z, model);
	}
	
	public boolean isAirborne()
	{
		return airborne;
	}
	
	public void setAirborne(boolean airborne)
	{
		this.airborne = airborne;
	}
}
