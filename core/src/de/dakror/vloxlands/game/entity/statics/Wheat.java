package de.dakror.vloxlands.game.entity.statics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.game.entity.StaticEntity;
import de.dakror.vloxlands.game.voxel.MetaTags;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.util.math.Bits;

/**
 * @author Dakror
 */
public class Wheat extends StaticEntity
{
	int growTicks;
	int growTicksLeft;
	boolean managed;
	int level;
	int perLevel;
	
	public static int itemsForHarvest = 5;
	
	public Wheat(float x, float y, float z)
	{
		super(x, y, z, "entities/wheat/wheat0[16].vxi");
		growTicks = growTicksLeft = Game.dayInTicks / 3 * 2;
		perLevel = growTicks / 5;
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		level = 0;
		island.set(voxelPos.x, voxelPos.y, voxelPos.z, Voxel.get("ACRE").getId());
		island.setMeta(voxelPos.x, voxelPos.y, voxelPos.z, MetaTags.ACRE_PLANT_GROWING);
	}
	
	@Override
	public void tick(int tick)
	{
		super.tick(tick);
		
		// if (level < 4)
		// {
		// if (StateTools.isWorkingTime()) growTicksLeft--; // only grows in sunlight, so <code>initial time * 2</code> = real time is takes
		//
		// if (growTicksLeft % perLevel == 0)
		// {
		// level++;
		// modelInstance = new ModelInstance(Vloxlands.assets.get("models/" + "entities/wheat/wheat" + level + "[16].vxi", Model.class));
		// }
		// if (level == 4) island.setMeta(voxelPos.x, voxelPos.y, voxelPos.z, MetaTags.ACRE_PLANT_GROWN);
		// }
	}
	
	@Override
	public void save(ByteArrayOutputStream baos) throws IOException
	{
		super.save(baos);
		
		Bits.putInt(baos, growTicksLeft);
		Bits.putBoolean(baos, managed);
	}
	
	public boolean isManaged()
	{
		return managed;
	}
	
	public void setManaged(boolean managed)
	{
		this.managed = managed;
	}
}
