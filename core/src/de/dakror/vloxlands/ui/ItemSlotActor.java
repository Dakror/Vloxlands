package de.dakror.vloxlands.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import de.dakror.vloxlands.game.item.ItemStack;

/**
 * @author Dakror
 */
public class ItemSlotActor extends ImageButton
{
	ItemStack stack;
	
	public ItemSlotActor()
	{
		this(null);
	}
	
	public ItemSlotActor(ItemStack stack)
	{
		super(createStyle(stack));
		
		this.stack = stack;
	}
	
	private static ImageButtonStyle createStyle(ItemStack stack)
	{
		return null;
		// TextureAtlas icons = LibgdxUtils.assets.get("icons/icons.atlas", TextureAtlas.class);
		// TextureRegion image;
		// if (slot.getItem() != null) {
		// image = icons.findRegion(slot.getItem().getTextureRegion());
		// } else {
		// // we have a special "empty" region in our atlas file, which is just black
		// image = icons.findRegion("nothing");
		// }
		// ImageButtonStyle style = new ImageButtonStyle(skin.get(ButtonStyle.class));
		// style.imageUp = new TextureRegionDrawable(image);
		// style.imageDown = new TextureRegionDrawable(image);
		//
		// return style;
	}
}
