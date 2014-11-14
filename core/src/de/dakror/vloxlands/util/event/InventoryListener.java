package de.dakror.vloxlands.util.event;

import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.inv.Inventory;

/**
 * @author Dakror
 */
public interface InventoryListener {
	public void onItemAdded(int countBefore, Item item, Inventory inventory);
	
	public void onItemRemoved(int countBefore, Item item, Inventory inventory);
}
