package de.dakror.vloxlands.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.item.Item;

/**
 * @author Dakror
 */
public class NonStackingInventoryListItem extends HorizontalGroup
{
	Tooltip tooltip;
	Label label;
	Image image;
	Item item;
	int amount;
	
	public NonStackingInventoryListItem(Stage stage, Item item, int amount)
	{
		setName((item.getId() + 128) + "");
		this.item = item;
		this.amount = amount;
		
		tooltip = new Tooltip(amount + "x " + item.getName(), item.getDescription(), this);
		stage.addActor(tooltip);
		image = new Image();
		addActor(image);
		
		label = new Label(amount + "x " + item.getName(), Vloxlands.skin);
		addActor(label);
		
		onChange();
	}
	
	public void setAmount(int amount)
	{
		this.amount = amount;
		onChange();
	}
	
	@Override
	public float getHeight()
	{
		if (!isVisible()) return 0;
		return super.getHeight();
	}
	
	@Override
	public float getPrefHeight()
	{
		if (!isVisible()) return 0;
		return super.getPrefHeight();
	}
	
	@Override
	public float getWidth()
	{
		return 200;
	}
	
	private void onChange()
	{
		tooltip.set(amount + "x " + item.getName(), item.getDescription());
		label.setText(amount + "x " + item.getName());
		Texture tex = Vloxlands.assets.get("img/icons.png", Texture.class);
		TextureRegion region = new TextureRegion(tex, item.getTextureX() * Item.SIZE, item.getTextureY() * Item.SIZE, Item.SIZE, Item.SIZE);
		image.setDrawable(new TextureRegionDrawable(region));
		image.setSize(24, 24);
		
		setVisible(amount > 0);
	}
}
