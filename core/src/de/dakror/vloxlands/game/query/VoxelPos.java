package de.dakror.vloxlands.game.query;


/**
 * @author Dakror
 */
public class VoxelPos
{
	public int x, y, z;
	public byte b;
	
	public VoxelPos()
	{
		this(-1, -1, -1, (byte) 0);
	}
	
	public VoxelPos(int x, int y, int z, byte b)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.b = b;
	}
}
