package de.dakror.vloxlands.util.math;

import com.binarydreamers.trees.IntegerInterval;

/**
 * @author Dakror
 */
public class VoxelInterval extends IntegerInterval
{
	byte type;
	
	public VoxelInterval()
	{
		this((byte) 0, 0, 0);
	}
	
	public VoxelInterval(byte type, int lower, int upper)
	{
		super(lower, upper);
		this.type = type;
	}
	
	public VoxelInterval set(byte type, int lower, int upper)
	{
		this.type = type;
		this.lower = lower;
		this.upper = upper;

		return this;
	}

	public byte getType()
	{
		return type;
	}
	
	public void setType(byte type)
	{
		this.type = type;
	}

	@Override
	public String toString()
	{
		return getLower() + "-[" + type + "]-" + getUpper();
	}
}
