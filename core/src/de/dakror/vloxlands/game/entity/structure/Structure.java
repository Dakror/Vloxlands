package de.dakror.vloxlands.game.entity.structure;

import java.util.Comparator;
import java.util.NoSuchElementException;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.ai.MessageType;
import de.dakror.vloxlands.ai.path.AStar;
import de.dakror.vloxlands.ai.state.HelperState;
import de.dakror.vloxlands.ai.state.StateTools;
import de.dakror.vloxlands.ai.task.Task;
import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.ItemDrop;
import de.dakror.vloxlands.game.entity.StaticEntity;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.item.inv.Inventory;
import de.dakror.vloxlands.game.item.inv.NonStackingInventory;
import de.dakror.vloxlands.game.item.inv.ResourceList;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.ui.ItemSlot;
import de.dakror.vloxlands.ui.PinnableWindow;
import de.dakror.vloxlands.ui.RevolverSlot;
import de.dakror.vloxlands.ui.TaskListItem;
import de.dakror.vloxlands.ui.TooltipImageButton;
import de.dakror.vloxlands.ui.skin.DProgressBar;
import de.dakror.vloxlands.util.CurserCommand;
import de.dakror.vloxlands.util.event.BroadcastPayload;
import de.dakror.vloxlands.util.event.InventoryListener;
import de.dakror.vloxlands.util.interf.Savable;
import de.dakror.vloxlands.util.interf.provider.InventoryProvider;
import de.dakror.vloxlands.util.interf.provider.ResourceListProvider;

/**
 * @author Dakror
 */
// TODO: saving
public abstract class Structure extends StaticEntity implements InventoryProvider, InventoryListener, ResourceListProvider, Savable
{
	Array<StructureNode> nodes;
	Array<Human> workers;
	Inventory inventory;
	/**
	 * Works reversed. Gets filled when placed and <code>built == false</code>. Gets emptied by delivering the building materials
	 */
	NonStackingInventory buildInventory;
	ResourceList costs;
	String workerName;
	State<Human> workerState;
	Class<?> workerTool;
	Array<State<Human>> requestedHumanStates;
	Array<State<Human>> handledHumanStates;
	
	Array<Task> tasks;
	private Array<Task> taskQueue;
	int taskTicksLeft;
	
	boolean working;
	
	boolean confirmDismante;
	boolean built;
	
	int buildProgress;
	int lastStateRequest;
	
	public boolean tickRequestsEnabled = true;
	
	final Vector3 tmp = new Vector3();
	
	public Structure(float x, float y, float z, String model)
	{
		super(Math.round(x), Math.round(y), Math.round(z), model);
		
		nodes = new Array<StructureNode>();
		workers = new Array<Human>();
		
		int width = (int) Math.ceil(boundingBox.getDimensions().x);
		int depth = (int) Math.ceil(boundingBox.getDimensions().z);
		
		nodes.add(new StructureNode(NodeType.target, -1, 0, Math.round(depth / 2)));
		nodes.add(new StructureNode(NodeType.target, width, 0, Math.round(depth / 2)));
		nodes.add(new StructureNode(NodeType.target, Math.round(width / 2), 0, -1));
		nodes.add(new StructureNode(NodeType.target, Math.round(width / 2), 0, depth));
		
		nodes.add(new StructureNode(NodeType.build, 0, 0, Math.round(depth / 2)));
		nodes.add(new StructureNode(NodeType.build, width - 1, 0, Math.round(depth / 2)));
		nodes.add(new StructureNode(NodeType.build, Math.round(width / 2), 0, 0));
		nodes.add(new StructureNode(NodeType.build, Math.round(width / 2), 0, depth - 1));
		
		inventory = new Inventory();
		buildInventory = new NonStackingInventory(256 /* That should be enough... */);
		costs = new ResourceList();
		requestedHumanStates = new Array<State<Human>>();
		handledHumanStates = new Array<State<Human>>();
		tasks = new Array<Task>();
		taskQueue = new Array<Task>();
		working = true;
		
		setBuilt(false);
	}
	
	public boolean isBuilt()
	{
		return built;
	}
	
	public void setBuilt(boolean built)
	{
		this.built = built;
		modelVisible = built;
		additionalVisible = built;
		
		if (!built)
		{
			buildInventory.clear();
			for (Byte b : costs.getAll())
				buildInventory.add(new ItemStack(Item.getForId(b), costs.get(b)));
		}
	}
	
