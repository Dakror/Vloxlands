package de.dakror.vloxlands.game.entity.creature;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.ai.AStar;
import de.dakror.vloxlands.ai.Path;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.util.event.VoxelSelection;

/**
 * @author Dakror
 */
public class Creature extends Entity
{
	protected boolean airborne;
	protected float climbHeight;
	protected float speed;
	protected float speedAmp;
	protected float rotateSpeed = 20;
	protected Vector3 blockTrn;
	
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
			Vector3 target = path.get().cpy().add(Vloxlands.world.getIslands()[0].pos).add(blockTrn);
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
	public void onVoxelSelection(VoxelSelection vs, boolean lmb)
	{
		if (wasSelected && !lmb)
		{
			path = AStar.findPath(getVoxelBelow(), vs.voxel, boundingBox.getDimensions());
			
			animationController.animate("walk", -1, 1, null, 0);
			selected = true;
		}
	}
	
	public Vector3 getVoxelBelow()
	{
		Vector3 v = posCache.sub(Vloxlands.world.getIslands()[0].pos).sub(boundingBox.getDimensions().x / 2, boundingBox.getDimensions().y / 2, boundingBox.getDimensions().z / 2);
		v.set(Math.round(v.x), Math.round(v.y) - 1, Math.round(v.z));
		
		return v;
	}
	
	// -- events -- //
	public void onReachTarget()
	{
		path = null;
		animationController.animate(null, 0);
	}
}
