package de.dakror.vloxlands.game.entity.creature;

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
		
		if (path != null && !path.isDone())
		{
			Vector3 dif = path.get().cpy().add(Vloxlands.world.getIslands()[0].pos).add(boundingBox.getDimensions()).sub(posCache);
			
			if (dif.len() > speed) dif.limit(speed);
			else if (dif.len() < 0.1f)
			{
				// transform.setToRotation(Vector3.Y, 0).translate(posCache);
				// transform.rotate(Vector3.Y, new Vector2(path.get().z - posCache.z, path.get().x - posCache.x).angle() - 180);
				path.next();
				if (path.isDone()) onReachTarget();
			}
			
			transform.trn(dif);
		}
		
		// Gdx.app.log("", "" + posCache);
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
			Vector3 target = vs.voxel.cpy().add(Vloxlands.world.getIslands()[vs.island].getPos()).add(blockTrn);
			
			Vector3 v = posCache.sub(Vloxlands.world.getIslands()[0].pos);
			v.set(Math.round(v.x), Math.round(v.y) - 1, Math.round(v.z));
			path = AStar.findPath(v, vs.voxel, boundingBox.getDimensions());
			// transform.setToRotation(Vector3.Y, 0).translate(posCache);
			// transform.rotate(Vector3.Y, new Vector2(target.z - posCache.z, target.x - posCache.x).angle() - 180);
			// animationController.animate("walk", -1, 1, null, 0);
			selected = true;
		}
	}
	
	// -- events -- //
	public void onReachTarget()
	{
		path = null;
		animationController.animate(null, 0);
	}
}
