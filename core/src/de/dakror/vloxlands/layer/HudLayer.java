package de.dakror.vloxlands.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.entity.structure.Warehouse;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.job.IdleJob;
import de.dakror.vloxlands.game.job.Job;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.ui.IslandInfo;
import de.dakror.vloxlands.ui.ItemSlot;
import de.dakror.vloxlands.ui.Minimap;
import de.dakror.vloxlands.ui.NonStackingInventoryListItem;
import de.dakror.vloxlands.ui.PinnableWindow;
import de.dakror.vloxlands.ui.Revolver;
import de.dakror.vloxlands.ui.RevolverSlot;
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
	
	ShapeRenderer shapeRenderer;
	
	int buttonDown = -1;
	
	final Vector2 dragStart = new Vector2(-1, -1);
	final Vector2 dragEnd = new Vector2(-1, -1);
	
	@Override
	public void show()
	{
		modal = true;
		GameLayer.instance.addListener(this);
		
		stage = new Stage(new ScreenViewport());
		
		shapeRenderer = new ShapeRenderer();
		
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
		
		addActionsMenu();
		
		stage.addActor(new Minimap());
		stage.addActor(new IslandInfo());
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		GameLayer.instance.removeListener(this);
	}
	
	public void addActionsMenu()
	{
		Revolver actions = new Revolver();
		actions.setPosition(10, 10);
		
		RevolverSlot s = new RevolverSlot(stage, new Vector2(3, 0), "Mine");
		s.getTooltip().set("Mine", "Mine or dig terrain.");
		actions.addSlot(0, null, s);
		s = new RevolverSlot(stage, new Vector2(3, 0), "clear|region");
		s.getTooltip().set("Clear", "Clear a selected region of natural materials.");
		actions.addSlot(1, "Mine", s);
		
		// s = new RevolverSlot(stage, new Vector2(0, 3), "voxel:11");
		// s.getTooltip().set("Wood", "Chop trees for wooden logs using an axe.");
		// s.setDisabled(true);
		// actions.addSlot(1, "Mine", s);
		// s = new RevolverSlot(stage, new Vector2(4, 4), "voxel:1|14");
		// s.getTooltip().set("(Sand-)Stone", "Mine rocks for stone, respectively sandstone in deserts, using a pickaxe.");
		// actions.addSlot(1, "Mine", s);
		//
		// s = new RevolverSlot(stage, new Vector2(0, 2), "Crystal");
		// s.getTooltip().set("Crystals", "Mine the different kinds of crystals.");
		// actions.addSlot(1, "Mine", s);
		// s = new RevolverSlot(stage, new Vector2(3, 2), "voxel:3");
		// s.getTooltip().set("Yellow Crystal", "Smash yellow crystals using a pickaxe.");
		// actions.addSlot(2, "Crystal", s);
		// s = new RevolverSlot(stage, new Vector2(2, 2), "voxel:4");
		// s.getTooltip().set("Red Crystal", "Smash red crystals using a pickaxe.");
		// actions.addSlot(2, "Crystal", s);
		// s = new RevolverSlot(stage, new Vector2(1, 2), "voxel:5");
		// s.getTooltip().set("Blue Crystal", "Smash blue crystals using a pickaxe.");
		// actions.addSlot(2, "Crystal", s);
		//
		// s = new RevolverSlot(stage, new Vector2(2, 3), "Ore");
		// s.getTooltip().set("Ore", "Mine the different kinds of ore.");
		// actions.addSlot(1, "Mine", s);
		// s = new RevolverSlot(stage, new Vector2(3, 3), "voxel:7");
		// s.getTooltip().set("Iron Ore", "Mine iron ore using a pickaxe.");
		// s.setDisabled(true);
		// actions.addSlot(2, "Ore", s);
		// s = new RevolverSlot(stage, new Vector2(5, 3), "voxel:8");
		// s.getTooltip().set("Coal Ore", "Mine coal ore using a pickaxe.");
		// s.setDisabled(true);
		// actions.addSlot(2, "Ore", s);
		// s = new RevolverSlot(stage, new Vector2(4, 3), "voxel:9");
		// s.getTooltip().set("Gold Ore", "Mine gold ore using a pickaxe.");
		// s.setDisabled(true);
		// actions.addSlot(2, "Ore", s);
		
		s = new RevolverSlot(stage, new Vector2(1, 5), "Build");
		s.getTooltip().set("Build", "Build various building and structures.");
		actions.addSlot(0, null, s);
		s = new RevolverSlot(stage, new Vector2(1, 5), "entity:129");
		s.getTooltip().set("Towncenter", "Functions as the central point and warehouse of an island.\nA prerequisite for settling on an island.");
		actions.addSlot(1, "Build", s);
		s = new RevolverSlot(stage, new Vector2(0, 3), "entity:130");
		s.getTooltip().set("Lumberjack", "Chops nearby trees for wooden logs.");
		actions.addSlot(1, "Build", s);
		
		s = new RevolverSlot(stage, new Vector2(5, 1), "Potato");
		s.getTooltip().set("Potato", "Hi.");
		actions.addSlot(0, null, s);
		
		stage.addActor(actions);
	}
	
	@Override
	public void onCreatureSelection(final Creature creature, boolean lmb, String[] action)
	{
		if (lmb && selectedStructureWindow.setShown(false))
		{
			selectedStructureWindow.clearChildren();
			selectedStructureWindow.clearActions();
		}
		
		if (lmb && selectedEntityWindow.setShown(false) && creature == null)
		{
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
		}
		
		if (lmb && creature != null)
		{
			boolean jobsWereExpanded = selectedEntityWindow.isVisible() && selectedEntityWindow.getTitle().equals(creature.getName()) && creature instanceof Human && ((Button) selectedEntityWindow.findActor("job")).isChecked();
			
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
				final Cell<?> cell = selectedEntityWindow.add(jobsWrap).height(0);
				
				selectedEntityWindow.row();
				ItemSlot tool = new ItemSlot(stage, ((Human) creature).getTool());
				selectedEntityWindow.left().add(tool);
				
				ItemSlot slot = new ItemSlot(stage, ((Human) creature).getCarryingItemStack());
				selectedEntityWindow.add(slot);
				
				ItemSlot armor = new ItemSlot(stage, new ItemStack());
				selectedEntityWindow.add(armor);
				
				ImageButtonStyle style = new ImageButtonStyle(Vloxlands.skin.get(ButtonStyle.class));
				style.imageUp = Vloxlands.skin.getDrawable("queue");
				style.imageUp.setMinWidth(ItemSlot.size);
				style.imageUp.setMinHeight(ItemSlot.size);
				style.imageDown = Vloxlands.skin.getDrawable("queue");
				style.imageDown.setMinWidth(ItemSlot.size);
				style.imageDown.setMinHeight(ItemSlot.size);
				final TooltipImageButton job = new TooltipImageButton(stage, style);
				job.setName("job");
				job.getStyle().checked = Vloxlands.skin.getDrawable("default-round-down");
				ClickListener cl = new ClickListener()
				{
					@Override
					public void clicked(InputEvent event, float x, float y)
					{
						cell.height(cell.getMinHeight() == 100 ? 0 : 100);
						jobsWrap.setVisible(!jobsWrap.isVisible());
						selectedEntityWindow.invalidateHierarchy();
						selectedEntityWindow.pack();
					}
				};
				job.addListener(cl);
				job.pad(4);
				job.getTooltip().set("Job Queue", "Toggle Job Queue display");
				selectedEntityWindow.add(job);
				
				if (jobsWereExpanded)
				{
					job.setChecked(true);
					cl.clicked(null, 0, 0);
				}
			}
			
			selectedEntityWindow.pack();
			selectedEntityWindow.setVisible(true);
			selectedEntityWindow.toFront();
		}
	}
	
	@Override
	public void onVoxelSelection(VoxelSelection vs, boolean lmb, String[] action)
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
	public void onStructureSelection(final Structure structure, boolean lmb, String[] action)
	{
		if (lmb && selectedEntityWindow.setShown(false))
		{
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
		}
		
		if (lmb && selectedStructureWindow.setShown(false) && structure == null)
		{
			selectedStructureWindow.clearChildren();
			selectedStructureWindow.clearActions();
		}
		
		if (lmb && structure != null)
		{
			selectedStructureWindow.setTitle(structure.getName());
			selectedStructureWindow.clearChildren();
			selectedStructureWindow.clearActions();
			selectedStructureWindow.addActor(selectedStructureWindow.getButtonTable());
			
			ImageButtonStyle style = new ImageButtonStyle(Vloxlands.skin.get(ButtonStyle.class));
			style.imageUp = Vloxlands.skin.getDrawable("bomb");
			style.imageUp.setMinWidth(ItemSlot.size);
			style.imageUp.setMinHeight(ItemSlot.size);
			style.imageDown = Vloxlands.skin.getDrawable("bomb");
			style.imageDown.setMinWidth(ItemSlot.size);
			style.imageDown.setMinHeight(ItemSlot.size);
			final TooltipImageButton dismantle = new TooltipImageButton(stage, style);
			dismantle.addListener(new ClickListener()
			{
				@Override
				public void clicked(InputEvent event, float x, float y)
				{
					if (structure.isConfirmDismantle())
					{
						Dialog d = new Dialog("Confirm Dismantle", Vloxlands.skin)
						{
							@Override
							protected void result(Object object)
							{
								if (object != null) structure.requestDismantle();
							}
						};
						d.text("Are you sure you want todismantle\nthis building? All perks given by\nit will be gone after deconstruction!");
						d.button("Cancel");
						d.button("Yes", true);
						
						d.show(stage);
					}
					else structure.requestDismantle();
				}
			});
			dismantle.pad(4);
			dismantle.getTooltip().set("Dismantle building", "Request a Human to dismantle this building. The building costs get refunded by 60%.");
			
			style = new ImageButtonStyle(Vloxlands.skin.get(ButtonStyle.class));
			style.imageUp = Vloxlands.skin.getDrawable("sleep");
			style.imageUp.setMinWidth(ItemSlot.size);
			style.imageUp.setMinHeight(ItemSlot.size);
			style.imageChecked = Vloxlands.skin.getDrawable("work");
			style.imageChecked.setMinWidth(ItemSlot.size);
			style.imageChecked.setMinHeight(ItemSlot.size);
			final TooltipImageButton sleep = new TooltipImageButton(stage, style);
			sleep.setChecked(structure.isWorking());
			sleep.addListener(new ClickListener()
			{
				@Override
				public void clicked(InputEvent event, float x, float y)
				{
					structure.setWorking(!structure.isWorking());
					sleep.getTooltip().set((sleep.isChecked() ? "Dis" : "En") + "able building", "Toggle the building's working state.");
				}
			});
			sleep.pad(4);
			sleep.getTooltip().set("Production is " + (structure.isWorking() ? "running" : "paused"), "Toggle the building's production activity.");
			
			style = new ImageButtonStyle(Vloxlands.skin.get(ButtonStyle.class));
			style.imageUp = Vloxlands.skin.getDrawable("queue");
			style.imageUp.setMinWidth(ItemSlot.size);
			style.imageUp.setMinHeight(ItemSlot.size);
			style.imageChecked = Vloxlands.skin.getDrawable("queue");
			style.imageChecked.setMinWidth(ItemSlot.size);
			style.imageChecked.setMinHeight(ItemSlot.size);
			TooltipImageButton queue = new TooltipImageButton(stage, style);
			queue.getTooltip().set("Job Queue", "Toggle Job Queue display");
			queue.pad(4);
			
			
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
				
				selectedStructureWindow.row().pad(0).width(400);
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
				
				Table rightSide = new Table(Vloxlands.skin);
				rightSide.row();
				rightSide.add(capacity).colspan(2);
				rightSide.row().spaceTop(5);
				rightSide.add(dismantle);
				rightSide.add(sleep);
				selectedStructureWindow.add(rightSide).top().width(200);
			}
			
			selectedStructureWindow.pack();
			selectedStructureWindow.setVisible(true);
			selectedStructureWindow.toFront();
		}
	}
	
	@Override
	public void onVoxelRangeSelection(Island island, Vector3 start, Vector3 end, boolean lmb, String[] action)
	{}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		if (button == Buttons.LEFT)
		{
			if (dragEnd.x > -1)
			{
				float x = Math.min(dragStart.x, dragEnd.x) / Gdx.graphics.getWidth();
				float y = Math.min(dragStart.y, dragEnd.y) / Gdx.graphics.getHeight();
				float width = Math.abs(dragStart.x - dragEnd.x) / Gdx.graphics.getWidth();
				float height = Math.abs(dragStart.y - dragEnd.y) / Gdx.graphics.getHeight();
				
				GameLayer.instance.selectionBox(new Rectangle(x, y, width, height));
			}
			dragStart.set(-1, -1);
			dragEnd.set(-1, -1);
		}
		buttonDown = -1;
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		buttonDown = button;
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		if (buttonDown == Buttons.LEFT)
		{
			if (dragStart.x == -1)
			{
				dragStart.set(screenX, Gdx.graphics.getHeight() - screenY);
				dragEnd.set(screenX, Gdx.graphics.getHeight() - screenY);
			}
			else dragEnd.set(screenX, Gdx.graphics.getHeight() - screenY);
			
			return true;
		}
		return false;
	}
	
	@Override
	public void render(float delta)
	{
		stage.act();
		if (Vloxlands.currentGame.getActiveLayer() == this || !Vloxlands.currentGame.getActiveLayer().isModal())
		{
			stage.draw();
			if (dragStart.x > -1)
			{
				shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
				shapeRenderer.begin(ShapeType.Line);
				shapeRenderer.identity();
				shapeRenderer.setColor(Color.WHITE);
				shapeRenderer.rect(Math.min(dragStart.x, dragEnd.x), Math.min(dragStart.y, dragEnd.y), Math.abs(dragStart.x - dragEnd.x), Math.abs(dragStart.y - dragEnd.y));
				shapeRenderer.end();
			}
		}
	}
	
}
