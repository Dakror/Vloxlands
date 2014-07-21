package de.dakror.vloxlands.util.event;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.structure.Structure;

/**
 * @author Dakror
 */
public interface SelectionListener
{
	public void onVoxelSelection(VoxelSelection vs, boolean lmb, String[] action);
	
	public void onVoxelRangeSelection(Vector3 start, Vector3 end, boolean lmb, String[] action);
	
	public void onStructureSelection(Structure structure, boolean lmb, String[] action);
	
	public void onCreatureSelection(Creature creature, boolean lmb, String[] action);
}
