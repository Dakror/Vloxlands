package de.dakror.vloxlands.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.util.event.ItemStackListener;

/**
 * @author Dakror
 */
public class ItemSlot extends TooltipImageButton implements ItemStackListener
{
	public static int size = 48;
	ItemStack stack;
	Label amount;
	
	public ItemSlot(Stage stage)
	{
		this(stage, new ItemStack());
	}
	
	public ItemSlot(Stage stage, ItemStack stack)
	{
		super(stage, createStyle(stack));
		amount = new Label("", Vloxlands.skin);
		amount.setFontScale(1.15f);
		amount.setZIndex(1);
		addActor(amount);
		
		setItemStack(stack);
	}
	
	private static ImageButtonStyle createStyle(ItemStack stack)
	{
		Texture tex = Vloxlands.assets.get("img/icons.png", Texture.class);
		TextureRegion region = null;
		if (!stack.isNull()) region = new TextureRegion(tex, stack.getItem().getTextureX() * Item.SIZE, stack.getItem().getTextureY() * Item.SIZE, Item.SIZE, Item.SIZE);
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
		this.stack.addListener(this);
		onStackChanged();
	}
	
	public ItemStack getItemStack()
	{
		return stack;
	}
	
	@Override
	protected void onRemove()
	{
		stack.removeListener(this);
	}
	
	@Override
	public void onStackChanged()
	{
		setStyle(createStyle(stack));
		pad(4);
		if (stack.getAmount() > 1) amount.setText(stack.getAmount() + "");
		else amount.setText("");
		amount.setPosition(getWidth() - amount.getTextBounds().width * 1.15f, 5);
		
		if (!stack.isNull())
		{
			tooltip.setTitle((stack.getAmount() > 1 ? stack.getAmount() + "x " : "") + stack.getItem().getName());
			tooltip.setDescription(stack.getItem().getDescription());
		}
		else tooltip.setTitle("");
	}
}
