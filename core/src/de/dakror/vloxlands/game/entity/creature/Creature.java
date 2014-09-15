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
import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.world.World;

/**
 * @author Dakror
 */
// TODO: saving
public abstract class Creature extends Entity
{
	protected boolean airborne;
	protected float climbHeight;
	protected float speed;
	protected float rotateSpeed = 20;
	
	protected boolean canFly;
	
	public Path path;
	
	public Creature(float x, float y, float z, String model)
	{
		super(x, y, z, model);
	}
	
	@Override
	public void render(ModelBatch batch, Environment environment, boolean minimapMode)
	{
		super.render(batch, environment, minimapMode);
		
		try
		{
			if (path != null && path.size() > 0 && Vloxlands.showPathDebug && !minimapMode)
			{
				Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
				Gdx.gl.glLineWidth(2);
				Vloxlands.shapeRenderer.setProjectionMatrix(Game.camera.combined);
				Vloxlands.shapeRenderer.identity();
				Vloxlands.shapeRenderer.translate(island.pos.x, island.pos.y, island.pos.z);
				Vloxlands.shapeRenderer.rotate(1, 0, 0, 90);
				Vloxlands.shapeRenderer.begin(ShapeType.Line);
				Vloxlands.shapeRenderer.setColor(Color.WHITE);
				
				Vloxlands.shapeRenderer.translate(0, 0, -path.getLast().y - 1.0f - World.gap);
				Vloxlands.shapeRenderer.circle(path.getLast().x + 0.5f, path.getLast().z + 0.5f, 0.25f, 100);
				Vloxlands.shapeRenderer.translate(0, 0, -(-path.getLast().y - 1.0f - World.gap));
				
				for (int i = path.getIndex(); i < path.size() - 1; i++)
				{
					Vector3 start = path.get(i);
					Vector3 end = path.get(i + 1);
					Vloxlands.shapeRenderer.line(start.x + 0.5f, start.z + 0.5f, -start.y - 1.0f - World.gap, end.x + 0.5f, end.z + 0.5f, -end.y - 1.0f - World.gap);
				}
				Vloxlands.shapeRenderer.end();
			}
		}
		catch (NullPointerException e)
		{
			Vloxlands.shapeRenderer.end();
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
					
					if (dif.len() > speed)
					{
						dif.limit(speed);
						
						posCache.add(dif);
						modelInstance.transform.setTranslation(posCache);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		if (path != null)
		{
			try
			{
				if (path.size() > 0)
				{
					modelInstance.transform.getRotation(rotCache);
					modelInstance.transform.getTranslation(posCache);
					
					Vector3 target = path.get().cpy().add(island.pos).add(blockTrn);
					Vector3 dif = target.cpy().sub(posCache);
					
					float rot = new Vector2(target.z - posCache.z, target.x - posCache.x).angle() - 180;
					modelInstance.transform.rotate(Vector3.Y, rot - rotCache.getYaw());
					if (dif.len() <= speed)
					{
						if (path.isDone()) onReachTarget();
						else path.next();
					}
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
		modelInstance.transform.getTranslation(posCache);
		
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
			modelInstance.transform.rotate(Vector3.Y, rot - rotCache.getYaw());
		}
	}
}
