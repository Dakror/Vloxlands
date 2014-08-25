package de.dakror.vloxlands.game.entity.creature;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.ai.MessageType;
import de.dakror.vloxlands.ai.SyncedStateMachine;
import de.dakror.vloxlands.ai.job.IdleJob;
import de.dakror.vloxlands.ai.job.Job;
import de.dakror.vloxlands.ai.job.WalkJob;
import de.dakror.vloxlands.ai.path.Path;
import de.dakror.vloxlands.ai.state.HelperState;
import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.game.entity.structure.NodeType;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.item.tool.Tool;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.ui.ItemSlot;
import de.dakror.vloxlands.ui.PinnableWindow;
import de.dakror.vloxlands.ui.TooltipImageButton;
import de.dakror.vloxlands.util.CurserCommand;
import de.dakror.vloxlands.util.D;
import de.dakror.vloxlands.util.event.VoxelSelection;

/**
 * @author Dakror
 */
public class Human extends Creature
{
	public static final Vector3 resourceTrn = new Vector3(0, 0.2f, -0.3f);
	
	ItemStack carryingItemStack;
	ModelInstance carryingItemModelInstance;
	Matrix4 carryingItemTransform;
	
	ItemStack tool;
	ModelInstance toolModelInstance;
	Matrix4 toolTransform;
	
	Array<Job> jobQueue = new Array<Job>();
	
	Structure workPlace;
	Structure location;
	
	boolean createModelInstanceForCarryiedItemStack;
	
	final Matrix4 tmp = new Matrix4();
	
	StateMachine<Human> stateMachine;
	Array<Object> previousStateParams = new Array<Object>();
	public Array<Object> stateParams = new Array<Object>();
	
	int tick;
	
	boolean queueRotateTowardsTarget;
	Path queueRotateTowardsTargetPath;
	
	public Human(float x, float y, float z)
	{
		super(x, y, z, "models/creature/humanblend/humanblend.g3db");
		name = "Helper";
		
		speed = 0.025f;
		climbHeight = 1;
		
		tool = new ItemStack();
		carryingItemStack = new ItemStack();
		
		stateMachine = new SyncedStateMachine<Human>(this);
		stateMachine.setInitialState(HelperState.IDLE);
		
		MessageDispatcher.getInstance().addListener(MessageType.STRUCTURE_BROADCAST.ordinal(), this);
	}
	
	public void setTool(Item tool)
	{
		if (tool == null)
		{
			toolModelInstance = null;
			toolTransform = null;
			this.tool.set(new ItemStack());
		}
		else
		{
			this.tool.setItem(tool);
			this.tool.setAmount(1);
			toolModelInstance = new ModelInstance(Vloxlands.assets.get("models/item/" + tool.getModel(), Model.class), new Matrix4());
			toolTransform = toolModelInstance.transform;
		}
	}
	
	public ItemStack getTool()
	{
		return tool;
	}
	
	public ItemStack getCarryingItemStack()
	{
		return carryingItemStack;
	}
	
	public void setCarryingItemStack(ItemStack carryingItemStack)
	{
		this.carryingItemStack.set(carryingItemStack);
		if (carryingItemStack.isNull())
		{
			carryingItemModelInstance = null;
			carryingItemTransform = null;
		}
		else createModelInstanceForCarryiedItemStack = true;
	}
	
	@Override
	public void tick(int tick)
	{
		super.tick(tick);
		
		this.tick = tick;
		
		Job j = firstJob();
		if (j != null && j.isActive() && !j.isDone()) j.tick(tick);
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		stateMachine.update();
		
		if (queueRotateTowardsTarget)
		{
			rotateTowardsGhostTarget(queueRotateTowardsTargetPath);
			queueRotateTowardsTarget = false;
			queueRotateTowardsTargetPath = null;
		}
		
		Job j = firstJob();
		if (j != null)
		{
			if (j.isActive())
			{
				if (j.isDone())
				{
					jobQueue.removeIndex(0);
					j.onEnd();
					j.triggerEndEvent();
					
					if (j.isPersistent())
					{
						j.resetState();
						queueJob(null, j);
					}
				}
			}
			else
			{
				j.trigger(tick);
				if (j instanceof WalkJob && path != ((WalkJob) j).getPath() && !j.isDone()) path = ((WalkJob) j).getPath();
			}
		}
	}
	
