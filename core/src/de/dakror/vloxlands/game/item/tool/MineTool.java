package de.dakror.vloxlands.game.item.tool;

import com.badlogic.gdx.math.Matrix4;

import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.job.MineJob;

/**
 * @author Dakror
 */
public class MineTool extends Tool
{
	@Override
	public void transformInHand(Matrix4 transform, Creature c)
	{
		if (((Human) c).firstJob() instanceof MineJob) super.transformInHand(transform, c, 40);
		else super.transformInHand(transform, c);
	}
}
