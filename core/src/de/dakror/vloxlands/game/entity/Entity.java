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
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.world.World;
import de.dakror.vloxlands.layer.GameLayer;
import de.dakror.vloxlands.util.base.EntityBase;

/**
 * @author Dakror
 */
public abstract class Entity extends EntityBase
{
	public static final int LINES[][] = { { 0, 1 }, { 0, 3 }, { 0, 4 }, { 6, 7 }, { 6, 5 }, { 6, 2 }, { 1, 5 }, { 2, 3 }, { 4, 5 }, { 3, 7 }, { 1, 2 }, { 7, 4 } };
	
	protected Matrix4 transform;
	
	public ModelInstance modelInstance;
	
	protected int id;
	protected String name;
	
	protected float weight;
	protected float uplift;
	
	public boolean inFrustum;
	public boolean hovered;
	public boolean wasSelected;
	public boolean selected;
	
	protected boolean markedForRemoval;
	protected BoundingBox boundingBox;
	
	protected AnimationController animationController;
	
	public final Vector3 posCache = new Vector3();
	public final Quaternion rotCache = new Quaternion();
	
	public Entity(float x, float y, float z, String model)
	{
		id = UUID.randomUUID().hashCode();
		modelInstance = new ModelInstance(Vloxlands.assets.get(model, Model.class));
		modelInstance.calculateBoundingBox(boundingBox = new BoundingBox());
		
		modelInstance.transform.translate(x, y, z).translate(boundingBox.getDimensions().cpy().scl(0.5f));
		
		animationController = new AnimationController(modelInstance);
		markedForRemoval = false;
		transform = modelInstance.transform;
		
		transform.getTranslation(posCache);
		
		GameLayer.instance.addListener(this);
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
	
	public BoundingBox getBoundingBox()
	{
		return boundingBox;
	}
	
	public AnimationController getAnimationController()
	{
		return animationController;
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
		transform.getRotation(rotCache);
		inFrustum = GameLayer.camera.frustum.boundsInFrustum(boundingBox.getCenter().x + posCache.x, boundingBox.getCenter().y + posCache.y, boundingBox.getCenter().z + posCache.z, boundingBox.getDimensions().x / 2, boundingBox.getDimensions().y / 2, boundingBox.getDimensions().z / 2);
	}
	
	public void getWorldBoundingBox(BoundingBox bb)
	{
		bb.min.set(boundingBox.min).add(posCache);
		bb.max.set(boundingBox.max).add(posCache);
		
		bb.set(bb.min, bb.max);
	}
	
	public void render(ModelBatch batch, Environment environment, boolean minimapMode)
	{
		batch.render(modelInstance, environment);
		
		renderAdditional(batch, environment);
		
		if ((hovered || selected) && !minimapMode)
		{
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glLineWidth(selected ? 3 : 2);
			GameLayer.shapeRenderer.setProjectionMatrix(GameLayer.camera.combined);
			GameLayer.shapeRenderer.identity();
			GameLayer.shapeRenderer.translate(posCache.x, posCache.y - boundingBox.getDimensions().y / 2 + boundingBox.getCenter().y + World.gap, posCache.z);
			GameLayer.shapeRenderer.rotate(1, 0, 0, 90);
			GameLayer.shapeRenderer.begin(ShapeType.Line);
			GameLayer.shapeRenderer.setColor(World.SELECTION);
			GameLayer.shapeRenderer.rect(-(float) Math.ceil(boundingBox.getDimensions().x) / 2, -(float) Math.ceil(boundingBox.getDimensions().z) / 2, (float) Math.ceil(boundingBox.getDimensions().x), (float) Math.ceil(boundingBox.getDimensions().z));
			GameLayer.shapeRenderer.end();
			Gdx.gl.glLineWidth(1);
		}
		
		if (Vloxlands.debug && !minimapMode)
		{
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			GameLayer.shapeRenderer.setProjectionMatrix(GameLayer.camera.combined);
			GameLayer.shapeRenderer.identity();
			GameLayer.shapeRenderer.translate(posCache.x, posCache.y - boundingBox.getDimensions().y / 2 + boundingBox.getCenter().y, posCache.z);
			GameLayer.shapeRenderer.begin(ShapeType.Line);
			GameLayer.shapeRenderer.setColor(Color.RED);
			GameLayer.shapeRenderer.box(-boundingBox.getDimensions().x / 2, 0, boundingBox.getDimensions().z / 2, boundingBox.getDimensions().x, boundingBox.getDimensions().y, boundingBox.getDimensions().z);
			GameLayer.shapeRenderer.end();
		}
	}
	
	public void renderAdditional(ModelBatch batch, Environment environment)
	{}
	
	public void update()
	{
		animationController.update(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void dispose()
	{
		GameLayer.instance.removeListener(this);
	}
	
	public void kill()
	{
		markedForRemoval = true;
	}
	
	// -- events -- //
	
	public void onSpawn()
	{}
	
	// -- abstracts -- //
}
