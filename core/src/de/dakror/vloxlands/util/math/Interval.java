package de.dakror.vloxlands.util.math;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.dakror.vloxlands.util.Savable;

/**
 * @author Dakror
 */
public class Interval implements Comparable<Interval>, Savable
{
	int start, length;
	byte type;
	
	public Interval()
	{}
	
	public Interval(byte type, int start, int length)
	{
		this.type = type;
		this.start = start;
		this.length = length;
	}
	
	public Interval set(byte type, int start, int length)
	{
		this.type = type;
		this.start = start;
		this.length = length;
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

	public void shrink()
	{
		length--;
	}
	
	public void extend()
	{
		length++;
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

	@Override
	public String toString()
	{
		return start + "-[" + type + "]-" + (start + length);
	}
	
	@Override
	public void save(ByteArrayOutputStream baos) throws IOException
	{
		Bits.putInt(baos, length);
		baos.write(type);
	}
}
