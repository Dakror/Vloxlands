package de.dakror.vloxlands.game.query;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.structure.NodeType;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.item.ItemStack;

/**
 * @author Dakror
 */
public class Query
{
	public static interface Queryable
	{
		public PathBundle query(Query query);
	}
	
	// -- searched -- //
	public Class<?> searchedClass;
	/**
	 * If amount == 0, then everything gets checked against the item type
	 */
	public ItemStack searchedItemStack;
	public NodeType searchedNodeType;
	public String searchedNodeName;
	public Class<?> searchedToolType;
	
	// -- mode -- //
	public boolean searchingStructure;
	public boolean takeClosest;
	
	// -- flags -- //
	public boolean mustBeEmpty;
	public boolean mustBeFull;
	public boolean mustHaveCapacity;
	public boolean mustHaveCapacityForTransportedItemStack;
	
	// - structure flags - //
	public boolean mustWork;
	
	// - human flags - //
	public boolean mustIdle;
	
	// -- transport -- //
	public ItemStack transportedItemStack;
	public Creature sourceCreature;
	public Structure sourceStructure;
	public Vector3 pathStart;
	public int island;
	
	private Query()
	{
		closest(true);
		island = -1;
	}
	
	public Query(Creature sourceCreature)
	{
		this();
		this.sourceCreature = sourceCreature;
	}
	
	public Query(Structure sourceStructure)
	{
		this();
		this.sourceStructure = sourceStructure;
	}
	
	public Query start(Vector3 pathStart)
	{
		this.pathStart = pathStart;
		return this;
	}
	
	public Query island(int island)
	{
		this.island = island;
		return this;
	}
	
	public Query searchClass(Class<?> searchedClass)
	{
		this.searchedClass = searchedClass;
		return this;
	}
	
	public Query stack(ItemStack searchedItemStack)
	{
		this.searchedItemStack = searchedItemStack;
		return this;
	}
	
	public Query tool(Class<?> class1)
	{
		searchedToolType = class1;
		return this;
	}
	
	public Query node(NodeType searchedNodeType)
	{
		this.searchedNodeType = searchedNodeType;
		return this;
	}
	
	public Query node(String searchedNodeName)
	{
		this.searchedNodeName = searchedNodeName;
		return this;
	}
	
	public Query structure(boolean searchingStructure)
	{
		this.searchingStructure = searchingStructure;
		return this;
	}
	
	public Query closest(boolean takeClosest)
	{
		this.takeClosest = takeClosest;
		return this;
	}
	
	public Query idle(boolean mustIdle)
	{
		this.mustIdle = mustIdle;
		return this;
	}
	
	public Query work(boolean mustWork)
	{
		this.mustWork = mustWork;
		return this;
	}
	
	public Query capacity(boolean mustHaveCapacity)
	{
		this.mustHaveCapacity = mustHaveCapacity;
		return this;
	}
	
	public Query capacityForTransported(boolean mustHaveCapacityForTransportedItemStack)
	{
		this.mustHaveCapacityForTransportedItemStack = mustHaveCapacityForTransportedItemStack;
		return this;
	}
	
	public Query full(boolean mustBeFull)
	{
		this.mustBeFull = mustBeFull;
		return this;
	}
	
	public Query empty(boolean mustBeEmpty)
	{
		this.mustBeEmpty = mustBeEmpty;
		return this;
	}
	
	public Query transport(ItemStack transportedItemStack)
	{
		this.transportedItemStack = transportedItemStack;
		return this;
	}
}