	@Override
	public void renderAdditional(ModelBatch batch, Environment environment)
	{
		if (!carryingItemStack.isNull() && carryingItemTransform != null)
		{
			tmp.setToRotation(Vector3.Y, 0).translate(posCache);
			tmp.rotate(Vector3.Y, rotCache.getYaw());
			tmp.translate(resourceTrn);
			carryingItemTransform.set(tmp);
		}
		
		if (!tool.isNull() && toolTransform != null)
		{
			tmp.setToRotation(Vector3.Y, 0).translate(posCache);
			tmp.rotate(Vector3.Y, rotCache.getYaw());
			
			((Tool) tool.getItem()).transformInHand(tmp, this);
			toolTransform.set(tmp);
		}
		
		if (createModelInstanceForCarryiedItemStack)
		{
			Model model = null;
			Vector3 scale = new Vector3();
			Vector3 tr = new Vector3();
			if (carryingItemStack.getItem().isModel())
			{
				model = Vloxlands.assets.get("models/item/" + carryingItemStack.getItem().getModel(), Model.class);
			}
			else if (carryingItemStack.getItem().getModel().startsWith("voxel:"))
			{
				Voxel v = Voxel.getForId(Integer.parseInt(carryingItemStack.getItem().getModel().replace("voxel:", "").trim()));
				
				ModelBuilder mb = new ModelBuilder();
				mb.begin();
				mb.part("voxel", v.getMesh(), GL20.GL_TRIANGLES, Game.world.getOpaque());
				model = mb.end();
				scale.set(0.4f, 0.4f, 0.4f);
				tr.set(-0.2f, 0, -0.3f);
			}
			else
			{
				Gdx.app.error("Human.setCarryingItemStack", "Can't handle item model!");
				return;
			}
			
			carryingItemModelInstance = new ModelInstance(model, new Matrix4());
			if (!scale.isZero()) carryingItemModelInstance.nodes.get(0).scale.set(scale);
			carryingItemModelInstance.nodes.get(0).translation.set(tr);
			carryingItemModelInstance.calculateTransforms();
			carryingItemTransform = carryingItemModelInstance.transform;
			createModelInstanceForCarryiedItemStack = false;
		}
		
		if (((jobQueue.size > 0 && firstJob().isUsingTool()) || (jobQueue.size > 1 && jobQueue.get(1).isUsingTool())) && toolModelInstance != null) batch.render(toolModelInstance, environment);
		else if (!carryingItemStack.isNull() && carryingItemModelInstance != null) batch.render(carryingItemModelInstance, environment);
	}
	
	public void queueJob(Path path, Job job)
	{
		if (job == null)
		{
			if (path.size() > 0)
			{
				WalkJob wj = new WalkJob(path, this);
				jobQueue.add(wj);
				wj.queue();
			}
		}
		else
		{
			if (path != null && path.size() > 0)
			{
				WalkJob wj = new WalkJob(path, this);
				jobQueue.add(wj);
				wj.queue();
			}
			else
			{
				queueRotateTowardsTarget = true;
				queueRotateTowardsTargetPath = path;
			}
			jobQueue.add(job);
			job.queue();
		}
	}
	
	public void setJob(Path path, Job job)
	{
		jobQueue.clear();
		queueJob(path, job);
	}
	
	public Job firstJob()
	{
		if (jobQueue.size == 0) return null;
		return jobQueue.first();
	}
	
	@Override
	public void onVoxelSelection(VoxelSelection vs, boolean lmb)
	{
		if ((wasSelected || selected) && (!lmb || D.android()) && location == null && workPlace == null)
		{
			selected = true;
			
			changeState(HelperState.WALK_TO_TARGET, vs.voxelPos.getPos());
		}
	}
	
	@Override
	public void onStructureSelection(Structure structure, boolean lmb)
	{
		if ((wasSelected || selected) && (!lmb || D.android()) && location == null && workPlace == null)
		{
			selected = true;
			
			CurserCommand c = structure.getCommandForEntity(this);
			
			if (c == CurserCommand.WALK)
			{
				changeState(HelperState.WALK_TO_TARGET, structure.getStructureNode(posCache, NodeType.target).pos.cpy().add(structure.getVoxelPos()));
			}
		}
	}
	
	@Override
	public void onVoxelRangeSelection(Island island, Vector3 start, Vector3 end, boolean lmb)
	{}
	
