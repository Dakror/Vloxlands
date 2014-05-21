package de.dakror.vloxlands.ai.node;

/**
 * @author Dakror
 */
public class AStarNode extends Node
{
	public float F, G, H;
	
	public AStarNode(float x, float y, float z, /* int island, */float G, float H, Node parent)
	{
		super(x, y, z, parent);
		F = G + H;
		this.G = G;
		this.H = H;
	}
}
