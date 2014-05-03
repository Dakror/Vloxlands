package de.dakror.vloxlands.game.entity;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.world.World;

/**
 * @author Dakror
 */
public class Creature extends Entity
{
	btPairCachingGhostObject ghostObject;
	btKinematicCharacterController controller;
	
	boolean airborne;
	float climbHeight;
	float speed;
	
	public Creature(float x, float y, float z, Vector3 trn, String model)
	{
		super(x, y, z, trn, model);
	}
	
	@Override
	protected void createPhysics(btConvexShape shape, float mass)
	{
		super.createPhysics(shape, mass);
		
		ghostObject = new btPairCachingGhostObject();
		ghostObject.setWorldTransform(transform);
		ghostObject.setCollisionShape(collisionShape);
		ghostObject.setCollisionFlags(CollisionFlags.CF_CHARACTER_OBJECT);
		ghostObject.setActivationState(Collision.DISABLE_DEACTIVATION);
		controller = new btKinematicCharacterController(ghostObject, collisionShape, climbHeight);
		
		Vloxlands.world.getCollisionWorld().addCollisionObject(ghostObject, World.ENTITY_FLAG, World.ALL_FLAG);
		Vloxlands.world.getCollisionWorld().addAction(controller);
	}
	
	@Override
	public void updateTransform()
	{
		ghostObject.getWorldTransform(transform);
	}
	
	@Override
	public void tick(int tick)
	{
		super.tick(tick);
		
		controller.setWalkDirection(new Vector3(0, 0, speed));
		// Vector3 from = transform.getTranslation(new Vector3());
		// Vector3 to = from.cpy().set(from.x, -1, from.z);
		// ClosestRayResultCallback crrc = new ClosestRayResultCallback(from, to);
		// crrc.setCollisionFilterGroup(World.ENTITY_FLAG);
		// crrc.setCollisionFilterMask(World.ALL_FLAG);
		// Vloxlands.world.getCollisionWorld().rayTest(from, to, crrc);
		//
		// if (crrc.hasHit())
		// {
		// float distance = crrc.getHitPointWorld().distance(from);
		//
		// airborne = distance > 0;
		// }
		// else airborne = true;
		//
		// crrc.dispose();
	}
	
	@Override
	public void update()
	{
		super.update();
	}
	
	public boolean isAirborne()
	{
		return airborne;
	}
	
	public void setAirborne(boolean airborne)
	{
		this.airborne = airborne;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		ghostObject.dispose();
		controller.dispose();
	}
}
