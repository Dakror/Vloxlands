package de.dakror.vloxlands.desktop;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.dakror.vloxlands.util.interf.PlatformSpecifics;
import de.dakror.vloxlands.util.math.MathHelper;

/**
 * @author Dakror
 */
public class DesktopSpecifics implements PlatformSpecifics
{
	@Override
	public String formatDate(String format, Date date)
	{
		return new SimpleDateFormat(format).format(date);
	}
	
	@Override
	public String formatNumber(long number, int digits, int base)
	{
		if (number == 0) return "0";
		for (int i = MathHelper.levels.length - 1; i > -1; i--)
			if (number >= (long) Math.pow(base, i))
			{
				DecimalFormat df = new DecimalFormat();
				df.setMaximumFractionDigits(digits);
				df.setMinimumFractionDigits(digits);
				df.setRoundingMode(RoundingMode.FLOOR);
				return df.format(number / Math.pow(base, i)) + MathHelper.levels[i];
			}
		return null;
	}
	
	@Override
	public <T> List<T> createConcurrentList()
	{
		return new CopyOnWriteArrayList<T>();
	}
}
