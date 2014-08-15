package de.dakror.vloxlands.game.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.util.math.Bits;

/**
 * @author Dakror
 */
public abstract class StaticEntity extends Entity
{
	protected Vector3 voxelPos;
	
	protected StaticEntity(float x, float y, float z, String model)
	{
		super(x, y, z, model);
		
		voxelPos = new Vector3(Math.round(x), Math.round(y), Math.round(z));
	}
	
	protected StaticEntity(float vx, float vy, float vz, float x, float y, float z, String model)
	{
		super(x, y, z, model);
		
		voxelPos = new Vector3(Math.round(vx), Math.round(vy), Math.round(vz));
	}
	
	public void updateVoxelPos()
	{
		transform.getTranslation(posCache);
		transform.getRotation(rotCache);
		Vector3 p = posCache.cpy().sub(island.pos).sub(boundingBox.getDimensions().cpy().scl(0.5f));
		voxelPos = new Vector3(Math.round(p.x), Math.round(p.y), Math.round(p.z));
	}
	
	public Vector3 getVoxelPos()
	{
		return voxelPos;
	}
	
	@Override
	public void save(ByteArrayOutputStream baos) throws IOException
	{
		super.save(baos);
		
		Bits.putVector3(baos, voxelPos);
	}
}
