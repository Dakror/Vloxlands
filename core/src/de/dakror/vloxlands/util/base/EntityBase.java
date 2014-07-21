package de.dakror.vloxlands.util.base;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.util.Tickable;
import de.dakror.vloxlands.util.event.SelectionListener;
import de.dakror.vloxlands.util.event.VoxelSelection;

/**
 * @author Dakror
 */
public abstract class EntityBase implements Tickable, Disposable, SelectionListener
{
	@Override
	public void onVoxelSelection(VoxelSelection vs, boolean lmb, String[] action)
	{}
	
	@Override
	public void onStructureSelection(Structure structure, boolean lmb, String[] action)
	{}
	
	@Override
	public void onCreatureSelection(Creature creature, boolean lmb, String[] action)
	{}
	
	@Override
	public void onVoxelRangeSelection(Island island, Vector3 start, Vector3 end, boolean lmb, String[] action)
	{}
	
	@Override
	public void dispose()
	{}
	
	@Override
	public void tick(int tick)
	{}
}
