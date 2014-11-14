package de.dakror.vloxlands.ai.path.node;

/**
 * @author Dakror
 */
public class BFSNode extends Node {
	public byte voxel;
	public byte meta;
	
	public BFSNode(float x, float y, float z, byte voxel, byte meta, Node parent) {
		super(x, y, z, parent);
		this.voxel = voxel;
		this.meta = meta;
	}
}
