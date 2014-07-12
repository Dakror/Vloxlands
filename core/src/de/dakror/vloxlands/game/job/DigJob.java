package de.dakror.vloxlands.game.job;

import com.badlogic.gdx.Gdx;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.util.event.VoxelSelection;

/**
 * @author Dakror
 */
public class DigJob extends DestroyVoxelJob
{
	private VoxelSelection target;

	public DigJob(Human human, VoxelSelection target, boolean persistent)
	{
		super(human, "walk" /* mine */, (persistent ? "Auto. d" : "d") + "iging " + target.type.getName(), target.type.getMining(), persistent);
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

		target.island.set(target.voxel.x, target.voxel.y, target.voxel.z, Voxel.get("AIR").getId());

		if (target.type.hasItemdrop())
		{
			if (human.getCarryingItemStack().isNull()) human.setCarryingItemStack(new ItemStack(Item.getForId(target.type.getItemdrop()), 1));
			else human.getCarryingItemStack().add(1);
		}
		else Gdx.app.error("DigJob.onEnd", "Voxel " + target.type.getName() + " has no ItemDrop!");
	}
}