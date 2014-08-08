package de.dakror.vloxlands.game.entity.structure;

import java.util.NoSuchElementException;

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
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.ai.MessageType;
import de.dakror.vloxlands.ai.state.HelperState;
import de.dakror.vloxlands.ai.state.StateTools;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.EntityItem;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.item.inv.Inventory;
import de.dakror.vloxlands.game.item.inv.ManagedInventory;
import de.dakror.vloxlands.game.item.inv.ResourceList;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.layer.GameLayer;
import de.dakror.vloxlands.ui.ItemSlot;
import de.dakror.vloxlands.ui.PinnableWindow;
import de.dakror.vloxlands.ui.TooltipImageButton;
import de.dakror.vloxlands.util.CurserCommand;
import de.dakror.vloxlands.util.InventoryProvider;
import de.dakror.vloxlands.util.ResourceListProvider;
import de.dakror.vloxlands.util.Savable;
import de.dakror.vloxlands.util.event.BroadcastPayload;
import de.dakror.vloxlands.util.event.InventoryListener;

/**
 * @author Dakror
 */
public abstract class Structure extends Entity implements InventoryProvider, InventoryListener, ResourceListProvider, Savable
{
	Array<StructureNode> nodes;
	Array<Human> workers;
	Vector3 voxelPos;
	Inventory inventory;
	/**
	 * Works reversed. Gets filled when placed and <code>built == false</code>. Gets emptied by delivering the building materials
	 */
	ManagedInventory buildInventory;
	ResourceList resourceList;
	String workerName;
	State<Human> workerState;
	Class<?> workerTool;
	Array<State<Human>> requestedHumanStates;
	Array<State<Human>> handledHumanStates;
	
	boolean working;
	
	boolean confirmDismante;
	boolean built;
	
	int buildProgress;
	int lastStateRequest;
	
	public boolean tickRequestsEnabled = true;
	
	final Vector3 tmp = new Vector3();
	final Vector3 dim = new Vector3();
	
	public Structure(float x, float y, float z, String model)
	{
		super(Math.round(x), Math.round(y), Math.round(z), model);
		voxelPos = new Vector3(x, y, z);
		
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
		inventory.addListener(this);
		buildInventory = new ManagedInventory(256 /* That should be enough... */);
		buildInventory.addListener(this);
		resourceList = new ResourceList();
		requestedHumanStates = new Array<State<Human>>();
		handledHumanStates = new Array<State<Human>>();
		working = true;
		
		dim.set((float) Math.ceil(boundingBox.getDimensions().x), (float) Math.ceil(boundingBox.getDimensions().y), (float) Math.ceil(boundingBox.getDimensions().z));
		
		setBuilt(false);
	}
	
