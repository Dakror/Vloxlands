package de.dakror.vloxlands.util.math;

/**
 * @author Dakror
 */
public class Interval implements Comparable<Interval>
{
	int start, length;
	byte type;
	
	public Interval(byte type, int start, int length)
	{
		this.type = type;
		this.start = start;
		this.length = length;
	}
	
	public byte getType()
	{
		return type;
	}
	
	public void setType(byte type)
	{
		this.type = type;
	}
	
	public int getStart()
	{
		return start;
	}
	
	public void setStart(int start)
	{
		this.start = start;
	}
	
	public int getLength()
	{
		return length;
	}
	
	public void setLength(int length)
	{
		this.length = length;
	}
	
	@Override
	public int compareTo(Interval o)
	{
		return Integer.compare(start, o.start);
	}
}
