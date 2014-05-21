package de.dakror.vloxlands.game.item.tool;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.item.Item;

/**
 * @author Dakror
 */
public abstract class Tool extends Item
{
	BoundingBox boundingBox;
	
	@Override
	public void onLoaded()
	{
		ModelInstance mi = new ModelInstance(Vloxlands.assets.get("models/item/" + model, Model.class));
		mi.calculateBoundingBox(boundingBox = new BoundingBox());
	}
	
	public BoundingBox getBoundingBox()
	{
		return boundingBox;
	}
	
	public void transformInHand(Matrix4 transform, Creature c)
	{
		Matrix4 tr = c.modelInstance.getAnimation("walk").nodeAnimations.get(3).node.globalTransform; // right hand bone; currently right underarm tho
		Vector3 v = new Vector3();
		tr.getTranslation(v);
		transform.translate(v).rotate(Vector3.Y, 90).rotate(0, 0, c.getRotationPerpendicular(), 30).translate(0, boundingBox.getDimensions().y + boundingBox.getCenter().y, 0);
	}
}
