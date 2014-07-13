package de.dakror.vloxlands.ui;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import de.dakror.vloxlands.layer.GameLayer;

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

				int l = g != null ? (int) g.getUserObject() : (int) slot.getUserObject() + 1;
				
				for (Actor a : getChildren())
				{
					if ((int) a.getUserObject() < l && !a.isVisible()) continue;
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
					String action = "";
					Actor p = slot;
					while (p.getName() != null)
					{
						action = p.getName() + (action.length() > 0 ? "-" + action : "");
						if (p instanceof RevolverSlot)
						{
							p = p.getParent();
						}
						else
						{
							for (Actor a : getChildren())
							{
								if ((int) a.getUserObject() == (int) p.getUserObject() - 1)
								{
									boolean done = false;
									
									for (Actor b : ((Group) a).getChildren())
									{
										if (b.getName().equals(p.getName()))
										{
											done = true;
											p = b.getParent();
											break;
										}
									}

									if (done) break;
								}
							}
						}

						if ((int) p.getUserObject() == 0) break;
					}

					GameLayer.instance.activeAction = action.split("-");
				}
				else GameLayer.instance.activeAction = null;

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
		float slotRadius = RevolverSlot.SIZE / 2f + level * 4 + 12;
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
}
