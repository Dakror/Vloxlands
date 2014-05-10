package de.dakror.vloxlands.game.entity.creature;

import de.dakror.vloxlands.game.entity.Entity;

/**
 * @author Dakror
 */
public class Creature extends Entity
{
	// protected btPairCachingGhostObject ghostObject;
	// protected btKinematicCharacterController controller;
	
	protected boolean airborne;
	protected float climbHeight;
	protected float speed;
	protected float rotateSpeed = 20;
	
	public Creature(float x, float y, float z, String model)
	{
		super(x, y, z, model);
	}
	
	// @Override
	// protected void createPhysics(btConvexShape shape, float mass)
	// {
	// super.createPhysics(shape, mass);
	//
	// ghostObject = new btPairCachingGhostObject();
	// ghostObject.setWorldTransform(transform);
	// ghostObject.setCollisionShape(collisionShape);
	// ghostObject.setCollisionFlags(CollisionFlags.CF_CHARACTER_OBJECT);
	// ghostObject.setActivationState(Collision.DISABLE_DEACTIVATION);
	// controller = new btKinematicCharacterController(ghostObject, collisionShape, climbHeight);
	//
	// Vloxlands.world.getCollisionWorld().addCollisionObject(ghostObject, World.ENTITY_FLAG, World.ALL_FLAG);
	// Vloxlands.world.getCollisionWorld().addAction(controller);
	// }
	//
	// @Override
	// public void updateTransform()
	// {
	// ghostObject.getWorldTransform(transform);
	// }
	
	@Override
	public void tick(int tick)
	{
		// ghostObject.getWorldTransform(transform);
		super.tick(tick);
		
		// transform.translate(-middleTranslate, 0, -middleTranslate).rotate(0, 1, 0, 5).translate(middleTranslate, 0, middleTranslate);
		// ghostObject.setWorldTransform(transform);
		
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
		// ghostObject.dispose();
		// controller.dispose();
	}
}
