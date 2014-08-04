package de.dakror.vloxlands.game.entity.creature;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
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
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.ai.MessageType;
import de.dakror.vloxlands.ai.path.Path;
import de.dakror.vloxlands.ai.state.HelperState;
import de.dakror.vloxlands.game.entity.structure.NodeType;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.item.tool.Tool;
import de.dakror.vloxlands.game.job.Job;
import de.dakror.vloxlands.game.job.WalkJob;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.game.world.World;
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
	
	public Human(float x, float y, float z)
	{
		super(x, y, z, "models/creature/humanblend/humanblend.g3db");
		name = "Helper";
		
		speed = 0.025f;
		climbHeight = 1;
		
		tool = new ItemStack();
		carryingItemStack = new ItemStack();
		
		
		stateMachine = new DefaultStateMachine<Human>(this);
		stateMachine.setInitialState(HelperState.IDLE);
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
		stateMachine.update();
		
		Job j = firstJob();
		if (j != null)
		{
			if (j.isActive())
			{
				if (j instanceof WalkJob && path != ((WalkJob) j).getPath() && !j.isDone() && !((WalkJob) j).getPath().isDone()) path = ((WalkJob) j).getPath();
				
				if (!j.isDone()) j.tick(tick);
				else
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
			else j.trigger(tick);
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
				mb.part("voxel", v.getMesh(), GL20.GL_TRIANGLES, World.opaque);
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
			if (path.size() > 0) jobQueue.add(new WalkJob(path, this));
		}
		else
		{
			if (path != null && path.size() > 0) jobQueue.add(new WalkJob(path, this));
			else rotateTowardsGhostTarget(path);
			jobQueue.add(job);
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
		if ((wasSelected || selected) && (!lmb || D.android()) && location == null)
		{
			selected = true;
			
			changeState(HelperState.WALK_TO_TARGET, vs.voxelPos.getPos());
		}
	}
	
	@Override
	public void onStructureSelection(Structure structure, boolean lmb)
	{
		if ((wasSelected || selected) && (!lmb || D.android()) && location == null)
		{
			selected = true;
			
			CurserCommand c = structure.getCommandForEntity(this);
			if (c == CurserCommand.BUILD && !structure.isBuilt())
			{
				if (structure.getBuildInventory().getCount() == 0)
				{
					changeState(HelperState.BUILD, structure);
				}
				else
				{
					changeState(HelperState.GET_RESOURCES_FOR_BUILD, structure);
				}
			}
			else if (c == CurserCommand.WALK)
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
		if (params.length > 2)
		{
			throw new IllegalArgumentException("Can only pass up to 2 parameters to a state");
		}
		
		stateMachine.changeState(newState);
		if (params.length > 0 && params[0] != null) MessageDispatcher.getInstance().dispatchMessage(0, this, this, MessageType.PARAM0.ordinal(), params[0]);
		if (params.length > 1 && params[1] != null) MessageDispatcher.getInstance().dispatchMessage(0, this, this, MessageType.PARAM1.ordinal(), params[1]);
	}
	
	public State<Human> getState()
	{
		return stateMachine.getCurrentState();
	}
	
	public StateMachine<Human> getStateMachine()
	{
		return stateMachine;
	}
}
