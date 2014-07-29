package de.dakror.vloxlands.game.entity.structure;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.ai.AStar;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.EntityItem;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.StructureNode.NodeType;
import de.dakror.vloxlands.game.item.Inventory;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ResourceList;
import de.dakror.vloxlands.game.job.DismantleJob;
import de.dakror.vloxlands.game.job.Job;
import de.dakror.vloxlands.game.query.PathBundle;
import de.dakror.vloxlands.game.query.Query;
import de.dakror.vloxlands.game.voxel.Voxel;
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
	Array<Human> workers;
	Vector3 voxelPos;
	Inventory inventory;
	ResourceList resourceList;
	boolean working;
	
	boolean dismantleRequested;
	boolean confirmDismante;
	boolean built;
	
	int buildProgress;
	
	final Vector3 tmp = new Vector3();
	final Vector3 dim = new Vector3();
	
	public Structure(float x, float y, float z, String model)
	{
		super(Math.round(x), Math.round(y), Math.round(z), model);
		voxelPos = new Vector3(x, y, z);
		
		nodes = new Array<StructureNode>();
		workers = new Array<Human>();
		
		int width = (int) Math.ceil(boundingBox.getDimensions().x);
		int depth = (int) Math.ceil(boundingBox.getDimensions().z);
		
		nodes.add(new StructureNode(NodeType.target, -1, 0, Math.round(depth / 2)));
		nodes.add(new StructureNode(NodeType.target, width, 0, Math.round(depth / 2)));
		nodes.add(new StructureNode(NodeType.target, Math.round(width / 2), 0, -1));
		nodes.add(new StructureNode(NodeType.target, Math.round(width / 2), 0, depth));
		
		nodes.add(new StructureNode(NodeType.build, 0, 0, Math.round(depth / 2)));
		nodes.add(new StructureNode(NodeType.build, width - 1, 0, Math.round(depth / 2)));
		nodes.add(new StructureNode(NodeType.build, Math.round(width / 2), 0, 0));
		nodes.add(new StructureNode(NodeType.build, Math.round(width / 2), 0, depth - 1));
		
		inventory = new Inventory();
		resourceList = new ResourceList();
		working = true;
		
		dim.set((float) Math.ceil(boundingBox.getDimensions().x), (float) Math.ceil(boundingBox.getDimensions().y), (float) Math.ceil(boundingBox.getDimensions().z));
		
		setBuilt(false);
	}
	
	public Vector3 getVoxelPos()
	{
		return voxelPos;
	}
	
	public boolean isBuilt()
	{
		return built;
	}
	
	public void setBuilt(boolean built)
	{
		this.built = built;
		modelVisible = built;
		additionalVisible = built;
	}
	
	public Vector3 getCenter()
	{
		return voxelPos.cpy().add(boundingBox.getDimensions().cpy().scl(0.5f));
	}
	
	public void updateVoxelPos()
	{
		transform.getTranslation(posCache);
		transform.getRotation(rotCache);
		Vector3 p = posCache.cpy().sub(island.pos).sub(boundingBox.getDimensions().cpy().scl(0.5f));
		voxelPos = new Vector3(Math.round(p.x), Math.round(p.y), Math.round(p.z));
	}
	
	public boolean canBePlaced()
	{
		int width = (int) Math.ceil(boundingBox.getDimensions().x);
		int height = (int) Math.ceil(boundingBox.getDimensions().y);
		int depth = (int) Math.ceil(boundingBox.getDimensions().z);
		
		for (int i = 0; i < width; i++)
			for (int j = -1; j < height; j++)
				for (int k = 0; k < depth; k++)
				{
					if (j == -1 && island.get(i + voxelPos.x, j + voxelPos.y + 1, k + voxelPos.z) == 0) return false;
					else if (j > -1 && island.get(i + voxelPos.x, j + voxelPos.y + 1, k + voxelPos.z) != 0) return false;
				}
		
		for (Entity s : island.getEntities())
			if (s instanceof Structure && intersects((Structure) s)) return false;
		
		return true;
	}
	
	/**
	 * @return true if building is done
	 */
	public boolean progressBuild()
	{
		if (buildProgress == resourceList.getCount())
		{
			if (!built) setBuilt(true);
			return true;
		}
		buildProgress++;
		
		if (!built && buildProgress == resourceList.getCount()) setBuilt(true);
		return buildProgress == resourceList.getCount();
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		if (!built)
		{
			int width = (int) Math.ceil(boundingBox.getDimensions().x);
			int depth = (int) Math.ceil(boundingBox.getDimensions().z);
			
			byte gr = Voxel.get("GRAVEL").getId();
			
			for (int i = 0; i < width; i++)
				for (int j = 0; j < depth; j++)
					island.set(i + voxelPos.x, voxelPos.y, j + voxelPos.z, gr);
		}
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
	
	public Array<StructureNode> getStructureNodes()
	{
		return nodes;
	}
	
	@Override
	public Inventory getInventory()
	{
		return inventory;
	}
	
	public ResourceList getResourceList()
	{
		return resourceList;
	}
	
	public boolean isWorking()
	{
		return working;
	}
	
	public boolean requestDismantle()
	{
		if (dismantleRequested) return false;
		PathBundle pb = GameLayer.world.query(new Query(this).searchClass(Human.class).idle(true).empty(true).node(NodeType.build).island(0));
		if (pb == null || pb.creature == null) return false;
		
		Human human = (Human) pb.creature;
		Job job = new DismantleJob((Human) pb.creature, this, false);
		Vector3 pathStart = human.getVoxelBelow();
		
		human.equipCorrectToolForJob(job, false, pathStart);
		
		((Human) pb.creature).queueJob(AStar.findPath(pathStart, pb.path.getGhostTarget() != null ? pb.path.getGhostTarget() : pb.path.getLast(), human, NodeType.build.useGhostTarget), job);
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
			Vector3 p = GameLayer.instance.activeIsland.pos;
			EntityItem i = new EntityItem(Island.SIZE / 2 - 5, Island.SIZE / 4 * 3 + p.y + 1, Island.SIZE / 2, Item.get("YELLOW_CRYSTAL"), 1);
			island.addEntity(i, false, false);
		}
	}
	
	public boolean intersects(Structure o)
	{
		float lx = Math.abs(posCache.x - o.posCache.x);
		float sumx = (dim.x / 2.0f) + (o.dim.x / 2.0f);
		
		float ly = Math.abs(posCache.y - o.posCache.y);
		float sumy = (dim.y / 2.0f) + (o.dim.y / 2.0f);
		
		float lz = Math.abs(posCache.z - o.posCache.z);
		float sumz = (dim.z / 2.0f) + (o.dim.z / 2.0f);
		
		return (lx <= sumx && ly <= sumy && lz <= sumz);
	}
	
	public CurserCommand getDefaultCommand()
	{
		return CurserCommand.WALK;
	}
	
	public CurserCommand getCommandForEntity(Entity selectedEntity)
	{
		if (selectedEntity instanceof Human && !built) return CurserCommand.BUILD;
		return getDefaultCommand();
	}
	
	public CurserCommand getCommandForStructure(Structure selectedStructure)
	{
		return getDefaultCommand();
	}
}
