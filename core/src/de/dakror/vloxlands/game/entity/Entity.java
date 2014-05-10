package de.dakror.vloxlands.game.entity;

import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
	
	public static final int LINES[][] = { { 0, 1 }, { 0, 3 }, { 0, 4 }, { 6, 7 }, { 6, 5 }, { 6, 2 }, { 1, 5 }, { 2, 3 }, { 4, 5 }, { 3, 7 }, { 1, 2 }, { 7, 4 } };
	
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
	
	// protected MotionState motionState;
	
	protected AnimationController animationController;
	
	public final Vector3 posCache = new Vector3();
	
	public Entity(float x, float y, float z, String model)
	{
		id = UUID.randomUUID().hashCode();
		modelInstance = new ModelInstance(Vloxlands.assets.get(model, Model.class));
		modelInstance.calculateBoundingBox(boundingBox = new BoundingBox());
		modelInstance.transform.translate(x, y, z);
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
		transform.getTranslation(posCache);
		// collisionShape.getAabb(rigidBody.getWorldTransform(), boundingBox.min, boundingBox.max);
		inFrustum = Vloxlands.camera.frustum.boundsInFrustum(boundingBox.getCenter().x + posCache.x, boundingBox.getCenter().y + posCache.y, boundingBox.getCenter().z + posCache.z, boundingBox.getDimensions().x / 2, boundingBox.getDimensions().y / 2, boundingBox.getDimensions().z / 2);
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
			
			Vloxlands.shapeRenderer.rect(-boundingBox.getDimensions().x / 2, -boundingBox.getDimensions().z / 2, boundingBox.getDimensions().x, boundingBox.getDimensions().z);
			Vloxlands.shapeRenderer.end();
			Gdx.gl.glLineWidth(1);
		}
		
		if (Vloxlands.debug)
		{
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Vloxlands.shapeRenderer.setProjectionMatrix(Vloxlands.camera.combined);
			Vloxlands.shapeRenderer.identity();
			Vloxlands.shapeRenderer.translate(posCache.x, posCache.y, posCache.z);
			Vloxlands.shapeRenderer.begin(ShapeType.Line);
			Vloxlands.shapeRenderer.setColor(Color.RED);
			Vector3[] crn = boundingBox.getCorners();
			for (int i = 0; i < LINES.length; i++)
			{
				Vector3 v = crn[LINES[i][0]];
				Vector3 w = crn[LINES[i][1]];
				Vloxlands.shapeRenderer.line(v.x, v.y, v.z, w.x, w.y, w.z, Color.RED, Color.RED);
			}
			Vloxlands.shapeRenderer.end();
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
