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


package de.dakror.vloxlands.game.item;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.util.event.ItemStackListener;
import de.dakror.vloxlands.util.interf.Savable;

/**
 * @author Dakror
 */
public class ItemStack implements Savable {
	Item item;
	int amount;
	
	public Array<ItemStackListener> listeners = new Array<ItemStackListener>();
	
	public ItemStack() {
		this(Item.get("NOTHING"), 0);
	}
	
	public ItemStack(Item item, int amount) {
		this.item = item;
		this.amount = amount;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public int setAmount(int amount) {
		this.amount = amount;
		if (amount > item.getStack()) {
			this.amount = item.getStack();
			
			dispatchStackChanged();
			return amount - item.getStack();
		}
		if (amount < 1) this.amount = 1;
		
		dispatchStackChanged();
		return 0;
	}
	
	public int add(int amount) {
		return setAmount(this.amount + amount);
	}
	
	public int sub(int amount) {
		return setAmount(this.amount - amount);
	}
	
	public boolean isFull() {
		return amount == item.getStack();
	}
	
	public void setItem(Item item) {
		this.item = item;
	}
	
	public Item getItem() {
		return item;
	}
	
	public boolean isNull() {
		return amount == 0;
	}
	
	public void set(ItemStack o) {
		amount = o.amount;
		item = o.item;
		listeners.addAll(o.listeners);
		dispatchStackChanged();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ItemStack)) return false;
		return item.getId() == ((ItemStack) obj).getItem().getId() && amount == ((ItemStack) obj).getAmount();
	}
	
	public boolean canAdd(ItemStack stack) {
		if (stack.getItem().getId() != item.getId()) return false;
		return amount + stack.getAmount() <= item.getStack();
	}
	
	public boolean canAddWithOverflow(ItemStack stack) {
		if (stack.getItem().getId() != item.getId()) return false;
		return !isFull();
	}
	
	@Override
	public String toString() {
		if (isNull()) return "Null";
		return amount + "x " + item.getName();
	}
	
	private void dispatchStackChanged() {
		for (ItemStackListener isl : listeners)
			isl.onStackChanged();
	}
	
	public void addListener(ItemStackListener listener) {
		listeners.insert(0, listener);
	}
	
	public void removeListener(ItemStackListener listener) {
		listeners.removeValue(listener, true);
	}
	
	@Override
	public void save(ByteArrayOutputStream baos) throws IOException {
		baos.write(item.getId());
		baos.write(amount);
	}
}
