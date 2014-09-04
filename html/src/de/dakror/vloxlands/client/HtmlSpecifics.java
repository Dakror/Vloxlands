package de.dakror.vloxlands.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;

import de.dakror.vloxlands.client.emul.CopyOnWriteArrayList;
import de.dakror.vloxlands.util.interf.PlatformSpecifics;
import de.dakror.vloxlands.util.math.MathHelper;

/**
 * @author Dakror
 */
public class HtmlSpecifics implements PlatformSpecifics
{
	@Override
	public String formatDate(String format, Date date)
	{
		return DateTimeFormat.getFormat(format).format(date);
	}
	
	@Override
	public String formatNumber(long number, int digits, int base)
	{
		if (number == 0) return "0";
		for (int i = MathHelper.levels.length - 1; i > -1; i--)
			if (number >= (long) Math.pow(base, i))
			{
				NumberFormat nf = NumberFormat.getDecimalFormat();
				nf.overrideFractionDigits(digits, digits); // TODO: probably not working
				return nf.format(number / Math.pow(base, i)) + MathHelper.levels[i];
			}
		return null;
	}
	
	@Override
	public <T> List<T> createConcurrentList()
	{
		return new CopyOnWriteArrayList<T>();
	}
}
