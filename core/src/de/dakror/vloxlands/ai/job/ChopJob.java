package de.dakror.vloxlands.ai.job;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.item.tool.ChopTool;
import de.dakror.vloxlands.game.voxel.Voxel;

/**
 * @author Dakror
 */
public class ChopJob extends Job
{
	final Vector3 target = new Vector3();
	
	int meta;
	
	public ChopJob(Human human, Vector3 target, int meta, boolean persistent)
	{
		super(human, "mine" /* chop */, "Chopping a tree", Voxel.get("WOOD").getMining(), persistent);
		this.target.set(target);
		this.meta = meta;
		tool = ChopTool.class;
	}
	
	@Override
	public void onEnd()
	{
		super.onEnd();
		human.getIsland().set(target.x, target.y + meta - 1, target.z, Voxel.get("AIR").getId());
		
		if (human.getCarryingItemStack().isNull()) human.setCarryingItemStack(new ItemStack(Item.getForId(Voxel.get("WOOD").getItemdrop()), 5));
		else human.getCarryingItemStack().add(5);
	}
}
