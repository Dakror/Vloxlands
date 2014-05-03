package de.dakror.vloxlands.game.entity;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;


/**
 * @author Dakror
 */
public class Human extends Creature
{
	public Human(float x, float y, float z)
	{
		super(x, y, z, new Vector3(), "models/humanblend/humanblend_nobones.g3db");
		name = "Mensch";
		
		speed = 0.05f;
		climbHeight = 1;
		
		BoundingBox bb = new BoundingBox();
		modelInstance.calculateBoundingBox(bb);
		modelInstance.calculateTransforms();
		btBoxShape bt = new btBoxShape(bb.getDimensions().cpy().scl(0.5f));
		createPhysics(bt, 2.5f);
		// animationController.setAnimation("walk", -1);
	}
}
