package de.dakror.vloxlands.game.entity.creature;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.ai.AStar;
import de.dakror.vloxlands.ai.BFS;
import de.dakror.vloxlands.ai.Path;
import de.dakror.vloxlands.ai.Path.PathBundle;
import de.dakror.vloxlands.game.Query;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.entity.structure.StructureNode.NodeType;
import de.dakror.vloxlands.game.entity.structure.Warehouse;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.item.tool.Tool;
import de.dakror.vloxlands.game.job.DumpJob;
import de.dakror.vloxlands.game.job.Job;
import de.dakror.vloxlands.game.job.MineJob;
import de.dakror.vloxlands.game.job.WalkJob;
import de.dakror.vloxlands.layer.GameLayer;
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
	
	public Human(float x, float y, float z)
	{
		super(x, y, z, "models/humanblend/humanblend.g3db");
		name = "Human";
		
		speed = 0.025f;
		climbHeight = 1;
		
		tool = new ItemStack();
		carryingItemStack = new ItemStack();
	}
	
	public void setTool(Item tool)
	{
		if (tool == null)
		{
			toolModelInstance = null;
			toolTransform = null;
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
		else
		{
			carryingItemModelInstance = new ModelInstance(Vloxlands.assets.get("models/item/" + carryingItemStack.getItem().getModel(), Model.class), new Matrix4());
			carryingItemTransform = carryingItemModelInstance.transform;
		}
	}
	
	@Override
	public void tick(int tick)
	{
		super.tick(tick);
		
		if (!carryingItemStack.isNull())
		{
			carryingItemTransform.setToRotation(Vector3.Y, 0).translate(posCache);
			carryingItemTransform.rotate(Vector3.Y, rotCache.getYaw());
			carryingItemTransform.translate(resourceTrn);
		}
		
		if (!tool.isNull())
		{
			toolTransform.setToRotation(Vector3.Y, 0).translate(posCache);
			toolTransform.rotate(Vector3.Y, rotCache.getYaw());
			
			((Tool) tool.getItem()).transformInHand(toolTransform, this);
		}
		
		if (jobQueue.size > 0)
		{
			Job j = firstJob();
			if (j.isActive())
			{
				if (j instanceof WalkJob)
				{
					if (path != ((WalkJob) j).getPath()) path = ((WalkJob) j).getPath();
				}
				
				if (j.isDone())
				{
					j.onEnd();
					
					jobQueue.removeIndex(0);
					onJobDone(j);
					
					if (j.isPersistent())
					{
						j.resetState();
						queueJob(null, j);
					}
				}
				else j.tick(tick);
			}
			else j.trigger();
		}
	}
	
	@Override
	public void renderAdditional(ModelBatch batch, Environment environment)
	{
		if ((firstJob() instanceof MineJob) || (jobQueue.size > 1 && jobQueue.get(1) instanceof MineJob)) batch.render(toolModelInstance, environment);
		else if (!carryingItemStack.isNull()) batch.render(carryingItemModelInstance, environment);
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
		if (wasSelected && !lmb)
		{
			boolean mineTarget = !tool.isNull() && vs.type.getMining() > 0 && vs.type.hasItemdrop() && (carryingItemStack.isNull() || (!carryingItemStack.isFull() && carryingItemStack.getItem().getId() == vs.type.getItemdrop())) && Gdx.input.isKeyPressed(Keys.CONTROL_LEFT);
			
			Path path = AStar.findPath(getVoxelBelow(), vs.voxel, this, mineTarget);
			
			if (path != null)
			{
				if (mineTarget) setJob(path, new MineJob(this, vs, Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)));
				else setJob(path, null);
			}
			selected = true;
		}
	}
	
	@Override
	public void onStructureSelection(Structure structure, boolean lmb)
	{
		if (wasSelected && !lmb)
		{
			Job job = null;
			NodeType type = NodeType.target;
			if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && !carryingItemStack.isNull() && structure.isWorking())
			{
				job = new DumpJob(this, structure, false);
				type = NodeType.dump;
			}
			
			Vector3 v = structure.getStructureNode(posCache, type).pos.cpy().add(structure.getVoxelPos());
			Path path = AStar.findPath(getVoxelBelow(), v, this, type.useGhostTarget);
			if (path != null || job != null) setJob(path, job);
		}
	}
	
	@Override
	public void onReachTarget()
	{
		super.onReachTarget();
		
		if (firstJob() instanceof WalkJob) firstJob().setDone();
	}
	
	public void onJobDone(Job j)
	{
		if (j instanceof MineJob)
		{
			PathBundle pps = null;
			
			if (carryingItemStack.isFull())
			{
				pps = GameLayer.world.query(new Query(this).searchClass(Warehouse.class).transport(carryingItemStack).structure(true).node(NodeType.dump).island(0));
				if (pps != null) queueJob(pps.path, new DumpJob(this, pps.structure, false));
				else Gdx.app.error("Human.onJobDone", "Couldn't find a Warehouse to dump stuff");
			}
			if (j.isPersistent())
			{
				Path path = BFS.findClosestVoxel(pps != null ? pps.path.getLast() : getVoxelBelow(), ((MineJob) j).getTarget().type.getId(), this);
				if (path != null)
				{
					((MineJob) j).getTarget().voxel.set(path.getGhostTarget());
					queueJob(path, null);
				}
				else
				{
					Gdx.app.error("Human.onJobDone", "No more voxels of this type to mine / I am too stupid to find a path to one (more likely)!");
					j.setPersistent(false);
				}
			}
		}
	}
	
	public Array<Job> getJobQueue()
	{
		return jobQueue;
	}
	
	public boolean isIdle()
	{
		return jobQueue.size == 0;
	}
	
	@Override
	public void onEnd(AnimationDesc animation)
	{
		if (jobQueue.size > 0) firstJob().setDone();
	}
}
