package de.dakror.vloxlands;

import de.dakror.vloxlands.layer.Layer;

/**
 * @author Dakror
 */
public class Updater extends Thread
{
	public static Updater instance;
	
	public int ticksPerSecond;
	long last;
	int tick;
	int ticks;
	
	public Updater()
	{
		instance = this;
		setName("Updater Thread");
		last = System.currentTimeMillis();
		start();
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			tick++;
			ticks++;
			
			for (Layer l : Vloxlands.instance.layers)
				l.tick(tick);
			
			if (System.currentTimeMillis() - last >= 1000)
			{
				ticksPerSecond = ticks;
				ticks = 0;
				last = System.currentTimeMillis();
			}
			
			try
			{
				Thread.sleep(Math.round(16.666f / Config.getGameSpeed()));
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
