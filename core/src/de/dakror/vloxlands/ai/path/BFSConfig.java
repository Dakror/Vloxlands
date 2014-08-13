package de.dakror.vloxlands.ai.path;

import de.dakror.vloxlands.game.entity.creature.Creature;

/**
 * @author Dakror
 */
public class BFSConfig
{
	byte voxel;
	byte meta;
	byte neighborMeta;
	
	float maxRange;
	
	int neighborRangeX, neighborRangeY, neighborRangeZ;
	
	boolean notMeta;
	boolean notNeighbor;
	boolean closest;
	
	Creature creature;
	
	public BFSConfig(Creature creature)
	{
		this.creature = creature;
		neighborRangeX = neighborRangeY = neighborRangeZ = 1;
	}
	
	public BFSConfig voxel(byte voxel)
	{
		this.voxel = voxel;
		return this;
	}
	
	public BFSConfig range(float maxRange)
	{
		this.maxRange = maxRange;
		return this;
	}
	
	public BFSConfig closest(boolean closest)
	{
		this.closest = closest;
		return this;
	}
	
	public BFSConfig meta(byte meta)
	{
		this.meta = meta;
		return this;
	}
	
	public BFSConfig notmeta(byte meta)
	{
		this.meta = meta;
		notMeta = true;
		return this;
	}
	
	public BFSConfig neighbor(byte neighborMeta)
	{
		this.neighborMeta = neighborMeta;
		return this;
	}
	
	public BFSConfig notneighbor(byte neighborMeta)
	{
		this.neighborMeta = neighborMeta;
		notNeighbor = true;
		return this;
	}
	
	public BFSConfig neighborrangeX(int neighborRangeX)
	{
		this.neighborRangeX = neighborRangeX;
		return this;
	}
	
	public BFSConfig neighborrangeY(int neighborRangeY)
	{
		this.neighborRangeY = neighborRangeY;
		return this;
	}
	
	public BFSConfig neighborrangeZ(int neighborRangeZ)
	{
		this.neighborRangeZ = neighborRangeZ;
		return this;
	}
	
	public BFSConfig neighborrange(int neighborRangeX, int neighborRangeY, int neighborRangeZ)
	{
		this.neighborRangeX = neighborRangeX;
		this.neighborRangeY = neighborRangeY;
		this.neighborRangeZ = neighborRangeZ;
		return this;
	}
}
