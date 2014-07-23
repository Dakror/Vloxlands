package de.dakror.vloxlands.game.item.tool;

import com.badlogic.gdx.math.Matrix4;

import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.creature.Human;

/**
 * @author Dakror
 */
public class BuildTool extends Tool
{
	@Override
	public void transformInHand(Matrix4 transform, Creature c)
	{
		if (((Human) c).firstJob() != null && ((Human) c).firstJob().isUsingTool() && ((Human) c).firstJob().getTool().isAssignableFrom(getClass())) super.transformInHand(transform, c, 40);
		else super.transformInHand(transform, c);
	}
}
