package de.dakror.vloxlands.game.entity.structure;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.EntityItem;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.StructureNode.NodeType;
import de.dakror.vloxlands.game.item.Inventory;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.job.DismantleJob;
import de.dakror.vloxlands.game.query.PathBundle;
import de.dakror.vloxlands.game.query.Query;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.layer.GameLayer;
import de.dakror.vloxlands.util.CurserCommand;
import de.dakror.vloxlands.util.IInventory;
import de.dakror.vloxlands.util.Savable;
import de.dakror.vloxlands.util.event.IEvent;

/**
 * @author Dakror
 */
public abstract class Structure extends Entity implements IInventory, Savable
{
	Array<StructureNode> nodes;
	Vector3 voxelPos;
	Inventory inventory;
	boolean working;
	
	boolean dismantleRequested;
	boolean confirmDismante;
	
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
	 * @param from
	 *          expected to be in world space
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
	 * @param from
	 *          expected to be in world space
	 */
	public StructureNode getStructureNode(Vector3 from, NodeType type)
	{
		return getStructureNode(from, type, null);
	}
	
	public Array<StructureNode> getStructureNodes()
	{
		return nodes;
	}
	
	@Override
	public Inventory getInventory()
	{
		return inventory;
	}
	
	public boolean isWorking()
	{
		return working;
	}
	
	public boolean requestDismantle()
	{
		if (dismantleRequested) return false;
		PathBundle pb = GameLayer.world.query(new Query(this).searchClass(Human.class).idle(true).empty(true).node(NodeType.target).island(0));
		if (pb == null || pb.creature == null) return false;
		
		((Human) pb.creature).queueJob(pb.path, new DismantleJob((Human) pb.creature, this, false));
		dismantleRequested = true;
		return true;
	}
	
	public boolean isConfirmDismantle()
	{
		return confirmDismante;
	}
	
	public void setWorking(boolean working)
	{
		this.working = working;
	}
	
	public void handleEvent(IEvent e)
	{
		if (e.getName().equals("onDismantle"))
		{
			kill();
			Vector3 p = GameLayer.world.getIslands()[0].pos;
			EntityItem i = new EntityItem(Island.SIZE / 2 - 5, Island.SIZE / 4 * 3 + p.y + 1, Island.SIZE / 2, Item.get("YELLOW_CRYSTAL"), 1);
			GameLayer.world.addEntity(i);
			
		}
	}
	
	public CurserCommand getDefaultCommand()
	{
		return CurserCommand.NO_OP;
	}
	
	public CurserCommand getCommandForEntity(Entity selectedEntity)
	{
		return getDefaultCommand();
	}
	
	public CurserCommand getCommandForStructure(Structure selectedStructure)
	{
		return getDefaultCommand();
	}
}
