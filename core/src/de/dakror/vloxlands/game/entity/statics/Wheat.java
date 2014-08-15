package de.dakror.vloxlands.game.entity.statics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.ai.state.StateTools;
import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.util.math.Bits;

/**
 * @author Dakror
 */
public class Wheat extends Entity
{
	int growTicksLeft;
	Vector3 voxelPos;
	
	public Wheat(float x, float y, float z)
	{
		super(x, y + 0.62f, z + 0.02f, "models/entities/wheat/wheat.g3db");
		visible = false;
		voxelPos = new Vector3(x, y, z);
		growTicksLeft = Game.dayInTicks;
	}
	
	@Override
	public void tick(int tick)
	{
		super.tick(tick);
		
		if (!visible)
		{
			if (StateTools.isWorkingTime()) growTicksLeft--; // only grows in sunlight, so <code>initial time * 2</code> = real time is takes
			
			if (growTicksLeft <= 0)
			{
				visible = true;
			}
		}
	}
	
	@Override
	public void save(ByteArrayOutputStream baos) throws IOException
	{
		super.save(baos);
		
		Bits.putInt(baos, growTicksLeft);
		Bits.putVector3(baos, voxelPos);
	}
}
