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
	final Vector3 dim = new Vector3();
	
	protected Vector3 voxelPos;
	
	protected StaticEntity(float x, float y, float z, String model)
	{
		super(x, y, z, model);
		voxelPos = new Vector3(Math.round(x), Math.round(y), Math.round(z));
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
	
	public boolean canBePlaced()
	{
		int width = (int) Math.ceil(boundingBox.getDimensions().x);
		int height = (int) Math.ceil(boundingBox.getDimensions().y);
		int depth = (int) Math.ceil(boundingBox.getDimensions().z);
		
		for (int i = 0; i < width; i++)
			for (int j = -1; j < height; j++)
				for (int k = 0; k < depth; k++)
				{
					if (j == -1 && island.get(i + voxelPos.x, j + voxelPos.y + 1, k + voxelPos.z) == 0) return false;
					else if (j > -1 && island.get(i + voxelPos.x, j + voxelPos.y + 1, k + voxelPos.z) != 0) return false;
				}
		
		for (Entity s : island.getEntities())
			if (s instanceof StaticEntity && intersects((StaticEntity) s)) return false;
		
		return true;
	}
	
	public boolean intersects(StaticEntity o)
	{
		float lx = Math.abs(posCache.x - o.posCache.x);
		float sumx = (dim.x / 2.0f) + (o.dim.x / 2.0f);
		
		float ly = Math.abs(posCache.y - o.posCache.y);
		float sumy = (dim.y / 2.0f) + (o.dim.y / 2.0f);
		
		float lz = Math.abs(posCache.z - o.posCache.z);
		float sumz = (dim.z / 2.0f) + (o.dim.z / 2.0f);
		
		return (lx <= sumx && ly <= sumy && lz <= sumz);
	}
	
	
	@Override
	public void save(ByteArrayOutputStream baos) throws IOException
	{
		super.save(baos);
		
		Bits.putVector3(baos, voxelPos);
	}
}
