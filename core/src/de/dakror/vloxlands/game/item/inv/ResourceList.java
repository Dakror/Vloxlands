/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package de.dakror.vloxlands.game.item.inv;

import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.Gdx;

import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;

/**
 * Functions as pre-requisites array as well as costs list or simple resource
 * stats
 * 
 * @author Dakror
 */
public class ResourceList {
	HashMap<Byte, Integer> items;
	
	int minBuildings;
	int maxBuildings;
	int minPopulation;
	int maxPopulation;
	
	int count;
	
	public ResourceList() {
		items = new HashMap<Byte, Integer>();
	}
	
	public ResourceList add(ItemStack stack) {
		add(stack.getItem(), stack.getAmount());
		return this;
	}
	
	public ResourceList add(Item item, int amount) {
		int o = items.containsKey(item.getId()) ? get(item) : 0;
		items.put(item.getId(), o + amount);
		count += amount;
		return this;
	}
	
	/**
	 * Doesn't check if the new amount is negative!
	 */
	public ResourceList remove(ItemStack stack) {
		remove(stack.getItem(), stack.getAmount());
		return this;
	}
	
	/**
	 * Doesn't check if the new amount is negative!
	 */
	public ResourceList remove(Item item, int amount) {
		int o = items.containsKey(item.getId()) ? get(item) : 0;
		items.put(item.getId(), o - amount);
		count -= amount;
		return this;
	}
	
	public int getCount() {
		return count;
	}
	
	public int get(Item item) {
		if (!items.containsKey(item.getId())) return 0;
		return get(item.getId());
	}
	
	public int get(byte itemId) {
		if (!items.containsKey(itemId)) return 0;
		return items.get(itemId);
	}
	
	public Set<Byte> getAll() {
		return items.keySet();
	}
	
	public int getMinBuildings() {
		return minBuildings;
	}
	
	public ResourceList setMinBuildings(int minBuildings) {
		this.minBuildings = minBuildings;
		return this;
	}
	
	public int getMaxBuildings() {
		return maxBuildings;
	}
	
	public ResourceList setMaxBuildings(int maxBuildings) {
		this.maxBuildings = maxBuildings;
		return this;
	}
	
	public int getMinPopulation() {
		return minPopulation;
	}
	
	public ResourceList setMinPopulation(int minPopulation) {
		this.minPopulation = minPopulation;
		return this;
	}
	
	public int getMaxPopulation() {
		return maxPopulation;
	}
	
	public ResourceList setMaxPopulation(int maxPopulation) {
		this.maxPopulation = maxPopulation;
		return this;
	}
	
	public boolean hasRequirements() {
		return maxBuildings > 0 || minBuildings > 0 || maxPopulation > 0 || minPopulation > 0;
	}
	
	public ResourceList set(ResourceList o) {
		maxBuildings = o.maxBuildings;
		maxPopulation = o.maxPopulation;
		minBuildings = o.minBuildings;
		minPopulation = o.minPopulation;
		
		items.clear();
		items.putAll(o.items);
		
		count = o.count;
		return this;
	}
	
	public boolean canSubtract(ResourceList o) {
		if (o.maxBuildings > maxBuildings) return false;
		if (o.maxPopulation > maxPopulation) return false;
		if (o.minBuildings > minBuildings) return false;
		if (o.minPopulation > minPopulation) return false;
		if (o.count > count) return false;
		if (o.items.size() > items.size()) return false;
		
		for (Byte b : items.keySet()) {
			if (!o.items.containsKey(b)) continue;
			if (o.get(b) > get(b)) return false;
		}
		for (Byte b : o.items.keySet()) {
			if (!items.containsKey(b)) return false;
		}
		
		return true;
	}
	
	public void print() {
		Gdx.app.log("ResourceList.print", "count         : " + count);
		Gdx.app.log("ResourceList.print", "maxBuildings  : " + maxBuildings);
		Gdx.app.log("ResourceList.print", "maxPopulation : " + maxPopulation);
		Gdx.app.log("ResourceList.print", "minBuildings  : " + minBuildings);
		Gdx.app.log("ResourceList.print", "minPopulation : " + minPopulation);
		Gdx.app.log("ResourceList.print", "items:");
		for (Byte b : items.keySet())
			Gdx.app.log("ResourceList.print", Item.getForId(b).getName() + " = " + get(b));
	}
}
