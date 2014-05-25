package de.dakror.vloxlands.game.job;

import com.badlogic.gdx.Gdx;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.layer.GameLayer;
import de.dakror.vloxlands.util.event.VoxelSelection;

/**
 * @author Dakror
 */
public class ToolJob extends Job
{
	private VoxelSelection target;
	
	public ToolJob(Human human, VoxelSelection target, boolean persistent)
	{
		super(human, "walk" /* mine */, target.type.getMining(), persistent);
		this.target = target;
	}
	
	public VoxelSelection getTarget()
	{
		return target;
	}
	
	@Override
	public void tick(int tick)
	{}
	
	@Override
	public void onEnd()
	{
		super.onEnd();
		
		GameLayer.world.getIslands()[target.island].set(target.voxel.x, target.voxel.y, target.voxel.z, Voxel.get("AIR").getId());
		
		if (target.type.getItemdrop() != -128)
		{
			if (human.getCarryingItemStack() == null) human.setCarryingItemStack(new ItemStack(Item.getForId(target.type.getItemdrop()), 1));
			else human.getCarryingItemStack().add(1);
		}
		else Gdx.app.error("ToolAction.onEnd", "Voxel " + target.type.getName() + " has no ItemDrop!");
	}
}
