package de.dakror.vloxlands.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.tablelayout.Cell;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.entity.structure.Warehouse;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.job.IdleJob;
import de.dakror.vloxlands.game.job.Job;
import de.dakror.vloxlands.ui.ItemSlot;
import de.dakror.vloxlands.ui.NonStackingInventoryListItem;
import de.dakror.vloxlands.ui.PinnableWindow;
import de.dakror.vloxlands.ui.TooltipImageButton;
import de.dakror.vloxlands.util.event.SelectionListener;
import de.dakror.vloxlands.util.event.VoxelSelection;

/**
 * @author Dakror
 */
public class HudLayer extends Layer implements SelectionListener
{
	PinnableWindow selectedEntityWindow;
	PinnableWindow selectedStructureWindow;
	
	@Override
	public void show()
	{
		modal = true;
		GameLayer.instance.addListener(this);
		
		stage = new Stage(new ScreenViewport());
		
		selectedEntityWindow = new PinnableWindow("", Vloxlands.skin);
		selectedEntityWindow.setPosition(Gdx.graphics.getWidth() - selectedEntityWindow.getWidth(), 0);
		selectedEntityWindow.setTitleAlignment(Align.left);
		selectedEntityWindow.setVisible(false);
		stage.addActor(selectedEntityWindow);
		
		selectedStructureWindow = new PinnableWindow("", Vloxlands.skin);
		selectedStructureWindow.setPosition(Gdx.graphics.getWidth() - selectedStructureWindow.getWidth(), 0);
		selectedStructureWindow.setTitleAlignment(Align.left);
		selectedStructureWindow.setVisible(false);
		stage.addActor(selectedStructureWindow);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		GameLayer.instance.removeListener(this);
	}
	
	@Override
	public void onCreatureSelection(final Creature creature, boolean lmb)
	{
		if (lmb && selectedStructureWindow.setShown(false))
		{
			selectedStructureWindow.clearChildren();
			selectedStructureWindow.clearActions();
		}
		
		if (lmb)
		{
			selectedEntityWindow.setTitle(creature.getName());
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
			selectedEntityWindow.addActor(selectedEntityWindow.getButtonTable());
			
			if (creature instanceof Human)
			{
				selectedEntityWindow.row().pad(0).colspan(4).width(220);
				final List<Job> jobs = new List<Job>(Vloxlands.skin);
				jobs.setItems(new IdleJob((Human) creature));
				jobs.addAction(new Action()
				{
					@Override
					public boolean act(float delta)
					{
						if (((Human) creature).getJobQueue().size == 0 && jobs.getItems().get(0) instanceof IdleJob) return false;
						
						if (!((Human) creature).getJobQueue().equals(jobs.getItems()))
						{
							if (((Human) creature).getJobQueue().size > 0) jobs.setItems(((Human) creature).getJobQueue());
							else jobs.setItems(new IdleJob((Human) creature));
							
							jobs.getSelection().setDisabled(true);
							jobs.setSelectedIndex(-1);
							selectedEntityWindow.pack();
						}
						
						return false;
					}
				});
				jobs.getSelection().setDisabled(true);
				jobs.setSelectedIndex(-1);
				final ScrollPane jobsWrap = new ScrollPane(jobs, Vloxlands.skin);
				jobsWrap.setVisible(false);
				jobsWrap.setScrollbarsOnTop(false);
				jobsWrap.setFadeScrollBars(false);
				final Cell<?> cell = selectedEntityWindow.add(jobsWrap).height(100).ignore();
				
				selectedEntityWindow.row();
				selectedEntityWindow.left().add(new ItemSlot(stage, ((Human) creature).getTool()));
				ItemSlot slot = new ItemSlot(stage, ((Human) creature).getCarryingItemStack());
				selectedEntityWindow.add(slot);
				selectedEntityWindow.add(new ItemSlot(stage, new ItemStack())); // armor / jetpack
				
				ImageButtonStyle style = new ImageButtonStyle(Vloxlands.skin.get(ButtonStyle.class));
				style.imageUp = Vloxlands.skin.getDrawable("gears");
				style.imageUp.setMinWidth(ItemSlot.size);
				style.imageUp.setMinHeight(ItemSlot.size);
				style.imageDown = Vloxlands.skin.getDrawable("gears");
				style.imageDown.setMinWidth(ItemSlot.size);
				style.imageDown.setMinHeight(ItemSlot.size);
				final TooltipImageButton job = new TooltipImageButton(stage, style);
				job.getStyle().checked = Vloxlands.skin.getDrawable("default-round-down");
				job.addListener(new ClickListener()
				{
					@Override
					public void clicked(InputEvent event, float x, float y)
					{
						cell.ignore(!cell.getIgnore());
						jobsWrap.setVisible(!jobsWrap.isVisible());
						selectedEntityWindow.invalidateHierarchy();
						selectedEntityWindow.pack();
					}
				});
				job.pad(4);
				job.getTooltip().set("Job Queue", "Toggle Job Queue display");
				selectedEntityWindow.add(job);
			}
			
			selectedEntityWindow.pack();
			selectedEntityWindow.setVisible(true);
			selectedEntityWindow.toFront();
		}
	}
	
