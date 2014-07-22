package de.dakror.vloxlands.game.entity.creature;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.ai.AStar;
import de.dakror.vloxlands.ai.BFS;
import de.dakror.vloxlands.ai.Path;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.entity.structure.StructureNode.NodeType;
import de.dakror.vloxlands.game.entity.structure.Warehouse;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.item.tool.Tool;
import de.dakror.vloxlands.game.job.BuildJob;
import de.dakror.vloxlands.game.job.ClearRegionJob;
import de.dakror.vloxlands.game.job.DepositJob;
import de.dakror.vloxlands.game.job.Job;
import de.dakror.vloxlands.game.job.MineJob;
import de.dakror.vloxlands.game.job.PickupJob;
import de.dakror.vloxlands.game.job.WalkJob;
import de.dakror.vloxlands.game.query.PathBundle;
import de.dakror.vloxlands.game.query.Query;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.game.world.World;
import de.dakror.vloxlands.layer.GameLayer;
import de.dakror.vloxlands.util.CurserCommand;
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
		super(x, y, z, "models/creature/humanblend/humanblend.g3db");
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
		else
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
		if (((jobQueue.size > 0 && firstJob().isUsingTool()) || (jobQueue.size > 1 && jobQueue.get(1).isUsingTool())) && toolModelInstance != null) batch.render(toolModelInstance, environment);
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
	public void onVoxelSelection(VoxelSelection vs, boolean lmb, String[] action)
	{
		if ((wasSelected || selected) && !lmb)
		{
			selected = true;
			
			if (action != null)
			{
				/*
				 * if (action[0].equals("Mine")) { if (action[action.length - 1].startsWith("voxel")) { String[] voxels = action[action.length - 1].replace("voxel:", "").trim().split("\\|"); for (String s : voxels) { if (s.trim().length() == 0) continue; Voxel v = Voxel.getForId(Integer.parseInt(s)); Job job = (Job) Class.forName("de.dakror.vloxlands.game.job." + v.getTool().getSimpleName().replace("Tool", "Job")).getConstructor(Human.class, VoxelSelection.class, boolean.class).newInstance(this, vs, !Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)); if (v.getId() == vs.type.getId()) { PathBundle pb = null; boolean setJob = false; if (!carryingItemStack.isNull() && !carryingItemStack.canAdd(new ItemStack(Item.getForId(vs.type.getItemdrop()), 1))) { pb = GameLayer.world.query(new Query(this).searchClass(Warehouse.class).structure(true).transport(carryingItemStack).capacityForTransported(true).node(NodeType.deposit).island(0)); setJob(pb.path, new DepositJob(this, pb.structure, false)); setJob = true; } if (equipCorrectToolForJob(job, !setJob,)) { setJob = true; } if (tool.isNull() || !v.getTool().isAssignableFrom(tool.getItem().getClass())) { pb = GameLayer.world.query(new Query(this).searchClass(Warehouse.class).structure(true).tool(v.getTool()).node(NodeType.pickup).island(0)); if (pb != null) { Job pickup = new PickupJob(this, pb.structure, new ItemStack(pb.structure.getInventory().getAnyItemForToolType(v.getTool()), 1), true, false); if (!setJob) { setJob(pb.path, pickup); setJob = true; } else queueJob(pb.path, pickup); } } try { Path p = AStar.findPath(pb != null ? pb.path.getLast() : getVoxelBelow(), vs.voxelPos.getPos(), this, true); if (setJob) queueJob(p, job); else setJob(p, job); } catch (Exception e) { e.printStackTrace(); } break; } } } }
				 */
				
				GameLayer.instance.activeAction = null;
			}
			else
			{
				Path path = AStar.findPath(getVoxelBelow(), vs.voxelPos.getPos(), this, false);
				if (path != null) setJob(path, null);
			}
		}
	}
	
	@Override
	public void onStructureSelection(Structure structure, boolean lmb, String[] action)
	{
		if (wasSelected && !lmb)
		{
			CurserCommand c = structure.getCommandForEntity(this);
			Vector3 pathStart = getVoxelBelow();
			boolean queue = false;
			
			Job job = null;
			NodeType type = NodeType.target;
			if (c == CurserCommand.DEPOSIT)
			{
				if (structure.isWorking())
				{
					job = new DepositJob(this, structure, false);
					type = NodeType.deposit;
				}
			}
			else if (c == CurserCommand.BUILD && !structure.isBuilt())
			{
				job = new BuildJob(this, structure, false);
				type = NodeType.build;
				
				queue = equipCorrectToolForJob(job, false, pathStart);
			}
			
			Vector3 v = structure.getStructureNode(posCache, type).pos.cpy().add(structure.getVoxelPos());
			Path path = AStar.findPath(pathStart, v, this, type.useGhostTarget);
			if (path != null || job != null)
			{
				if (!queue) setJob(path, job);
				else queueJob(path, job);
			}
		}
	}
	
	@Override
	public void onVoxelRangeSelection(Island island, Vector3 start, Vector3 end, boolean lmb, String[] action)
	{
		if ((wasSelected || selected) && !lmb)
		{
			selected = true;
			
			if (action != null)
			{
				if (action[0].equals("Mine"))
				{
					if (action[action.length - 1].startsWith("clear"))
					{
						if (jobQueue.size > 1 || (jobQueue.size > 0 && !(jobQueue.get(0) instanceof WalkJob))) setJob(null, new ClearRegionJob(this, island, start, end, false));
						else queueJob(null, new ClearRegionJob(this, island, start, end, false)); // TODO: finish clear region action
					}
				}
				
				GameLayer.instance.activeAction = null;
			}
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
			PathBundle pb = null;
			
			if (carryingItemStack.isFull())
			{
				pb = GameLayer.world.query(new Query(this).searchClass(Warehouse.class).structure(true).transport(carryingItemStack).capacityForTransported(true).node(NodeType.deposit).island(0));
				if (pb != null) queueJob(pb.path, new DepositJob(this, pb.structure, false));
				else Gdx.app.error("Human.onJobDone", "Couldn't find a Warehouse to dump stuff");
			}
			if (j.isPersistent())
			{
				Path path = BFS.findClosestVoxel(pb != null ? pb.path.getLast() : getVoxelBelow(), ((MineJob) j).getTarget().type.getId(), this);
				
				if (path != null)
				{
					((MineJob) j).getTarget().voxelPos.set(path.getGhostTarget());
					queueJob(path, null);
				}
				else
				{
					Gdx.app.error("Human.onJobDone", "No more voxels of this type to mine / I am too stupid to find a path to one (more likely)!");
					j.setPersistent(false);
					if (pb == null) pb = GameLayer.world.query(new Query(this).searchClass(Warehouse.class).transport(carryingItemStack).capacityForTransported(true).structure(true).node(NodeType.deposit).island(0));
					if (pb != null) queueJob(pb.path, new DepositJob(this, pb.structure, false));
					else Gdx.app.error("Human.onJobDone", "Couldn't find a Warehouse to dump stuff");
				}
			}
		}
	}
	
	public boolean equipCorrectToolForJob(Job job, boolean queue, Vector3 pathStart)
	{
		if (!job.isUsingTool()) return false;
		
		if (tool.isNull() || !(tool.getItem().getClass().isAssignableFrom(job.getTool())))
		{
			PathBundle pb = GameLayer.world.query(new Query(this).searchClass(Warehouse.class).structure(true).tool(job.getTool()).node(NodeType.pickup).island(0));
			if (pb != null)
			{
				PickupJob pj = new PickupJob(this, pb.structure, new ItemStack(pb.structure.getInventory().getAnyItemForToolType(job.getTool()), 1), true, false);
				if (!queue) setJob(pb.path, pj);
				else queueJob(pb.path, pj);
				
				if (pb.path.getLast() != null) pathStart.set(pb.path.getLast());
				
				return true;
			}
		}
		
		return false;
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
