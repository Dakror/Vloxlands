package de.dakror.vloxlands.game.entity.creature;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;

import de.dakror.vloxlands.game.entity.Entity;


/**
 * @author Dakror
 */
public class Human extends Entity
{
	public Human(float x, float y, float z)
	{
		super(x, y, z, new Vector3(), "models/humanblend/humanblend.g3db");
		name = "Mensch";
		
		// speed = 0.05f;
		// climbHeight = 1;
		// middleTranslate = -0.45f;
		
		Vector3 v = boundingBox.getDimensions().cpy().scl(0.5f);
		btBoxShape bt = new btBoxShape(new Vector3(v.x, v.z, v.y));
		createPhysics(bt, 2.5f);
		// animationController.setAnimation("walk", -1);
	}
}
