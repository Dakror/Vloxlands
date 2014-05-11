package de.dakror.vloxlands.game.entity.creature;



/**
 * @author Dakror
 */
public class Human extends Creature
{
	public Human(float x, float y, float z)
	{
		super(x, y, z, "models/humanblend/humanblend.g3db");
		name = "Mensch";
		
		speed = 0.05f;
		climbHeight = 1;
		// animationController.setAnimation("walk", -1);
	}
}
