package de.dakror.vloxlands.game.entity;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;


/**
 * @author Dakror
 */
public class Human extends Entity
{
	public Human(float x, float y, float z)
	{
		super(x, y, z, new Vector3(0, 0, 0), "models/humanblend/humanblend.g3db");
		name = "Mensch";
		
		createPhysics(new btBoxShape(new Vector3(0.3f, 1.6f, 0.5f)), 2.5f);
	}
}
