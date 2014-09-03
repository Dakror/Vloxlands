package de.dakror.vloxlands.util.math;

import java.text.DecimalFormat;

import com.badlogic.gdx.math.MathUtils;

/**
 * @author Dakror
 */
public class MathHelper
{
	public static String formatBinarySize(long size, int digits)
	{
		return formatNumber(size, digits, 1024).toUpperCase() + "B";
	}
	
	public static String formatNumber(long size, int digits, int base)
	{
		final String[] levels = { "", "k", "m", "g", "t" };
		for (int i = levels.length - 1; i > -1; i--)
			if (size > (long) Math.pow(base, i))
			{
				DecimalFormat df = new DecimalFormat();
				df.setMaximumFractionDigits(digits);
				df.setMinimumFractionDigits(digits);
				return df.format(size / Math.pow(base, i)) + levels[i];
			}
		return null;
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
