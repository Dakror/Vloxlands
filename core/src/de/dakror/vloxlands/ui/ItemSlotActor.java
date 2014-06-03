package de.dakror.vloxlands.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.item.Item;
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
		pad(5, 5, 5, 5);
		setItemStack(stack);
	}
	
	private static ImageButtonStyle createStyle(ItemStack stack)
	{
		int size = 48;
		
		Texture tex = Vloxlands.assets.get("img/icons.png", Texture.class);
		TextureRegion region = null;
		if (stack != null) region = new TextureRegion(tex, stack.getItem().getTextureX() * Item.SIZE, stack.getItem().getTextureY() * Item.SIZE, Item.SIZE, Item.SIZE);
		else region = new TextureRegion(tex, 5 * Item.SIZE, Item.SIZE, Item.SIZE, Item.SIZE); // default transparent space
		
		ImageButtonStyle style = new ImageButtonStyle(Vloxlands.skin.get(ButtonStyle.class));
		style.imageUp = new TextureRegionDrawable(region);
		
		style.imageUp.setMinWidth(size);
		style.imageUp.setMinHeight(size);
		style.imageDown = new TextureRegionDrawable(region);
		style.imageDown.setMinWidth(size);
		style.imageDown.setMinHeight(size);
		return style;
	}
	
	public void setItemStack(ItemStack stack)
	{
		this.stack = stack;
		if (this.stack != null) this.stack.slot = this;
		onStackChanged();
	}
	
	public ItemStack getItemStack()
	{
		return stack;
	}
	
	public void onStackChanged()
	{
		createStyle(stack);
	}
}
