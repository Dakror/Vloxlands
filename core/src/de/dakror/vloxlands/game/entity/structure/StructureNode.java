package de.dakror.vloxlands.game.entity.structure;

import com.badlogic.gdx.math.Vector3;

/**
 * @author Dakror
 */
public class StructureNode
{
	/**
	 * An unique name, should be name of a item
	 */
	public String name;
	public Vector3 pos;
	public NodeType type;
	
	public StructureNode(NodeType type, int x, int y, int z)
	{
		this(type, "", x, y, z);
	}
	
	public StructureNode(NodeType type, String name, int x, int y, int z)
	{
		this.type = type;
		this.name = name;
		pos = new Vector3(x, y, z);
	}
}
