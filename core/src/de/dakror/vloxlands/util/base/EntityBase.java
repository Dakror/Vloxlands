package de.dakror.vloxlands.util.base;

import com.badlogic.gdx.utils.Disposable;

import de.dakror.vloxlands.util.Tickable;
import de.dakror.vloxlands.util.event.EventListener;
import de.dakror.vloxlands.util.event.VoxelSelection;

/**
 * @author Dakror
 */
public abstract class EntityBase implements Tickable, Disposable, EventListener
{
	@Override
	public void onVoxelSelection(VoxelSelection vs)
	{}
	
	@Override
	public void dispose()
	{}
	
	@Override
	public void tick(int tick)
	{}
}
