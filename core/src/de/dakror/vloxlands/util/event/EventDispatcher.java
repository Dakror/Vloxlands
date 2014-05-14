package de.dakror.vloxlands.util.event;

import com.badlogic.gdx.utils.Array;

/**
 * @author Dakror
 */
public class EventDispatcher
{
	static Array<EventListener> listeners = new Array<EventListener>();
	
	public static void addListener(EventListener value)
	{
		listeners.add(value);
	}
	
	public static boolean removeListener(EventListener value)
	{
		return listeners.removeValue(value, false);
	}
	
	public static void dispatchVoxelSelection(VoxelSelection vs, boolean lmb)
	{
		for (EventListener l : listeners)
			l.onVoxelSelection(vs, lmb);
	}
}
