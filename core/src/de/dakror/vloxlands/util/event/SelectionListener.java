package de.dakror.vloxlands.util.event;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.world.Island;

/**
 * @author Dakror
 */
public interface SelectionListener
{
	public void onVoxelSelection(VoxelSelection vs, boolean lmb);
	
	public void onVoxelRangeSelection(Island island, Vector3 start, Vector3 end, boolean lmb);
	
	public void onStructureSelection(Structure structure, boolean lmb);
	
	public void onCreatureSelection(Creature creature, boolean lmb);
}
