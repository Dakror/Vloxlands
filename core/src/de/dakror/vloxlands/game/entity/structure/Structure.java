package de.dakror.vloxlands.game.entity.structure;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.structure.StructureNode.NodeType;
import de.dakror.vloxlands.game.item.Inventory;

/**
 * @author Dakror
 */
public abstract class Structure extends Entity
{
	Array<StructureNode> nodes;
	Vector3 voxelPos;
	Inventory inventory;
	boolean working;
	
	final Vector3 tmp = new Vector3();
	
	public Structure(float x, float y, float z, String model)
	{
		super(Math.round(x), Math.round(y), Math.round(z), model);
		voxelPos = new Vector3(x, y, z);
		nodes = new Array<StructureNode>();
		
		int width = (int) Math.ceil(boundingBox.getDimensions().x);
		int depth = (int) Math.ceil(boundingBox.getDimensions().z);
		
		nodes.add(new StructureNode(NodeType.target, -1, 0, Math.round(depth / 2)));
		nodes.add(new StructureNode(NodeType.target, width + 1, 0, Math.round(depth / 2)));
		nodes.add(new StructureNode(NodeType.target, Math.round(width / 2), 0, -1));
		nodes.add(new StructureNode(NodeType.target, Math.round(width / 2), 0, depth + 1));
		
		inventory = new Inventory();
		working = true;
	}
	
	public Vector3 getVoxelPos()
	{
		return voxelPos;
	}
	
	public Vector3 getCenter()
	{
		return voxelPos.cpy().add(boundingBox.getDimensions().cpy().scl(0.5f));
	}
	
	/**
	 * @param from expected to be in world space
	 */
	public StructureNode getStructureNode(Vector3 from, NodeType type, String name)
	{
		if (name != null)
		{
			for (StructureNode sn : nodes)
				if (sn.name.equals(name)) return sn;
		}
		
		StructureNode node = null;
		float distance = 0;
		
		for (StructureNode sn : nodes)
		{
			if (type != null && sn.type != type) continue;
			if (from == null) return sn;
			
			tmp.set(posCache).add(sn.pos);
			if (node == null || from.dst(tmp) < distance)
			{
				node = sn;
				distance = from.dst(tmp);
			}
		}
		
		return node;
	}
	
	public boolean hasStructureNode(NodeType type)
	{
		for (StructureNode sn : nodes)
			if (sn.type == type) return true;
		
		return false;
	}
	
	public boolean hasStructureNode(String name)
	{
		for (StructureNode sn : nodes)
			if (sn.name.equals(name)) return true;
		
		return false;
	}
	
	/**
	 * @param from expected to be in world space
	 */
	public StructureNode getStructureNode(Vector3 from, NodeType type)
	{
		return getStructureNode(from, type, null);
	}
	
	public Inventory getInventory()
	{
		return inventory;
	}
	
	public boolean isWorking()
	{
		return working;
	}
	
	public void setWorking(boolean working)
	{
		this.working = working;
	}
}
