package de.dakror.vloxlands.util.event;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.util.Direction;

/**
 * @author Dakror
 */
public class VoxelSelection
{
	public int island;
	public Voxel type;
	public Vector3 voxel;
	public Direction face;
	
	public VoxelSelection(int island, Voxel type, Vector3 voxel, Direction face)
	{
		this.island = island;
		this.type = type;
		this.voxel = voxel;
		this.face = face;
	}
}