package de.dakror.vloxlands.game.query;

import de.dakror.vloxlands.ai.Path;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.structure.Structure;

public class PathBundle
{
	public Path path;
	public Structure structure;
	public Creature creature;
	public Query query;
	
	public PathBundle(Path path, Structure structure, Creature creature, Query query)
	{
		this.path = path;
		this.structure = structure;
		this.creature = creature;
		this.query = query;
	}
}
