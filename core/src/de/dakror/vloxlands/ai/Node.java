package de.dakror.vloxlands.ai;

/**
 * @author Dakror
 */
public class Node
{
	public int x, y, z;// , island;
	public float F, G, H;
	public Node parent;
	
	public Node(float x, float y, float z, /* int island, */float G, float H, Node parent)
	{
		this.x = (int) x;
		this.y = (int) y;
		this.z = (int) z;
		// this.island = island;
		F = G + H;
		this.G = G;
		this.H = H;
		this.parent = parent;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Node)) return false;
		return x == ((Node) obj).x && y == ((Node) obj).y && z == ((Node) obj).z;
	}
}
