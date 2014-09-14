package de.dakror.vloxlands.game.entity.structure;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.inv.NonStackingInventory;
import de.dakror.vloxlands.ui.NonStackingInventoryListItem;
import de.dakror.vloxlands.ui.PinnableWindow;
import de.dakror.vloxlands.util.CurserCommand;

/**
 * @author Dakror
 */
public class Warehouse extends Structure
{
	public Warehouse(float x, float y, float z)
	{
		this(x, y, z, "structure/house.vxi");
	}
	
	protected Warehouse(float x, float y, float z, String model)
	{
		super(x, y, z, model);
		
		nodes.add(new StructureNode(NodeType.deposit, 0, 0, 2));
		nodes.add(new StructureNode(NodeType.pickup, 0, 0, 2));
		name = "Warehouse";
		
		weight = 1000f;
		
		inventory = new NonStackingInventory(100);
	}
	
	@Override
	public CurserCommand getCommandForEntity(Entity selectedEntity)
	{
		if (selectedEntity instanceof Human && !((Human) selectedEntity).getCarryingItemStack().isNull()) return CurserCommand.DEPOSIT;
		return super.getCommandForEntity(selectedEntity);
	}
	
	@Override
	protected void setupUI(final PinnableWindow window, Object... params)
	{
		final VerticalGroup items = new VerticalGroup();
		items.left();
		items.addAction(new Action()
		{
			int hashCode = 0;
			
			@Override
			public boolean act(float delta)
			{
				int hc = getInventory().hashCode();
				if (hc != hashCode)
				{
					hashCode = hc;
					
					for (int i = 0; i < Item.ITEMS; i++)
					{
						Item item = Item.getForId(i);
						if (item == null) continue;
						
						Actor a = items.findActor(i + "");
						if (a != null) ((NonStackingInventoryListItem) a).setAmount(getInventory().get(item));
						else items.addActor(new NonStackingInventoryListItem(window.getStage(), item, getInventory().get(item)));
					}
				}
				return false;
			}
		});
		window.row().pad(0).width(400);
		final ScrollPane itemsWrap = new ScrollPane(items, Vloxlands.skin);
		itemsWrap.setScrollbarsOnTop(false);
		itemsWrap.setFadeScrollBars(false);
		itemsWrap.setScrollingDisabled(true, false);
		itemsWrap.getStyle().background.setLeftWidth(10);
		itemsWrap.getStyle().background.setRightWidth(10);
		itemsWrap.getStyle().background.setBottomHeight(10);
		itemsWrap.getStyle().background.setTopHeight(10);
		window.add(itemsWrap).left().maxHeight(100).minHeight(100).width(220).padRight(10).padRight(0);
	}
}
