package de.dakror.vloxlands.game.voxel;

import com.badlogic.gdx.math.Vector2;

import de.dakror.vloxlands.util.Direction;

public class VoxelDouble extends Voxel
{
	@Override
	protected Vector2 getTexCoord(int x, int y, int z, Direction d)
	{
		Vector2 v = super.getTexCoord(x, y, z, d);
		if (d != Direction.UP && d != Direction.DOWN) v.x++;
		return v;
	}
}
