package de.dakror.vloxlands.game.item.tool;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
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
		Matrix4 tr = c.modelInstance.getAnimation("walk").nodeAnimations.get(2).node.getChild("Bone_014", true, false).globalTransform; // right hand
		Vector3 v = new Vector3();
		tr.getTranslation(v);
		Quaternion q = new Quaternion();
		tr.getRotation(q);
		float f = q.getAngleAround(Vector3.Y) - 100;
		transform.translate(v).rotate(Vector3.Y, 90).rotate(0, 0, c.getRotationPerpendicular(), f).translate(0, boundingBox.getDimensions().y / 2 - boundingBox.getCenter().y / 2, -0.02f);
	}
}
