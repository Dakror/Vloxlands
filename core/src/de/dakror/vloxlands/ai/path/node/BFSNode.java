package de.dakror.vloxlands.ai.path.node;

/**
 * @author Dakror
 */
public class BFSNode extends Node
{
	public byte voxel;
	
	public BFSNode(float x, float y, float z, byte voxel, Node parent)
	{
		super(x, y, z, parent);
		this.voxel = voxel;
	}
}
