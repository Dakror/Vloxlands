package de.dakror.vloxlands.util.event;

import de.dakror.vloxlands.game.query.VoxelPos;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.util.Direction;

/**
 * @author Dakror
 */
public class VoxelSelection
{
	public Island island;
	public VoxelPos voxelPos;
	public Direction face;
	public Voxel type;
	
	public VoxelSelection(Island island, VoxelPos voxelPos, Direction face)
	{
		this.island = island;
		this.voxelPos = voxelPos;
		type = Voxel.getForId(voxelPos.b);
		this.face = face;
	}
}
