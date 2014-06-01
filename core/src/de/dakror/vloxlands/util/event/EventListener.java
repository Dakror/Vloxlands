package de.dakror.vloxlands.util.event;

import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.structure.Structure;

/**
 * @author Dakror
 */
public interface EventListener
{
	public void onVoxelSelection(VoxelSelection vs, boolean lmb);
	
	public void onStructureSelection(Structure structure, boolean lmb);
	
	public void onCreatureSelection(Creature creature, boolean lmb);
}
