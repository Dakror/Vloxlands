package de.dakror.vloxlands.util.interf;

import java.util.Date;
import java.util.List;

/**
 * @author Dakror
 */
public interface PlatformSpecifics
{
	public String formatDate(String format, Date date);
	
	public String formatNumber(long number, int digits, int base);
	
	public <T> List<T> createConcurrentList();
}
