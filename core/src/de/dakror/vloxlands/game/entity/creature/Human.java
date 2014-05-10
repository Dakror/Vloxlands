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
		
		boundingBox.set(boundingBox.min, boundingBox.min.cpy().add(boundingBox.getDimensions().x, boundingBox.getDimensions().z, boundingBox.getDimensions().y));
	}
}
