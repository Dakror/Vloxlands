package de.dakror.vloxlands.game.entity.creature;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.ai.Path;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.layer.GameLayer;

/**
 * @author Dakror
 */
public abstract class Creature extends Entity implements AnimationListener
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
		modelInstance.transform.translate(blockTrn);
		blockTrn.add(boundingBox.getDimensions().cpy().scl(0.5f));
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
					Vector3 target = path.get().cpy().add(GameLayer.world.getIslands()[0].pos).add(blockTrn);
					Vector3 dif = target.cpy().sub(posCache);
					transform.setToRotation(Vector3.Y, 0).translate(posCache);
					transform.rotate(Vector3.Y, new Vector2(target.z - posCache.z, target.x - posCache.x).angle() - 180);
					
					if (dif.len() > speed) dif.limit(speed);
					else
					{
						if (path.isDone()) onReachTarget();
						else path.next();
					}
					transform.trn(dif);
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
		Vector3 v = posCache.cpy().sub(GameLayer.world.getIslands()[0].pos).sub(boundingBox.getDimensions().x / 2, boundingBox.getDimensions().y / 2, boundingBox.getDimensions().z / 2); // TODO: multi island support
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
		if (path.getGhostTarget() != null)
		{
			Vector3 target = path.getGhostTarget().cpy().add(GameLayer.world.getIslands()[0].pos).add(blockTrn);
			transform.setToRotation(Vector3.Y, 0).translate(posCache);
			transform.rotate(Vector3.Y, new Vector2(target.z - posCache.z, target.x - posCache.x).angle() - 180);
		}
	}
	
	@Override
	public void onEnd(AnimationDesc animation)
	{}
	
	@Override
	public void onLoop(AnimationDesc animation)
	{}
}
