package de.dakror.vloxlands.game.entity.creature;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.item.tool.Tool;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.util.event.VoxelSelection;

/**
 * @author Dakror
 */
public class Human extends Creature implements AnimationListener
{
	public static final Vector3 resourceTrn = new Vector3(0, 0.2f, -0.3f);
	
	ItemStack carryingItemStack;
	Item tool;
	ModelInstance carryingItemModelInstance;
	Matrix4 carryingItemTransform;
	ModelInstance toolModelInstance;
	Matrix4 toolTransform;
	
	boolean useToolOnReachTarget;
	VoxelSelection toolTarget;
	
	boolean doneUsingTool;
	boolean usingTool;
	
	public Human(float x, float y, float z)
	{
		super(x, y, z, "models/humanblend/humanblend.g3db");
		name = "Mensch";
		
		speed = 0.025f;
		climbHeight = 1;
	}
	
	public void setTool(Item tool)
	{
		this.tool = tool;
		if (tool == null)
		{
			toolModelInstance = null;
			toolTransform = null;
		}
		else
		{
			toolModelInstance = new ModelInstance(Vloxlands.assets.get("models/item/" + tool.getModel(), Model.class), new Matrix4());
			toolTransform = toolModelInstance.transform;
		}
	}
	
	public ItemStack getCarryingItemStack()
	{
		return carryingItemStack;
	}
	
	public void setCarryingItemStack(ItemStack carryingItemStack)
	{
		this.carryingItemStack = carryingItemStack;
		if (carryingItemStack == null)
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
		
		if (carryingItemStack != null)
		{
			carryingItemTransform.setToRotation(Vector3.Y, 0).translate(posCache);
			carryingItemTransform.rotate(Vector3.Y, rotCache.getYaw());
			carryingItemTransform.translate(resourceTrn);
		}
		
		if (tool != null)
		{
			if (doneUsingTool)
			{
				animationController.animate(null, 0);
				doneUsingTool = false;
			}
			
			toolTransform.setToRotation(Vector3.Y, 0).translate(posCache);
			toolTransform.rotate(Vector3.Y, rotCache.getYaw());
			
			((Tool) tool).transformInHand(toolTransform, this);
			
			// pickaxe animation
			// int t = tick * 2 % 120;
			// float yaw = rotCache.getYawRad();
			// float rot = (float) -Math.abs(Math.max(Math.sin(yaw), Math.cos(yaw)));
			// carryingItemTransform.translate(0, 0.2f, -0.3f).rotate(Vector3.Y, 90).rotate(0, 0, rot, t - 25).translate(0, carryingItemBoundingBox.getDimensions().y, 0);
			// }
		}
	}
	
	@Override
	public void onVoxelSelection(VoxelSelection vs, boolean lmb)
	{
		// if (wasSelected && !lmb)
		// {
		// path = AStar.findPath(getVoxelBelow(), vs.voxel, this);
		//
		// if (path != null && path.size() > 0)
		// {
		// if (tool != null && vs.type.getMining() > 0 && (carryingItemStack == null || (!carryingItemStack.isFull() && carryingItemStack.getItem().getId() == vs.type.getItemdrop())) && Gdx.input.isKeyPressed(Keys.CONTROL_LEFT))
		// {
		// gotoLastPathTarget = false;
		// useToolOnReachTarget = true;
		// toolTarget = vs;
		// }
		// else
		// {
		// gotoLastPathTarget = true;
		// useToolOnReachTarget = false;
		// toolTarget = null;
		// }
		// if (!(path.isLast() && !gotoLastPathTarget)) animationController.animate("walk", -1, 1, null, 0);
		// }
		// else
		// animationController.animate(null, 0);
		// selected = true;
		// }
	}
	
	@Override
	public void onReachTarget()
	{
		super.onReachTarget();
		
		if (useToolOnReachTarget)
		{
			animationController.animate("walk" /* mine */, toolTarget.type.getMining(), this, 0.2f); // WIP
			usingTool = true;
		}
	}
	
	@Override
	public void renderAdditional(ModelBatch batch, Environment environment)
	{
		if (!usingTool && carryingItemStack != null) batch.render(carryingItemModelInstance, environment);
		if (usingTool && tool != null) batch.render(toolModelInstance, environment);
	}
	
	@Override
	public void onEnd(AnimationDesc animation)
	{
		if (usingTool)
		{
			Vloxlands.world.getIslands()[toolTarget.island].set(toolTarget.voxel.x, toolTarget.voxel.y, toolTarget.voxel.z, Voxel.get("AIR").getId());
			
			if (toolTarget.type.getItemdrop() != -128)
			{
				if (carryingItemStack == null) setCarryingItemStack(new ItemStack(Item.getForId(toolTarget.type.getItemdrop()), 1));
				else carryingItemStack.add(1);
			}
			
			usingTool = false;
			doneUsingTool = true;
		}
	}
	
	@Override
	public void onLoop(AnimationDesc animation)
	{}
}
