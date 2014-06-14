package de.dakror.vloxlands.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.item.Item;

/**
 * @author Dakror
 */
public class NonStackingInventoryListItem extends TextButton
{
	Tooltip tooltip;
	
	Image image;
	Item item;
	int amount;
	
	public NonStackingInventoryListItem(Stage stage, Item item, int amount)
	{
		super(amount + "x " + item.getName(), Vloxlands.skin);
		
		tooltip = new Tooltip(amount + "x " + item.getName(), item.getDescription(), this);
		stage.addActor(tooltip);
		image = new Image();
		addActor(image);
		
		onChange();
	}
	
	private void onChange()
	{
		tooltip.set(amount + "x " + item.getName(), item.getDescription());
		
		Texture tex = Vloxlands.assets.get("img/icons.png", Texture.class);
		TextureRegion region = new TextureRegion(tex, item.getTextureX() * Item.SIZE, item.getTextureY() * Item.SIZE, Item.SIZE, Item.SIZE);
		image.setDrawable(new TextureRegionDrawable(region));
		image.setSize(24, 24);
	}
	
	@Override
	protected void setStage(Stage stage)
	{
		super.setStage(stage);
		if (stage == null) tooltip.remove();
	}
	
	public Item getItem()
	{
		return item;
	}
	
	public void setItem(Item item)
	{
		this.item = item;
		onChange();
	}
	
	public int getAmount()
	{
		return amount;
	}
	
	public void setAmount(int amount)
	{
		this.amount = amount;
		onChange();
	}
}
