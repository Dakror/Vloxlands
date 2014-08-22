package de.dakror.vloxlands.ai.job;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.statics.Wheat;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.voxel.MetaTags;

/**
 * @author Dakror
 */
public class HarvestWheatJob extends Job
{
	Vector3 target;
	
	public HarvestWheatJob(Human human, Vector3 target, boolean persistent)
	{
		super(human, "mine_lower", "Harvesting wheat", 2, persistent);
		
		this.target = target;
	}
	
	@Override
	public void onEnd()
	{
		super.onEnd();
		
		for (Entity e : human.getIsland().getEntities())
		{
			if (e instanceof Wheat && ((Wheat) e).getVoxelPos().equals(target))
			{
				e.setVisible(false);
				break;
			}
		}
		
		human.getIsland().setMeta(target.x, target.y, target.z, MetaTags.ACRE_PLANT_GROWING);
		
		if (human.getCarryingItemStack().isNull()) human.setCarryingItemStack(new ItemStack(Item.get("WHEAT"), Wheat.itemsForHarvest));
		else human.getCarryingItemStack().add(Wheat.itemsForHarvest);
	}
}
