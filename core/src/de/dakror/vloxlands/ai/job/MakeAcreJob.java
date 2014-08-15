package de.dakror.vloxlands.ai.job;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.item.tool.FarmTool;
import de.dakror.vloxlands.game.voxel.Voxel;

/**
 * @author Dakror
 */
public class MakeAcreJob extends PlaceJob
{
	public MakeAcreJob(Human human, Vector3 target, Voxel voxel, boolean persistent)
	{
		super(human, target, voxel, persistent);
		
		tool = FarmTool.class;
		animation = "mine_lower";
	}
}
