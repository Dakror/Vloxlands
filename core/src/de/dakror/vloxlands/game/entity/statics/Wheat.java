package de.dakror.vloxlands.game.entity.statics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.dakror.vloxlands.ai.state.StateTools;
import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.game.entity.StaticEntity;
import de.dakror.vloxlands.util.math.Bits;

/**
 * @author Dakror
 */
public class Wheat extends StaticEntity
{
	int growTicksLeft;
	boolean managed;
	
	public Wheat(float x, float y, float z)
	{
		super(x, y, z, x, y + 0.62f, z + 0.02f, "models/entities/wheat/wheat.g3db");
		visible = false;
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
