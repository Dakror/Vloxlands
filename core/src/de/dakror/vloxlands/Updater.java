package de.dakror.vloxlands;

import de.dakror.vloxlands.layer.Layer;

/**
 * @author Dakror
 */
public class Updater extends Thread
{
	int tick;
	
	public Updater()
	{
		setName("Updater Thread");
		start();
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			tick++;
			
			for (Layer l : Vloxlands.instance.layers)
				l.tick(tick);
			
			try
			{
				Thread.sleep(16, 667);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
