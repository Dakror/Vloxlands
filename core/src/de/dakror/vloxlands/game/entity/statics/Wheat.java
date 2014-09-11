package de.dakror.vloxlands.game.entity.statics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.dakror.vloxlands.ai.state.StateTools;
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
	
	public static int itemsForHarvest = 5;
	
	public Wheat(float x, float y, float z)
	{
		super(x - 0.5f, y, z + 0.5f, "entities/wheat/wheat.g3db");
		visible = false;
		growTicks = growTicksLeft = Game.dayInTicks;
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		visible = false;
		island.set(voxelPos.x, voxelPos.y, voxelPos.z, Voxel.get("ACRE").getId());
		island.setMeta(voxelPos.x, voxelPos.y, voxelPos.z, MetaTags.ACRE_PLANT_GROWING);
	}
	
	@Override
	public void tick(int tick)
	{
		super.tick(tick);
		
		if (!visible)
		{
			if (StateTools.isWorkingTime()) growTicksLeft--; // only grows in
																												// sunlight, so
																												// <code>initial time *
																												// 2</code> = real time
																												// is takes
			
			if (growTicksLeft <= 0)
			{
				visible = true;
				island.setMeta(voxelPos.x, voxelPos.y, voxelPos.z, MetaTags.ACRE_PLANT_GROWN);
			}
		}
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		if (!visible) growTicksLeft = growTicks;
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
