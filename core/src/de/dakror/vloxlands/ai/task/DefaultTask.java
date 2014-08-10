package de.dakror.vloxlands.ai.task;

import com.badlogic.gdx.math.Vector2;

import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.item.inv.ResourceList;

/**
 * @author Dakror
 */
public class DefaultTask extends Task
{
	public DefaultTask(String name, String title, String description, Vector2 icon, int duration, ResourceList costs, ResourceList result)
	{
		super(name, title, description, icon, duration, costs, result);
	}
	
	@Override
	public void exit()
	{
		for (Byte b : result.getAll())
		{
			origin.getInventory().add(new ItemStack(Item.getForId(b), costs.get(b)));
		}
	}
}
