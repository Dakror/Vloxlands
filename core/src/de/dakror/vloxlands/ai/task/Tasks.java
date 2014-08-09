package de.dakror.vloxlands.ai.task;

import com.badlogic.gdx.math.Vector2;

import de.dakror.vloxlands.game.item.inv.ResourceList;

/**
 * @author Dakror
 */
public class Tasks
{
	public static final Task human = new DefaultTask("human", "Human Helper", "Make a human helper.", new Vector2(3, 6), 60 * 30, null, new ResourceList().setCostPopulation(1));
}