	public Vector3 getCenter()
	{
		return voxelPos.cpy().add(boundingBox.getDimensions().cpy().scl(0.5f));
	}
	
	/**
	 * @return true if building is done
	 */
	public boolean progressBuild()
	{
		if (buildProgress == costs.getCount())
		{
			if (!built) setBuilt(true);
			return true;
		}
		
		buildProgress++;
		
		if (buildProgress == costs.getCount())
		{
			if (!built) setBuilt(true);
			return true;
		}
		return false;
	}
	
	public int getBuildProgress()
	{
		return buildProgress;
	}
	
	public boolean addWorker(Human human)
	{
		if (workers.size >= costs.getCostPopulation()) return false;
		if (human.getWorkPlace() != null) return false;
		
		human.setWorkPlace(this);
		human.setName(workerName);
		workers.add(human);
		StateTools.initForWorkplace(human);
		onWorkerAdded(human);
		return true;
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		inventory.addListener(this);
		
		for (Entity e : island.getEntities())
		{
			if (e instanceof Creature && ((Creature) e).path != null)
			{
				((Creature) e).path = AStar.findPath(((Creature) e).getVoxelBelow(), ((Creature) e).path.getGhostTarget() != null ? ((Creature) e).path.getGhostTarget() : ((Creature) e).path.getLast(), (Creature) e, ((Creature) e).path.getGhostTarget() != null);
			}
		}
		
		tickRequestsEnabled = true;
		if (!built)
		{
			int width = (int) Math.ceil(boundingBox.getDimensions().x);
			int depth = (int) Math.ceil(boundingBox.getDimensions().z);
			
			byte gr = Voxel.get("GRAVEL").getId();
			
			for (int i = 0; i < width; i++)
				for (int j = 0; j < depth; j++)
					island.set(i + voxelPos.x, voxelPos.y, j + voxelPos.z, gr);
			
			broadcast(HelperState.GET_RESOURCES_FOR_BUILD);
		}
	}
	
	@Override
	public void tick(int tick)
	{
		super.tick(tick);
		
		if (taskQueue.size > 0)
		{
			taskTicksLeft--;
			if (taskTicksLeft <= 0)
			{
				if (taskQueue.first().started)
				{
					taskQueue.first().exit();
					taskQueue.removeIndex(0);
				}
				
				if (taskQueue.size > 0)
				{
					taskTicksLeft = taskQueue.first().getDuration();
					taskQueue.first().enter();
				}
			}
		}
		
		if (lastStateRequest == 0) lastStateRequest = tick;
		
		if ((tick - lastStateRequest) % 60 == 0 && tickRequestsEnabled)
		{
			for (State<Human> s : requestedHumanStates)
				broadcast(s);
			
			if (workers.size < costs.getCostPopulation() && built) broadcast(HelperState.START_WORK);
			if (inventory.getCount() >= inventory.getCapacity() / 2 && costs.getCostPopulation() > 0 && !requestedHumanStates.contains(HelperState.EMPTY_INVENTORY, true) && !handledHumanStates.contains(HelperState.EMPTY_INVENTORY, true))
			{
				requestedHumanStates.add(HelperState.EMPTY_INVENTORY);
				handledHumanStates.add(HelperState.EMPTY_INVENTORY);
			}
			
			lastStateRequest = tick;
		}
	}
	
	/**
	 * @param from expected to be in world space
	 */
	public StructureNode getStructureNode(Vector3 from, NodeType type, String name)
	{
		if (name != null)
		{
			for (StructureNode sn : nodes)
				if (sn.name.equals(name)) return sn;
		}
		
		StructureNode node = null;
		float distance = 0;
		
		for (StructureNode sn : nodes)
		{
			if (type != null && sn.type != type) continue;
			if (from == null) return sn;
			
			tmp.set(posCache).add(sn.pos);
			if (node == null || from.dst(tmp) < distance)
			{
				node = sn;
				distance = from.dst(tmp);
			}
		}
		
		return node;
	}
	
	public boolean hasStructureNode(NodeType type)
	{
		try
		{
			for (StructureNode sn : nodes)
				if (sn.type == type) return true;
		}
		catch (NoSuchElementException e)
		{
			return false;
		}
		
		return false;
	}
	
	public boolean hasStructureNode(String name)
	{
		for (StructureNode sn : nodes)
			if (sn.name.equals(name)) return true;
		
		return false;
	}
	
