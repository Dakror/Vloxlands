package de.dakror.vloxlands.util.interf.provider;

import de.dakror.vloxlands.game.item.inv.ResourceList;

/**
 * @author Dakror
 */
public interface ResourceListProvider {
	public ResourceList getCosts();
	
	public ResourceList getResult();
}
