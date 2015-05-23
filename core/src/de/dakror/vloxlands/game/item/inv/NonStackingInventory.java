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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.util.math.Bits;

/**
 * @author Dakror
 */
public class NonStackingInventory extends Inventory {
	int[] storage;
	
	public NonStackingInventory(int capacity) {
		super(capacity);
		storage = new int[Item.ITEMS];
	}
	
	public NonStackingInventory() {
		this(10);
	}
	
	@Override
	public void clear() {
		dispatchItemRemoved(count, null);
		Arrays.fill(storage, 0);
		count = 0;
	}
	
	@Override
	protected void addStack(ItemStack stack, int amount) {
		int oldCount = count;
		storage[stack.getItem().getId() + 128] += amount;
		count += amount;
		dispatchItemAdded(oldCount, stack.getItem());
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(storage);
	}
	
	@Override
	public int get(Item item) {
		return storage[item.getId() + 128];
	}
	
	@Override
	public int get(byte id) {
		return storage[id + 128];
	}
	
	@Override
	public ItemStack getFirst() {
		if (count == 0) return new ItemStack();
		
		for (int i = 0; i < storage.length; i++)
			if (storage[i] > 0) return new ItemStack(Item.getForId(i), storage[i]);
		
		return null;
	}
	
	@Override
	public boolean contains(ItemStack stack) {
		return get(stack.getItem()) >= stack.getAmount();
	}
	
	@Override
	public boolean contains(Class<?> class1) {
		for (Item i : Item.getAll())
			if (i.getClass().equals(class1) && get(i) > 0) return true;
		
		return false;
	}
	
	@Override
	public Item getAnyItemForToolType(Class<?> class1) {
		for (Item i : Item.getAll())
			if (i.getClass().equals(class1) && get(i) > 0) return i;
		
		return null;
	}
	
	@Override
	public ItemStack take(Item item, int amount) {
		if (amount == 0) return null;
		int oldCount = count;
		int am = Math.min(amount, storage[item.getId() + 128]);
		count -= am;
		storage[item.getId() + 128] -= am;
		
		dispatchItemRemoved(oldCount, item);
		return new ItemStack(item, am);
	}
	
	@Override
	public void save(ByteArrayOutputStream baos) throws IOException {
		Bits.putInt(baos, capacity);
		Bits.putInt(baos, count);
		Bits.putInt(baos, storage.length * 4 /* byte size of storage */);
		
		for (int i = 0; i < storage.length; i++)
			Bits.putInt(baos, storage[i]);
	}
}