	public Vector3 getVoxelPos()
	{
		return voxelPos;
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
			for (Byte b : resourceList.getAll())
				buildInventory.add(new ItemStack(Item.getForId(b), resourceList.get(b)));
		}
	}
	
	public Vector3 getCenter()
	{
		return voxelPos.cpy().add(boundingBox.getDimensions().cpy().scl(0.5f));
	}
	
	public void updateVoxelPos()
	{
		transform.getTranslation(posCache);
		transform.getRotation(rotCache);
		Vector3 p = posCache.cpy().sub(island.pos).sub(boundingBox.getDimensions().cpy().scl(0.5f));
		voxelPos = new Vector3(Math.round(p.x), Math.round(p.y), Math.round(p.z));
	}
	
	public boolean canBePlaced()
	{
		int width = (int) Math.ceil(boundingBox.getDimensions().x);
		int height = (int) Math.ceil(boundingBox.getDimensions().y);
		int depth = (int) Math.ceil(boundingBox.getDimensions().z);
		
		for (int i = 0; i < width; i++)
			for (int j = -1; j < height; j++)
				for (int k = 0; k < depth; k++)
				{
					if (j == -1 && island.get(i + voxelPos.x, j + voxelPos.y + 1, k + voxelPos.z) == 0) return false;
					else if (j > -1 && island.get(i + voxelPos.x, j + voxelPos.y + 1, k + voxelPos.z) != 0) return false;
				}
		
		for (Entity s : island.getEntities())
			if (s instanceof Structure && intersects((Structure) s)) return false;
		
		return true;
	}
	
	/**
	 * @return true if building is done
	 */
	public boolean progressBuild()
	{
		if (buildProgress == resourceList.getCount())
		{
			if (!built) setBuilt(true);
			return true;
		}
		
		buildProgress++;
		
		if (buildProgress == resourceList.getCount())
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
		if (workers.size >= resourceList.getCostPopulation()) return false;
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
		
		if (lastStateRequest == 0) lastStateRequest = tick;
		
		if ((tick - lastStateRequest) % 60 == 0 && tickRequestsEnabled)
		{
			for (State<Human> s : requestedHumanStates)
				broadcast(s);
			
			if (workers.size < resourceList.getCostPopulation() && built) broadcast(HelperState.START_WORK);
			if (inventory.getCount() >= inventory.getCapacity() / 2 && resourceList.getCostPopulation() > 0 && !requestedHumanStates.contains(HelperState.EMPTY_INVENTORY, true) && !handledHumanStates.contains(HelperState.EMPTY_INVENTORY, true))
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
	
	public ManagedInventory getBuildInventory()
	{
		return buildInventory;
	}
	
	public Inventory getInnerInventory()
	{
		return inventory;
	}
	
	@Override
	public ResourceList getResourceList()
	{
		return resourceList;
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
			Vector3 p = GameLayer.instance.activeIsland.pos;
			EntityItem i = new EntityItem(Island.SIZE / 2 - 5, Island.SIZE / 4 * 3 + p.y + 1, Island.SIZE / 2, Item.get("YELLOW_CRYSTAL"), 1);
			island.addEntity(i, false, false);
			return true;
		}
		
		if (msg.message == MessageType.STRUCTURE_BROADCAST_HANDLED.ordinal())
		{
			requestedHumanStates.removeValue((State<Human>) msg.extraInfo, true);
		}
		
		return false;
	}
	
	public boolean intersects(Structure o)
	{
		float lx = Math.abs(posCache.x - o.posCache.x);
		float sumx = (dim.x / 2.0f) + (o.dim.x / 2.0f);
		
		float ly = Math.abs(posCache.y - o.posCache.y);
		float sumy = (dim.y / 2.0f) + (o.dim.y / 2.0f);
		
		float lz = Math.abs(posCache.z - o.posCache.z);
		float sumz = (dim.z / 2.0f) + (o.dim.z / 2.0f);
		
		return (lx <= sumx && ly <= sumy && lz <= sumz);
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
	public void onItemAdded(int countBefore, Inventory inventory)
	{}
	
	@Override
	public void onItemRemoved(int countBefore, Inventory inventory)
	{
		if (inventory instanceof ManagedInventory)
		{
			if (inventory.getCount() == 0 && countBefore > 0 && resourceList.getCount() > 0) broadcast(1, HelperState.BUILD);
		}
		else
		{
			handledHumanStates.removeValue(HelperState.EMPTY_INVENTORY, true);
		}
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
	
	protected Table getDefaultTable(final PinnableWindow window)
	{
		ImageButtonStyle style = new ImageButtonStyle(Vloxlands.skin.get(ButtonStyle.class));
		style.imageUp = Vloxlands.skin.getDrawable("bomb");
		style.imageUp.setMinWidth(ItemSlot.size);
		style.imageUp.setMinHeight(ItemSlot.size);
		style.imageDown = Vloxlands.skin.getDrawable("bomb");
		style.imageDown.setMinWidth(ItemSlot.size);
		style.imageDown.setMinHeight(ItemSlot.size);
		final TooltipImageButton dismantle = new TooltipImageButton(window.getStage(), style);
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
		dismantle.pad(4);
		dismantle.getTooltip().set("Dismantle building", "Request a Human to dismantle this building. The building costs get refunded by 60%.");
		
		style = new ImageButtonStyle(Vloxlands.skin.get(ButtonStyle.class));
		style.imageUp = Vloxlands.skin.getDrawable("sleep");
		style.imageUp.setMinWidth(ItemSlot.size);
		style.imageUp.setMinHeight(ItemSlot.size);
		style.imageChecked = Vloxlands.skin.getDrawable("work");
		style.imageChecked.setMinWidth(ItemSlot.size);
		style.imageChecked.setMinHeight(ItemSlot.size);
		final TooltipImageButton sleep = new TooltipImageButton(window.getStage(), style);
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
		sleep.pad(4);
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
		
		Table rightSide = new Table(Vloxlands.skin);
		rightSide.row();
		rightSide.add(capacity).colspan(2);
		rightSide.row().spaceTop(5);
		rightSide.add(dismantle);
		rightSide.add(sleep);
		
		return rightSide;
	}
	
	protected void setupUI(PinnableWindow window, Object... params)
	{
		window.add(getDefaultTable(window)).top().width(200);
	}
	
	@Override
	public final void setUI(PinnableWindow window, Object... params)
	{
		if (isBuilt())
		{
			setupUI(window, params);
		}
		else
		{
			final Table res = new Table();
			Inventory inv = getInventory();
			Texture tex = Vloxlands.assets.get("img/icons.png", Texture.class);
			int i = 0;
			for (Byte b : getResourceList().getAll())
			{
				if (i % 4 == 0 && i > 0) res.row();
				Item item = Item.getForId(b);
				Image img = new Image(new TextureRegion(tex, item.getIconX() * Item.SIZE, item.getIconY() * Item.SIZE, Item.SIZE, Item.SIZE));
				res.add(img);
				
				int max = getResourceList().get(b);
				
				Label l = new Label((max - inv.get(b)) + " / " + max, Vloxlands.skin);
				l.setName(b + "");
				l.setWrap(true);
				res.add(l).width(50);
				i++;
			}
			window.add(res).minWidth(200);
			final ProgressBar progress = new ProgressBar(0, getResourceList().getCount(), 1, false, Vloxlands.skin);
			progress.setAnimateDuration(0.2f);
			progress.setAnimateInterpolation(Interpolation.pow3);
			window.addAction(new Action()
			{
				@Override
				public boolean act(float delta)
				{
					Inventory inv = getInventory();
					progress.setValue(getBuildProgress());
					for (Byte b : getResourceList().getAll())
					{
						Actor a = res.findActor(b + "");
						if (a instanceof Label)
						{
							int max = getResourceList().get(b);
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
}
