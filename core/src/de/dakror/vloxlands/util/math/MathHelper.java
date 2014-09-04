package de.dakror.vloxlands.util.math;

import com.badlogic.gdx.math.MathUtils;

import de.dakror.vloxlands.Vloxlands;

/**
 * @author Dakror
 */
public class MathHelper
{
	public final static String[] levels = { "", "k", "m", "g", "t" };
	
	public static String formatBinarySize(long size, int digits)
	{
		return Vloxlands.specifics.formatNumber(size, digits, 1024).toUpperCase() + "B";
	}
	
	public static int[] indexShuffle(int size)
	{
		int[] arr = new int[size];
		for (int i = 0; i < size; i++)
			arr[i] = i;
		
		for (int i = 0; i < size; i++)
		{
			int ran = MathUtils.random(size - 1);
			int temp = arr[i];
			arr[i] = arr[ran];
			arr[ran] = temp;
		}
		
		return arr;
	}
}
