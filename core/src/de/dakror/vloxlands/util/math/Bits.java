package de.dakror.vloxlands.util.math;

import java.io.ByteArrayOutputStream;

/**
 * Copied from {@link java.io.Bits} and slightly modified to work with {@link java.io.ByteArrayOutputStream}
 * 
 * @author Dakror
 */
public class Bits
{
	public static void putBoolean(ByteArrayOutputStream baos, boolean val)
	{
		baos.write((byte) (val ? 1 : 0));
	}
	
	public static void putChar(ByteArrayOutputStream baos, char val)
	{
		baos.write((byte) (val >>> 8));
		baos.write((byte) (val));
	}
	
	public static void putShort(ByteArrayOutputStream baos, short val)
	{
		baos.write((byte) (val >>> 8));
		baos.write((byte) (val));
	}
	
	public static void putInt(ByteArrayOutputStream baos, int val)
	{
		baos.write((byte) (val >>> 24));
		baos.write((byte) (val >>> 16));
		baos.write((byte) (val >>> 8));
		baos.write((byte) (val));
	}
	
	public static void putFloat(ByteArrayOutputStream baos, float val)
	{
		putInt(baos, Float.floatToIntBits(val));
	}
	
	public static void putLong(ByteArrayOutputStream baos, long val)
	{
		baos.write((byte) (val >>> 56));
		baos.write((byte) (val >>> 48));
		baos.write((byte) (val >>> 40));
		baos.write((byte) (val >>> 32));
		baos.write((byte) (val >>> 24));
		baos.write((byte) (val >>> 16));
		baos.write((byte) (val >>> 8));
		baos.write((byte) (val));
	}
	
	public static void putDouble(ByteArrayOutputStream baos, double val)
	{
		putLong(baos, Double.doubleToLongBits(val));
	}
}
