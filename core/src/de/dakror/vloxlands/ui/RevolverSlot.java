package de.dakror.vloxlands.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.item.Item;

/**
 * @author Dakror
 */
public class RevolverSlot extends TooltipImageButton
{
	public static final int SIZE = 54;
	
	public RevolverSlot(Stage stage, Vector2 icon, String name)
	{
		super(stage, createStyle(icon));
		setName(name);
		pad(12);
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
