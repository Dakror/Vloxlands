package de.dakror.vloxlands.game.query;

import com.badlogic.gdx.math.Vector3;


/**
 * @author Dakror
 */
public class VoxelPos
{
	public int x, y, z;
	public byte b;
	
	final Vector3 v = new Vector3();
	
	public VoxelPos()
	{
		this(-1, -1, -1, (byte) 0);
	}
	
	public VoxelPos(Vector3 pos, byte b)
	{
		x = (int) pos.x;
		y = (int) pos.y;
		z = (int) pos.z;
		this.b = b;
	}
	
	public VoxelPos(int x, int y, int z, byte b)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.b = b;
	}
	
	public Vector3 getPos()
	{
		v.set(x, y, z);
		return v;
	}
	
	public void set(Vector3 pos)
	{
		x = (int) pos.x;
		y = (int) pos.y;
		z = (int) pos.z;
	}
}