	@Override
	public void onVoxelSelection(VoxelSelection vs, boolean lmb)
	{
		if (lmb && selectedEntityWindow.setShown(false))
		{
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
		}
		if (lmb && selectedStructureWindow.setShown(false))
		{
			selectedStructureWindow.clearChildren();
			selectedStructureWindow.clearActions();
		}
	}
	
	@Override
	public void onStructureSelection(final Structure structure, boolean lmb)
	{
		if (lmb && selectedEntityWindow.setShown(false))
		{
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
		}
		
		if (lmb)
		{
			selectedStructureWindow.setTitle(structure.getName());
			selectedStructureWindow.clearChildren();
			selectedStructureWindow.clearActions();
			selectedStructureWindow.addActor(selectedStructureWindow.getButtonTable());
			
			if (structure instanceof Warehouse)
			{
				final VerticalGroup items = new VerticalGroup();
				items.left();
				items.addAction(new Action()
				{
					int hashCode = 0;
					
					@Override
					public boolean act(float delta)
					{
						int hc = structure.getInventory().hashCode();
						if (hc != hashCode)
						{
							hashCode = hc;
							
							for (int i = 0; i < Item.ITEMS; i++)
							{
								Item item = Item.getForId(i);
								if (item == null) continue;
								
								Actor a = items.findActor(i + "");
								if (a != null) ((NonStackingInventoryListItem) a).setAmount(structure.getInventory().get(item));
								else items.addActor(new NonStackingInventoryListItem(stage, item, structure.getInventory().get(item)));
							}
						}
						return false;
					}
				});
				
				selectedStructureWindow.row().pad(0).colspan(4).width(400);
				final ScrollPane itemsWrap = new ScrollPane(items, Vloxlands.skin);
				itemsWrap.setScrollbarsOnTop(false);
				itemsWrap.setFadeScrollBars(false);
				selectedStructureWindow.left().add(itemsWrap).maxHeight(100).minHeight(100).width(200);
				
				final Label capacity = new Label("Capacity: 0 / 10 Items", Vloxlands.skin);
				capacity.setAlignment(Align.center, Align.center);
				capacity.addAction(new Action()
				{
					@Override
					public boolean act(float delta)
					{
						capacity.setText("Capacity: " + structure.getInventory().getCount() + " / " + structure.getInventory().getCapacity() + " Items");
						
						float percent = structure.getInventory().getCount() / (float) structure.getInventory().getCapacity();
						
						if (percent >= 0.8f) capacity.setColor(1, 0.5f, 0, 1);
						else if (percent >= 0.5f) capacity.setColor(1, 1, 0, 1);
						else if (percent == 1) capacity.setColor(1, 0, 0, 1);
						else capacity.setColor(1, 1, 1, 1);
						
						return false;
					}
				});
				selectedStructureWindow.add(capacity).top().width(200);
			}
			
			selectedStructureWindow.pack();
			selectedStructureWindow.setVisible(true);
			selectedStructureWindow.toFront();
		}
	}
	
	@Override
	public void render(float delta)
	{
		stage.act();
		if (Vloxlands.currentGame.getActiveLayer() == this || !Vloxlands.currentGame.getActiveLayer().isModal()) stage.draw();
	}
}
