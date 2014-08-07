package de.dakror.vloxlands.game.item.tool;

import com.badlogic.gdx.math.Matrix4;

import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.creature.Human;

/**
 * @author Dakror
 */
public class ChopTool extends Tool
{
	@Override
	public void transformInHand(Matrix4 transform, Creature c)
	{
		if (((Human) c).firstJob() != null && ((Human) c).firstJob().getTool() == getClass()) super.transformInHand(transform, c, 60);
		else super.transformInHand(transform, c);
	}
}
