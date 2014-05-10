package de.dakror.vloxlands.game.entity;

import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.world.World;
import de.dakror.vloxlands.util.base.EntityBase;

/**
 * @author Dakror
 */
public abstract class Entity extends EntityBase
{
	// static class MotionState extends btMotionState
	// {
	// private final Matrix4 transform;
	//
	// public MotionState(final Matrix4 transform)
	// {
	// this.transform = transform;
	// }
	//
	// @Override
	// public void getWorldTransform(final Matrix4 worldTrans)
	// {
	// worldTrans.set(transform);
	// }
	//
	// @Override
	// public void setWorldTransform(final Matrix4 worldTrans)
	// {
	// transform.set(worldTrans);
	// }
	// }
	
	protected Matrix4 transform;
	
	public ModelInstance modelInstance;
	
	protected int id;
	protected String name;
	
	protected float weight;
	protected float uplift;
	
	public boolean inFrustum;
	public boolean hovered;
	public boolean selected;
	
	protected boolean markedForRemoval;
	
	// protected btConvexShape collisionShape;
	// protected btRigidBody rigidBody;
	public BoundingBox boundingBox;
	public final Vector3 size = new Vector3();
	
	// protected MotionState motionState;
	
	protected AnimationController animationController;
	
	public final Vector3 posCache = new Vector3();
	
	public Entity(float x, float y, float z, String model)
	{
		id = UUID.randomUUID().hashCode();
		modelInstance = new ModelInstance(Vloxlands.assets.get(model, Model.class), new Matrix4().translate(x, y, z));
		boundingBox = new BoundingBox();
		animationController = new AnimationController(modelInstance);
		markedForRemoval = false;
		transform = modelInstance.transform;
	}
	
	// protected void createPhysics(btConvexShape shape, float mass)
	// {
	// collisionShape = shape;
	//
	// Vector3 localInertia = new Vector3();
	// collisionShape.calculateLocalInertia(mass, localInertia);
	//
	// motionState = new MotionState(modelInstance.transform);
	// rigidBody = new btRigidBody(mass, motionState, collisionShape);
	// rigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);
	// Vloxlands.world.getCollisionWorld().addRigidBody(rigidBody, World.ENTITY_FLAG, World.ALL_FLAG);
	// }
	
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
		// transform.getTranslation(posCache);
		// collisionShape.getAabb(rigidBody.getWorldTransform(), boundingBox.min, boundingBox.max);
		// boundingBox.set(boundingBox.min, boundingBox.max);
		// size.set(boundingBox.getDimensions().x, boundingBox.getDimensions().y, boundingBox.getDimensions().z);
		//
		// inFrustum = Vloxlands.camera.frustum.boundsInFrustum(boundingBox);
	}
	
	public void render(ModelBatch batch, Environment environment)
	{
		batch.render(modelInstance, environment);
		if (hovered || selected)
		{
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glLineWidth(selected ? 3 : 2);
			Vloxlands.shapeRenderer.setProjectionMatrix(Vloxlands.camera.combined);
			Vloxlands.shapeRenderer.identity();
			Vloxlands.shapeRenderer.translate(posCache.x, posCache.y, posCache.z);
			Vloxlands.shapeRenderer.rotate(1, 0, 0, 90);
			Vloxlands.shapeRenderer.begin(ShapeType.Line);
			Vloxlands.shapeRenderer.setColor(World.SELECTION);
			
			Vloxlands.shapeRenderer.rect(-size.x / 2, -size.z / 2, size.x, size.z);
			Vloxlands.shapeRenderer.end();
			Gdx.gl.glLineWidth(1);
		}
	}
	
	public void update()
	{
		animationController.update(Gdx.graphics.getDeltaTime());
		// do translations, rotations here
	}
	
	public void updateTransform()
	{}
	
	@Override
	public void dispose()
	{}
	
	// -- events -- //
	
	public void onSpawn()
	{}
	
	// -- abstracts -- //
}
