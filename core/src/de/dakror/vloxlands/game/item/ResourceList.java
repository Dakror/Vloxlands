package de.dakror.vloxlands.game.item;

import java.util.HashMap;


/**
 * Functions as pre-requisites array as well as costs list
 * 
 * @author Dakror
 */
public class ResourceList
{
	HashMap<Byte, Integer> items;
	
	int minBuildings;
	int maxBuildings;
	int costBuildings;
	int minPopulation;
	int maxPopulation;
	int costPopulation;
	
	int count;
	
	public ResourceList()
	{
		items = new HashMap<Byte, Integer>();
	}
	
	public ResourceList add(ItemStack stack)
	{
		add(stack.getItem(), stack.getAmount());
		return this;
	}
	
	public ResourceList add(Item item, int amount)
	{
		int o = items.containsKey(item.getId()) ? get(item) : 0;
		items.put(item.getId(), o + amount);
		count += amount;
		return this;
	}
	
	public int getCount()
	{
		return count;
	}
	
	public int get(Item item)
	{
		return get(item.getId());
	}
	
	public int get(byte itemId)
	{
		return items.get(itemId);
	}
	
	public HashMap<Byte, Integer> getAll()
	{
		return items;
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
	
	public boolean hasRequirements()
	{
		return maxBuildings > 0 || minBuildings > 0 || maxPopulation > 0 || minPopulation > 0;
	}
}