	/**
	 * @param from expected to be in world space
	 */
	public StructureNode getStructureNode(Vector3 from, NodeType type)
	{
		return getStructureNode(from, type, null);
	}
	
	public Array<StructureNode> getStructureNodes()
	{
		return nodes;
	}
	
	@Override
	public Inventory getInventory()
	{
		return built ? inventory : buildInventory;
	}
	
	public NonStackingInventory getBuildInventory()
	{
		return buildInventory;
	}
	
	public Inventory getInnerInventory()
	{
		return inventory;
	}
	
	@Override
	public ResourceList getCosts()
	{
		return costs;
	}
	
	@Override
	public ResourceList getResult()
	{
		return null;
	}
	
	public boolean isWorking()
	{
		return working;
	}
	
	public boolean isConfirmDismantle()
	{
		return confirmDismante;
	}
	
	public void setWorking(boolean working)
	{
		this.working = working;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean handleMessage(Telegram msg)
	{
		if (msg.message == MessageType.YOU_ARE_DISMANTLED.ordinal())
		{
			kill();
			Vector3 p = Game.instance.activeIsland.pos;
			ItemDrop i = new ItemDrop(Island.SIZE / 2 - 5, Island.SIZE / 4 * 3 + p.y + 1, Island.SIZE / 2, Item.get("YELLOW_CRYSTAL"), 1);
			island.addEntity(i, false, false);
			return true;
		}
		
		if (msg.message == MessageType.STRUCTURE_BROADCAST_HANDLED.ordinal())
		{
			requestedHumanStates.removeValue((State<Human>) msg.extraInfo, true);
		}
		
		return false;
	}
	
	protected void onWorkerAdded(Human human)
	{}
	
	public State<Human> getWorkerState()
	{
		return workerState;
	}
	
	public Class<?> getWorkerTool()
	{
		return workerTool;
	}
	
	public void broadcast(State<Human> requestedState, Object... params)
	{
		broadcast(0, requestedState, params);
	}
	
	public void broadcast(float delay, State<Human> requestedState, Object... params)
	{
		Array<Object> array = new Array<Object>(params);
		array.insert(0, this);
		
		if (!requestedHumanStates.contains(requestedState, true)) requestedHumanStates.add(requestedState);
		
		MessageDispatcher.getInstance().dispatchMessage(delay, this, null, MessageType.STRUCTURE_BROADCAST.ordinal(), new BroadcastPayload(requestedState, array.items));
	}
	
	@Override
	public void onItemAdded(int countBefore, Item item, Inventory inventory)
	{}
	
	@Override
	public void onItemRemoved(int countBefore, Item item, Inventory inventory)
	{
		handledHumanStates.removeValue(HelperState.EMPTY_INVENTORY, true);
	}
	
	public CurserCommand getDefaultCommand()
	{
		return CurserCommand.WALK;
	}
	
	public CurserCommand getCommandForEntity(Entity selectedEntity)
	{
		if (selectedEntity instanceof Human && !built) return CurserCommand.BUILD;
		if (selectedEntity instanceof Human && built) return CurserCommand.WORK;
		
		return getDefaultCommand();
	}
	
	public CurserCommand getCommandForStructure(Structure selectedStructure)
	{
		return getDefaultCommand();
	}
	
	protected Table getDefaultTable(final PinnableWindow window, Object... params)
	{
		ImageButtonStyle style = new ImageButtonStyle(Vloxlands.skin.get("image", ButtonStyle.class));
		style.imageUp = Vloxlands.skin.getDrawable("bomb");
		style.imageUp.setMinWidth(ItemSlot.size);
		style.imageUp.setMinHeight(ItemSlot.size);
		style.imageDown = Vloxlands.skin.getDrawable("bomb");
		style.imageDown.setMinWidth(ItemSlot.size);
		style.imageDown.setMinHeight(ItemSlot.size);
		final TooltipImageButton dismantle = new TooltipImageButton(style);
		window.getStage().addActor(dismantle.getTooltip());
		dismantle.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				if (isConfirmDismantle())
				{
					Dialog d = new Dialog("Confirm Dismantle", Vloxlands.skin)
					{
						@Override
						protected void result(Object object)
						{
							if (object != null) broadcast(HelperState.DISMANTLE);
						}
					};
					d.text("Are you sure you want todismantle\nthis building? All perks given by\nit will be gone after deconstruction!");
					d.button("Cancel");
					d.button("Yes", true);
					
					d.show(window.getStage());
				}
				else broadcast(HelperState.DISMANTLE);
			}
		});
		dismantle.getTooltip().set("Dismantle building", "Request a Human to dismantle this building. The building costs get refunded by 60%.");
		
		style = new ImageButtonStyle(Vloxlands.skin.get("image_toggle", ButtonStyle.class));
		style.imageUp = Vloxlands.skin.getDrawable("sleep");
		style.imageUp.setMinWidth(ItemSlot.size);
		style.imageUp.setMinHeight(ItemSlot.size);
		style.imageChecked = Vloxlands.skin.getDrawable("work");
		style.imageChecked.setMinWidth(ItemSlot.size);
		style.imageChecked.setMinHeight(ItemSlot.size);
		final TooltipImageButton sleep = new TooltipImageButton(style);
		window.getStage().addActor(sleep.getTooltip());
		sleep.setChecked(isWorking());
		sleep.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				setWorking(!isWorking());
				sleep.getTooltip().set((sleep.isChecked() ? "Dis" : "En") + "able building", "Toggle the building's working state.");
			}
		});
		
		sleep.getTooltip().set("Production is " + (isWorking() ? "running" : "paused"), "Toggle the building's production activity.");
		
		final Label capacity = new Label("Capacity: 0 / 10 Items", Vloxlands.skin);
		capacity.setAlignment(Align.center, Align.center);
		capacity.addAction(new Action()
		{
			@Override
			public boolean act(float delta)
			{
				capacity.setText("Capacity: " + getInventory().getCount() + " / " + getInventory().getCapacity() + " Items");
				
				float percent = getInventory().getCount() / (float) getInventory().getCapacity();
				
				if (percent >= 0.8f) capacity.setColor(1, 0.5f, 0, 1);
				else if (percent >= 0.5f) capacity.setColor(1, 1, 0, 1);
				else if (percent == 1) capacity.setColor(1, 0, 0, 1);
				else capacity.setColor(1, 1, 1, 1);
				
				return false;
			}
		});
		
		style = new ImageButtonStyle(Vloxlands.skin.get("image_toggle", ButtonStyle.class));
		style.imageUp = Vloxlands.skin.getDrawable("queue");
		style.imageUp.setMinWidth(ItemSlot.size);
		style.imageUp.setMinHeight(ItemSlot.size);
		style.imageDown = Vloxlands.skin.getDrawable("queue");
		style.imageDown.setMinWidth(ItemSlot.size);
		style.imageDown.setMinHeight(ItemSlot.size);
		final TooltipImageButton queue = new TooltipImageButton(style);
		window.getStage().addActor(queue.getTooltip());
		ClickListener cl = new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				Actor actor = window.findActor("TaskWrap");
				Cell<Actor> cell = window.getCell(actor);
				cell.height(cell.getMinHeight() == 100 ? 0 : 100);
				actor.setVisible(!actor.isVisible());
				window.invalidateHierarchy();
				if (x != -3) window.pack();
			}
		};
		queue.addListener(cl);
		queue.setName("queue");
		queue.getTooltip().set("Task Queue", "Toggle Task Queue display");
		
		if (params[0] == Boolean.TRUE)
		{
			queue.setChecked(true);
			cl.clicked(null, -3, 0);
		}
		
		Table rightSide = new Table(Vloxlands.skin);
		rightSide.row();
		rightSide.add(capacity).colspan(3);
		rightSide.row().spaceTop(5).spaceBottom(2);
		rightSide.add(dismantle).spaceRight(2);
		rightSide.add(sleep).spaceRight(2);
		rightSide.add(queue).spaceRight(2);
		
		return rightSide;
	}
	
	protected void setupTaskQueueUI(final PinnableWindow window, Object... params)
	{
		final VerticalGroup tasks = new VerticalGroup();
		tasks.left();
		tasks.addAction(new Action()
		{
			@Override
			public boolean act(float delta)
			{
				if (taskQueue.size != tasks.getChildren().size)
				{
					int size = tasks.getChildren().size;
					for (Actor a : tasks.getChildren())
					{
						if (taskQueue.size < size)
						{
							a.remove();
							size--;
						}
					}
					
					for (int i = size; i < taskQueue.size; i++)
					{
						final TaskListItem l = new TaskListItem(taskQueue.get(i).getTitle(), Vloxlands.skin);
						l.setWrap(true);
						l.setWidth(tasks.getWidth());
						l.structure = Structure.this;
						tasks.addActor(l);
					}
					
					for (int i = 0; i < tasks.getChildren().size; i++)
						tasks.getChildren().get(i).setUserObject(i);
				}
				return false;
			}
		});
		
		window.row().right().pad(5, 0, 5, -10).colspan(50).fillX();
		final ScrollPane tasksWrap = new ScrollPane(tasks, Vloxlands.skin);
		tasksWrap.setScrollbarsOnTop(false);
		tasksWrap.setFadeScrollBars(false);
		tasksWrap.setVisible(false);
		tasksWrap.setName("TaskWrap");
		window.add(tasksWrap).height(0);
	}
	
	protected void setupUI(PinnableWindow window, Object... params)
	{}
	
	@Override
	public final void setUI(PinnableWindow window, Object... params)
	{
		if (isBuilt())
		{
			setupTaskQueueUI(window, params);
			setupUI(window, params);
			window.add(getDefaultTable(window, params)).right().width(200).pad(0, -2, 0, 0);
		}
		else
		{
			final Table res = new Table();
			Inventory inv = getInventory();
			Texture tex = Vloxlands.assets.get("img/icons.png", Texture.class);
			int i = 0;
			for (Byte b : getCosts().getAll())
			{
				if (i % 4 == 0 && i > 0) res.row();
				Item item = Item.getForId(b);
				Image img = new Image(new TextureRegion(tex, item.getIconX() * Item.SIZE, item.getIconY() * Item.SIZE, Item.SIZE, Item.SIZE));
				res.add(img);
				
				int max = getCosts().get(b);
				
				Label l = new Label((max - inv.get(b)) + " / " + max, Vloxlands.skin);
				l.setName(b + "");
				l.setWrap(true);
				res.add(l).width(100);
				i++;
			}
			window.add(res).minWidth(200);
			final DProgressBar progress = new DProgressBar(0, getCosts().getCount(), 0, Vloxlands.skin);
			progress.setAnimateDuration(0.2f);
			progress.setAnimateInterpolation(Interpolation.pow3);
			window.addAction(new Action()
			{
				@Override
				public boolean act(float delta)
				{
					Inventory inv = getInventory();
					progress.setValue(getBuildProgress());
					for (Byte b : getCosts().getAll())
					{
						Actor a = res.findActor(b + "");
						if (a instanceof Label)
						{
							int max = getCosts().get(b);
							((Label) a).setText((max - inv.get(b)) + " / " + max);
						}
					}
					
					if (isBuilt())
					{
						onStructureSelection(Structure.this, true);
						return true;
					}
					return false;
				}
			});
			window.row();
			window.add(progress).width(190);
		}
	}
	
	@Override
	public void setActions(RevolverSlot parent)
	{
		tasks.sort(new Comparator<Task>()
		{
			
			@Override
			public int compare(Task o1, Task o2)
			{
				return o1.getTitle().compareTo(o2.getTitle());
			}
		});
		for (Task task : tasks)
		{
			final Task copy = task;
			final RevolverSlot s = new RevolverSlot(parent.getStage(), task.getIcon(), "task:" + task.getName());
			s.getTooltip().set(task.getTitle(), task.getDescription());
			s.addListener(new InputListener()
			{
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
				{
					return button == Buttons.LEFT;
				}
				
				@Override
				public void touchUp(InputEvent event, float x, float y, int pointer, int button)
				{
					if (!Game.instance.activeIsland.availableResources.canSubtract(copy.getCosts())) return; // safety first ;)
					
					for (Byte b : copy.getCosts().getAll())
					{
						island.takeItemsIslandWide(Item.getForId(b), copy.getCosts().get(b));
					}
					
					queueTask(copy);
				}
			});
			s.addAction(new Action()
			{
				
				@Override
				public boolean act(float delta)
				{
					s.setDisabled(!Game.instance.activeIsland.availableResources.canSubtract(copy.getCosts()));
					return false;
				}
			});
			parent.addSlot(s);
		}
	}
	
	public void queueTask(Task task)
	{
		if (!tasks.contains(task, true)) throw new IllegalArgumentException("Can't queue task '" + task + "' in structure '" + name + "'");
		
		task.setOrigin(this);
		taskQueue.add(task);
		if (taskQueue.size == 1)
		{
			taskTicksLeft = task.getDuration();
			task.enter();
		}
	}
	
	public Task firstTask()
	{
		if (taskQueue.size == 0) return null;
		return taskQueue.first();
	}
	
	public int getTaskTicksLeft()
	{
		return taskTicksLeft;
	}
}
