package de.dakror.vloxlands.game.item;


/**
 * Functions as pre-requisites array as well as costs list
 * 
 * @author Dakror
 */
public class ResourceList
{
	int[] items;
	
	int minBuildings;
	int maxBuildings;
	int costBuildings;
	int minPopulation;
	int maxPopulation;
	int costPopulation;
	
	public ResourceList()
	{
		items = new int[Item.ITEMS];
	}
	
	public ResourceList add(ItemStack stack)
	{
		add(stack.getItem(), stack.getAmount());
		return this;
	}
	
	public ResourceList add(Item item, int amount)
	{
		items[item.getId() + 128] = amount;
		return this;
	}
	
	public int get(Item item)
	{
		return get(item.getId());
	}
	
	public int get(byte itemId)
	{
		return items[itemId + 128];
	}
	
	public int getMinBuildings()
	{
		return minBuildings;
	}
	
	public ResourceList setMinBuildings(int minBuildings)
	{
		this.minBuildings = minBuildings;
		return this;
	}
	
	public int getMaxBuildings()
	{
		return maxBuildings;
	}
	
	public ResourceList setMaxBuildings(int maxBuildings)
	{
		this.maxBuildings = maxBuildings;
		return this;
	}
	
	public int getCostBuildings()
	{
		return costBuildings;
	}
	
	public ResourceList setCostBuildings(int costBuildings)
	{
		this.costBuildings = costBuildings;
		return this;
	}
	
	public int getMinPopulation()
	{
		return minPopulation;
	}
	
	public ResourceList setMinPopulation(int minPopulation)
	{
		this.minPopulation = minPopulation;
		return this;
	}
	
	public int getMaxPopulation()
	{
		return maxPopulation;
	}
	
	public ResourceList setMaxPopulation(int maxPopulation)
	{
		this.maxPopulation = maxPopulation;
		return this;
	}
	
	public int getCostPopulation()
	{
		return costPopulation;
	}
	
	public ResourceList setCostPopulation(int costPopulation)
	{
		this.costPopulation = costPopulation;
		return this;
	}
}
