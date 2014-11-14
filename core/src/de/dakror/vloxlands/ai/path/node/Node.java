package de.dakror.vloxlands.ai.path.node;

/**
 * @author Dakror
 */
public class Node {
	public int x, y, z;
	public Node parent;
	
	public Node(float x, float y, float z, Node parent) {
		this.x = (int) x;
		this.y = (int) y;
		this.z = (int) z;
		this.parent = parent;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().equals(getClass())) return false;
		return x == ((Node) obj).x && y == ((Node) obj).y && z == ((Node) obj).z;
	}
}
