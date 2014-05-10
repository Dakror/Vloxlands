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
		
		// btBoxShape bt = new btBoxShape(new Vector3(0.5f, 1, 0.5f));
		// createPhysics(bt, 2.5f);
		// animationController.setAnimation("walk", -1);
	}
}
