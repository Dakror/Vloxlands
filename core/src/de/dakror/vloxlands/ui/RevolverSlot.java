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
	public static int size = 48;
	
	public RevolverSlot(Stage stage, Vector2 icon)
	{
		super(stage, createStyle(icon));
	}

	private static ImageButtonStyle createStyle(Vector2 icon)
	{
		Texture tex = Vloxlands.assets.get("img/icons.png", Texture.class);
		TextureRegion region = new TextureRegion(tex, icon.x * Item.SIZE, icon.y * Item.SIZE, Item.SIZE, Item.SIZE);

		ImageButtonStyle style = new ImageButtonStyle();
		
		style.up = Vloxlands.skin.getDrawable("revolverSlot");
		style.up.setMinWidth(size);
		style.up.setMinHeight(size);
		style.down = Vloxlands.skin.getDrawable("revolverSlot");
		style.down.setMinWidth(size);
		style.down.setMinHeight(size);
		style.disabled = Vloxlands.skin.getDrawable("revolverSlot_disabled");
		style.disabled.setMinWidth(size);
		style.disabled.setMinHeight(size);
		style.imageUp = new TextureRegionDrawable(region);
		style.imageUp.setMinWidth(size);
		style.imageUp.setMinHeight(size);
		style.imageDown = new TextureRegionDrawable(region);
		style.imageDown.setMinWidth(size);
		style.imageDown.setMinHeight(size);
		return style;
	}
}
