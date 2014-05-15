package de.dakror.vloxlands.game.entity.creature;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.item.ItemStack;

/**
 * @author Dakror
 */
public class Human extends Creature
{
	public static final Vector3 itemTrn = new Vector3(0, 0.2f, -0.3f);
	
	ItemStack carryingItemStack;
	ModelInstance carryingItemModelInstance;
	Matrix4 carryingItemTransform;
	
	public Human(float x, float y, float z)
	{
		super(x, y, z, "models/humanblend/humanblend.g3db");
		name = "Mensch";
		
		speed = 0.025f;
		climbHeight = 1;
	}
	
	public ItemStack getCarryingItemStack()
	{
		return carryingItemStack;
	}
	
	public void setCarryingItemStack(ItemStack carryingItemStack)
	{
		this.carryingItemStack = carryingItemStack;
		if (carryingItemStack == null) carryingItemModelInstance = null;
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
		
		carryingItemTransform.setToRotation(Vector3.Y, 0).translate(posCache);
		carryingItemTransform.rotate(Vector3.Y, rotCache.getYaw()).translate(itemTrn);
	}
	
	@Override
	public void renderAdditional(ModelBatch batch, Environment environment)
	{
		if (carryingItemStack != null)
		{
			if (carryingItemStack.getItem().isResource())
			{
				batch.render(carryingItemModelInstance, environment);
			}
		}
	}
}
