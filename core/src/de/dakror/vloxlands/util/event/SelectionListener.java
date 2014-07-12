package de.dakror.vloxlands.util.event;

import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.structure.Structure;

/**
 * @author Dakror
 */
public interface SelectionListener
{
	public void onVoxelSelection(VoxelSelection vs, boolean lmb, String[] action);
	
	public void onStructureSelection(Structure structure, boolean lmb, String[] action);
	
	public void onCreatureSelection(Creature creature, boolean lmb, String[] action);
}
