package de.dakror.vloxlands.game.voxel;

import com.badlogic.gdx.math.Vector2;

import de.dakror.vloxlands.util.Direction;

public class VoxelGrass extends Voxel {
	@Override
	protected Vector2 getTexCoord(int x, int y, int z, Direction d) {
		Vector2 v = super.getTexCoord(x, y, z, d);
		if (d == Direction.UP) return v;
		if (d == Direction.DOWN) return Voxel.get("DIRT").getTexCoord(x, y, z, d);
		v.x++;
		return v;
	}
}
