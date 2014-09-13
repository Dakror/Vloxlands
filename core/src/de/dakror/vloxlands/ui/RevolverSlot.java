package de.dakror.vloxlands.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.ai.task.Task;
import de.dakror.vloxlands.ai.task.Tasks;
import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.util.interf.provider.ResourceListProvider;

/**
 * @author Dakror
 */
public class RevolverSlot extends TooltipImageButton
{
	public static final float SIZE = 54f;
	Revolver revolver;
	
	public RevolverSlot(Stage stage, Vector2 icon, String name)
	{
		super(createStyle(icon));
		setName(name);
		
		if (name.startsWith("entity:"))
		{
			final Entity e = Entity.getForId((byte) Integer.parseInt(name.replace("entity:", "").replace("|cont", "").trim()), 0, 0, 0);
			if (e instanceof ResourceListProvider) tooltip = new ResourceListTooltip("", "", (ResourceListProvider) e, this);
			
			if (e instanceof Structure)
			{
				addAction(new Action()
				{
					@Override
					public boolean act(float delta)
					{
						setDisabled(!Game.instance.activeIsland.availableResources.canSubtract(((Structure) e).getCosts()));
						return false;
					}
				});
			}
		}
		if (name.startsWith("task:"))
		{
			try
			{
				Task t = (Task) Tasks.class.getField(name.replace("task:", "")).get(null);
				tooltip = new ResourceListTooltip("", "", t, this);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		stage.addActor(tooltip);
	}
	
	public void setIcon(Vector2 icon)
	{
		setStyle(createStyle(icon));
	}
	
	public void addSlot(RevolverSlot slot)
	{
		revolver.addSlot(((Integer) getUserObject()) + 1, getName(), slot);
	}
	
	private static ImageButtonStyle createStyle(Vector2 icon)
	{
		Texture tex = Vloxlands.assets.get("img/icons.png", Texture.class);
		TextureRegion region = new TextureRegion(tex, (int) icon.x * Item.SIZE, (int) icon.y * Item.SIZE, Item.SIZE, Item.SIZE);
		
		ImageButtonStyle style = new ImageButtonStyle();
		style.up = Vloxlands.skin.getDrawable("revolverSlot");
		style.down = Vloxlands.skin.getDrawable("revolverSlot");
		style.over = Vloxlands.skin.getDrawable("revolverSlot_over");
		style.checked = Vloxlands.skin.getDrawable("revolverSlot_over");
		style.disabled = Vloxlands.skin.getDrawable("revolverSlot_disabled");
		style.imageUp = new TextureRegionDrawable(region);
		style.imageUp.setMinWidth(SIZE - 24);
		style.imageUp.setMinHeight(SIZE - 24);
		style.imageDown = new TextureRegionDrawable(region);
		style.imageDown.setMinWidth(SIZE - 24);
		style.imageDown.setMinHeight(SIZE - 24);
		
		return style;
	}
}
