package de.dakror.vloxlands.ai.job;

import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.creature.Human;

/**
 * @author Dakror
 */
public class PlaceEntityJob extends Job {
	private Entity entity;
	
	public PlaceEntityJob(Human human, Entity entity, boolean persistent) {
		super(human, "deposit", "Placing " + entity.getName(), 1, persistent);
		this.entity = entity;
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		
		human.getIsland().addEntity(entity, true, false);
	}
}