	@Override
	public void onReachTarget()
	{
		super.onReachTarget();
		
		if (firstJob() instanceof WalkJob) firstJob().setDone();
	}
	
	public Array<Job> getJobQueue()
	{
		return jobQueue;
	}
	
	public boolean isIdle()
	{
		return jobQueue.size == 0;
	}
	
	public Structure getWorkPlace()
	{
		return workPlace;
	}
	
	public void setWorkPlace(Structure workPlace)
	{
		this.workPlace = workPlace;
	}
	
	public Structure getLocation()
	{
		return location;
	}
	
	public void setLocation(Structure location)
	{
		this.location = location;
		visible = location == null;
	}
	
	@Override
	public boolean handleMessage(Telegram msg)
	{
		return stateMachine.handleMessage(msg);
	}
	
	public void changeState(State<Human> newState, Object... params)
	{
		previousStateParams.clear();
		previousStateParams.addAll(stateParams);
		stateParams.clear();
		stateParams.addAll(params);
		stateMachine.changeState(newState);
	}
	
	public void revertToPreviousState()
	{
		stateParams.clear();
		stateParams.addAll(previousStateParams);
		stateMachine.revertToPreviousState();
	}
	
	public State<Human> getState()
	{
		return stateMachine.getCurrentState();
	}
	
	public StateMachine<Human> getStateMachine()
	{
		return stateMachine;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		MessageDispatcher.getInstance().removeListener(MessageType.STRUCTURE_BROADCAST.ordinal(), this);
	}
	
	@Override
	public void setUI(final PinnableWindow window, Object... params)
	{
		window.row().pad(0).colspan(50).padRight(-10).fillX();
		final List<Job> jobs = new List<Job>(Vloxlands.skin);
		jobs.setItems(new IdleJob(this));
		jobs.addAction(new Action()
		{
			@Override
			public boolean act(float delta)
			{
				if (jobQueue.size == 0 && jobs.getItems().get(0) instanceof IdleJob) return false;
				
				if (!jobQueue.equals(jobs.getItems()))
				{
					if (jobQueue.size > 0) jobs.setItems(jobQueue);
					else jobs.setItems(new IdleJob(Human.this));
					
					jobs.getSelection().setDisabled(true);
					jobs.setSelectedIndex(-1);
					window.pack();
				}
				
				return false;
			}
		});
		jobs.getSelection().setDisabled(true);
		jobs.setSelectedIndex(-1);
		jobs.getStyle().selection.setLeftWidth(10);
		jobs.getStyle().selection.setTopHeight(3);
		final ScrollPane jobsWrap = new ScrollPane(jobs, Vloxlands.skin);
		jobsWrap.setVisible(false);
		jobsWrap.setScrollbarsOnTop(false);
		jobsWrap.setFadeScrollBars(false);
		final Cell<?> cell = window.add(jobsWrap).height(0);
		
		window.row();
		ItemSlot tool = new ItemSlot(window.getStage(), this.tool);
		window.left().add(tool).spaceRight(2);
		
		ItemSlot slot = new ItemSlot(window.getStage(), carryingItemStack);
		window.add(slot).spaceRight(2);
		
		ItemSlot armor = new ItemSlot(window.getStage(), new ItemStack());
		window.add(armor).spaceRight(2);
		
		ImageButtonStyle style = new ImageButtonStyle(Vloxlands.skin.get("image_toggle", ButtonStyle.class));
		style.imageUp = Vloxlands.skin.getDrawable("queue");
		style.imageUp.setMinWidth(ItemSlot.size);
		style.imageUp.setMinHeight(ItemSlot.size);
		style.imageDown = Vloxlands.skin.getDrawable("queue");
		style.imageDown.setMinWidth(ItemSlot.size);
		style.imageDown.setMinHeight(ItemSlot.size);
		final TooltipImageButton job = new TooltipImageButton(style);
		window.getStage().addActor(job.getTooltip());
		job.setName("job");
		ClickListener cl = new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				cell.height(cell.getMinHeight() == 100 ? 0 : 100);
				jobsWrap.setVisible(!jobsWrap.isVisible());
				window.invalidateHierarchy();
				window.pack();
			}
		};
		job.addListener(cl);
		job.getTooltip().set("Job Queue", "Toggle Job Queue display");
		window.add(job).padRight(-10);
		
		if (params[0] == Boolean.TRUE)
		{
			job.setChecked(true);
			cl.clicked(null, 0, 0);
		}
	}
}
