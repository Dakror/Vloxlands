package de.dakror.vloxlands.game.entity.creature;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.ai.path.Path;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.world.World;
import de.dakror.vloxlands.layer.GameLayer;

/**
 * @author Dakror
 */
public abstract class Creature extends Entity
{
	protected boolean airborne;
	protected float climbHeight;
	protected float speed;
	protected float speedAmp;
	protected float rotateSpeed = 20;
	protected Vector3 blockTrn;
	
	protected boolean canFly;
	
	public Path path;
	
	public Creature(float x, float y, float z, String model)
	{
		super(x, y, z, model);
		
		speedAmp = 1;
		blockTrn = new Vector3(((float) Math.ceil(boundingBox.getDimensions().x) - boundingBox.getDimensions().x) / 2, 1, ((float) Math.ceil(boundingBox.getDimensions().z) - boundingBox.getDimensions().z) / 2);
		transform.translate(blockTrn);
		blockTrn.add(boundingBox.getDimensions().cpy().scl(0.5f));
	}
	
	@Override
	public void render(ModelBatch batch, Environment environment, boolean minimapMode)
	{
		super.render(batch, environment, minimapMode);
		
		if (path != null && Vloxlands.showPathDebug && !minimapMode)
		{
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glLineWidth(2);
			GameLayer.shapeRenderer.setProjectionMatrix(GameLayer.camera.combined);
			GameLayer.shapeRenderer.identity();
			GameLayer.shapeRenderer.translate(island.pos.x, island.pos.y, island.pos.z);
			GameLayer.shapeRenderer.rotate(1, 0, 0, 90);
			GameLayer.shapeRenderer.begin(ShapeType.Line);
			GameLayer.shapeRenderer.setColor(Color.WHITE);
			
			GameLayer.shapeRenderer.translate(0, 0, -path.getLast().y - 1.0f - World.gap);
			GameLayer.shapeRenderer.circle(path.getLast().x + 0.5f, path.getLast().z + 0.5f, 0.25f, 100);
			GameLayer.shapeRenderer.translate(0, 0, -(-path.getLast().y - 1.0f - World.gap));
			
			for (int i = path.getIndex(); i < path.size() - 1; i++)
			{
				Vector3 start = path.get(i);
				Vector3 end = path.get(i + 1);
				GameLayer.shapeRenderer.line(start.x + 0.5f, start.z + 0.5f, -start.y - 1.0f - World.gap, end.x + 0.5f, end.z + 0.5f, -end.y - 1.0f - World.gap);
			}
			GameLayer.shapeRenderer.end();
		}
	}
	
	@Override
	public void tick(int tick)
	{
		super.tick(tick);
		
		if (path != null)
		{
			try
			{
				if (path.size() > 0)
				{
					Vector3 target = path.get().cpy().add(island.pos).add(blockTrn);
					Vector3 dif = target.cpy().sub(posCache);
					
					float rot = new Vector2(target.z - posCache.z, target.x - posCache.x).angle() - 180;
					
					// transform.rotate(Vector3.Y, rot - rotCache.getYaw());
					if (dif.len() > speed) dif.limit(speed);
					else
					{
						if (path.isDone()) onReachTarget();
						else path.next();
					}
					posCache.add(dif);
					transform.setTranslation(posCache);
				}
				else onReachTarget();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public boolean isAirborne()
	{
		return airborne;
	}
	
	public void setAirborne(boolean airborne)
	{
		this.airborne = airborne;
	}
	
	public Vector3 getVoxelBelow()
	{
		transform.getTranslation(posCache);
		
		Vector3 v = posCache.cpy().sub(island.pos).sub(boundingBox.getDimensions().x / 2, boundingBox.getDimensions().y / 2, boundingBox.getDimensions().z / 2);
		v.set(Math.round(v.x), Math.round(v.y) - 1, Math.round(v.z));
		
		return v;
	}
	
	public boolean canFly()
	{
		return canFly;
	}
	
	public int getHeight()
	{
		return (int) Math.ceil(boundingBox.getDimensions().y);
	}
	
	public float getRotationPerpendicular()
	{
		float yaw = rotCache.getYawRad();
		return (float) -Math.abs(Math.max(Math.sin(yaw), Math.cos(yaw)));
	}
	
	// -- events -- //
	public void onReachTarget()
	{
		rotateTowardsGhostTarget(path);
		path = null;
	}
	
	protected void rotateTowardsGhostTarget(Path path)
	{
		if (path != null && path.getGhostTarget() != null)
		{
			Vector3 target = path.getGhostTarget().cpy().add(island.pos).add(blockTrn);
			float rot = new Vector2(target.z - posCache.z, target.x - posCache.x).angle() - 180;
			transform.rotate(Vector3.Y, rot - rotCache.getYaw());
		}
	}
}
