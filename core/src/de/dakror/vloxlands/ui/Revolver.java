package de.dakror.vloxlands.ui;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import de.dakror.vloxlands.game.Game;

/**
 * @author Dakror
 */
public class Revolver extends Group
{
	public Revolver()
	{
		setTransform(true);
	}
	
	public void addSlot(int level, String parent, final RevolverSlot slot)
	{
		final Group group = ensureCapacity(level, parent);
		
		int amount = group.getChildren().size;
		
		float radius = getRadius(level);
		float degrees = getDegrees(amount, level);
		
		slot.revolver = this;
		slot.setUserObject(level);
		slot.addListener(new InputListener()
		{
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				if (slot.isDisabled()) return false;
				if (button != Buttons.LEFT) return false;
				
				for (Actor a : group.getChildren())
					((ImageButton) a).setChecked(false);
				
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				if (slot.isDisabled()) return;
				if (button != Buttons.LEFT) return;
				
				Actor g = null;
				for (Actor a : getChildren())
				{
					if (a.getName().equals(slot.getName()))
					{
						g = a;
						break;
					}
				}
				
				int l = g != null ? (Integer) g.getUserObject() : (Integer) slot.getUserObject() + 1;
				
				for (Actor a : getChildren())
				{
					if ((Integer) a.getUserObject() < l && !a.isVisible()) continue;
					a.setVisible((Integer) a.getUserObject() < l);
					if (!a.isVisible())
					{
						for (Actor b : ((Group) a).getChildren())
							((Button) b).setChecked(false);
					}
				}
				
				if (g != null) g.setVisible(true);
				if (g == null)
				{
					Game.instance.action(slot.getName());
				}
				else Game.instance.activeAction = "";
				
				slot.setChecked(false);
			}
		});
		slot.setPosition(-MathUtils.cosDeg(degrees + 90) * radius, MathUtils.sinDeg(degrees + 90) * radius);
		group.addActor(slot);
	}
	
	public float getRadius(int level)
	{
		return (RevolverSlot.SIZE + 5) * (level + 2);
	}
	
	public float getDegrees(int slots, int level)
	{
		float radius = getRadius(level);
		float slotRadius = RevolverSlot.SIZE / 2f + level * 4 + 12 * (RevolverSlot.SIZE / RevolverSlot.DEFAULT_SIZE);
		float degreesPerSlot = (float) Math.toDegrees(Math.asin(slotRadius / (radius - slotRadius)));
		
		return slots * degreesPerSlot;
	}
	
	private Group ensureCapacity(int level, String parent)
	{
		if (parent != null)
		{
			for (Actor a : getChildren())
				if (a.getName().equals(parent)) return (Group) a;
		}
		
		while (getChildren().size <= level || parent != null)
		{
			Group g = new Group();
			g.setName(parent == null ? "" : parent);
			g.setUserObject(level);
			g.setVisible(getChildren().size == 0);
			addActor(g);
			
			if (parent != null) return g;
		}
		
		return (Group) getChildren().get(level);
	}
	
	public void removeGroup(String parent)
	{
		Actor a = findActor(parent);
		if (!(a instanceof RevolverSlot)) a.remove();
	}
}
