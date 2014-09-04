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
	public static final float DEFAULT_SIZE = 54f;
	public static int SIZE = (int) DEFAULT_SIZE;
	Revolver revolver;
	
	public RevolverSlot(Stage stage, Vector2 icon, String name)
	{
		super(createStyle(icon));
		setName(name);
		pad(12 * (SIZE / DEFAULT_SIZE));
		
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
		pad(12 * (SIZE / DEFAULT_SIZE));
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
		style.up.setMinWidth(SIZE);
		style.up.setMinHeight(SIZE);
		style.down = Vloxlands.skin.getDrawable("revolverSlot");
		style.down.setMinWidth(SIZE);
		style.down.setMinHeight(SIZE);
		style.over = Vloxlands.skin.getDrawable("revolverSlot_over");
		style.over.setMinWidth(SIZE);
		style.over.setMinHeight(SIZE);
		style.checked = Vloxlands.skin.getDrawable("revolverSlot_over");
		style.checked.setMinWidth(SIZE);
		style.checked.setMinHeight(SIZE);
		style.disabled = Vloxlands.skin.getDrawable("revolverSlot_disabled");
		style.disabled.setMinWidth(SIZE);
		style.disabled.setMinHeight(SIZE);
		style.imageUp = new TextureRegionDrawable(region);
		style.imageUp.setMinWidth(SIZE);
		style.imageUp.setMinHeight(SIZE);
		style.imageDown = new TextureRegionDrawable(region);
		style.imageDown.setMinWidth(SIZE);
		style.imageDown.setMinHeight(SIZE);
		return style;
	}
}
