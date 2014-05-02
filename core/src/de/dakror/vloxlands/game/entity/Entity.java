package de.dakror.vloxlands.game.entity;

import java.util.UUID;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Disposable;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.world.World;
import de.dakror.vloxlands.util.Tickable;

/**
 * @author Dakror
 */
public abstract class Entity implements Tickable, Disposable
{
	static class MotionState extends btMotionState
	{
		private final Matrix4 transform;
		
		public MotionState(final Matrix4 transform)
		{
			this.transform = transform;
		}
		
		@Override
		public void getWorldTransform(final Matrix4 worldTrans)
		{
			worldTrans.set(transform);
		}
		
		@Override
		public void setWorldTransform(final Matrix4 worldTrans)
		{
			transform.set(worldTrans);
		}
	}
	
	Matrix4 transform;
	
	public ModelInstance modelInstance;
	
	int id;
	String name;
	
	float weight;
	float uplift;
	
	boolean markedForRemoval;
	boolean airborne;
	
	btConvexShape collisionShape;
	btRigidBody rigidBody;
	btPairCachingGhostObject ghostObject;
	btKinematicCharacterController controller;
	
	MotionState motionState;
	
	public Entity(float x, float y, float z, Vector3 trn, String model)
	{
		id = UUID.randomUUID().hashCode();
		modelInstance = new ModelInstance(Vloxlands.assets.get(model, Model.class), new Matrix4().translate(x, y, z).trn(trn));
		markedForRemoval = false;
	}
	
	protected void createPhysics(btConvexShape shape, float mass)
	{
		collisionShape = shape;
		transform = modelInstance.transform;
		motionState = new MotionState(modelInstance.transform);
		
		ghostObject = new btPairCachingGhostObject();
		ghostObject.setWorldTransform(transform);
		ghostObject.setCollisionShape(collisionShape);
		ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
		
		controller = new btKinematicCharacterController(ghostObject, collisionShape, 0.25f);
		
		rigidBody = new btRigidBody(mass, motionState, collisionShape);
		
		Vloxlands.world.getCollisionWorld().addRigidBody(rigidBody);
		Vloxlands.world.getCollisionWorld().addCollisionObject(ghostObject, World.ENTITY_FLAG, World.ALL_FLAG);
		Vloxlands.world.getCollisionWorld().addAction(controller);
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public float getWeight()
	{
		return weight;
	}
	
	public void setWeight(float weight)
	{
		this.weight = weight;
	}
	
	public float getUplift()
	{
		return uplift;
	}
	
	public void setUplift(float uplift)
	{
		this.uplift = uplift;
	}
	
	public boolean isAirborne()
	{
		return airborne;
	}
	
	public void setAirborne(boolean airborne)
	{
		this.airborne = airborne;
	}
	
	public Matrix4 getTransform()
	{
		return transform;
	}
	
	public int getId()
	{
		return id;
	}
	
	public boolean isMarkedForRemoval()
	{
		return markedForRemoval;
	}
	
	@Override
	public void tick(int tick)
	{
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
	
	public void update()
	{
		// do translations, rotations here
	}
	
	public void updateTransform()
	{
		ghostObject.getWorldTransform(transform);
	}
	
	@Override
	public void dispose()
	{
		rigidBody.dispose();
		controller.dispose();
		ghostObject.dispose();
		collisionShape.dispose();
	}
	
	// -- events -- //
	
	public void onSpawn()
	{}
	
	// -- abstracts -- //
}
