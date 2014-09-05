package de.dakror.vloxlands.util;

import java.util.Arrays;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;

/**
 * @author Dakror
 */
public class D
{
	static long last = 0;
	
	public static void u()
	{
		if (last == 0) last = System.nanoTime();
		else
		{
			double dif = System.nanoTime() - last;
			p(r(dif) + "ns = " + r(dif /= 1000.0) + "µs = " + r(dif /= 1000.0) + "ms = " + r(dif /= 1000.0) + "s = " + r(dif /= 60.0) + "m = " + r(dif /= 60.0) + "h");
			last = 0;
		}
	}
	
	public static void p(Object... objects)
	{
		if (Gdx.app == null)
		{
			p2(objects);
			return;
		}
		
		if (objects.length == 1) Gdx.app.log("", "" + objects[0]);
		else Gdx.app.log("", Arrays.toString(objects));
	}
	
	private static void p2(Object... objects)
	{
		if (objects.length == 1) System.out.println("" + objects[0]);
		else System.out.println(Arrays.toString(objects));
	}
	
	public static String r(double d)
	{
		String s = (Math.round(d * 1000) / 1000.0) + "";
		while (s.length() < 10)
			s = " " + s;
		
		return s;
	}
	
	public static boolean android()
	{
		return Gdx.app.getType() == ApplicationType.Android;
	}
}
