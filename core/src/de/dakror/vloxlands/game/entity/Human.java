package de.dakror.vloxlands.game.entity;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;


/**
 * @author Dakror
 */
public class Human extends Entity
{
	public Human(float x, float y, float z)
	{
		super(x, y, z, new Vector3(), "models/humanblend/humanblend.g3db");
		name = "Mensch";
		
		BoundingBox bb = new BoundingBox();
		modelInstance.calculateTransforms();
		modelInstance.calculateBoundingBox(bb);
		btBoxShape bt = new btBoxShape(bb.getDimensions().cpy().scl(0.5f));
		createPhysics(bt, 2.5f);
		// animationController.setAnimation("walk", -1);
	}
}
