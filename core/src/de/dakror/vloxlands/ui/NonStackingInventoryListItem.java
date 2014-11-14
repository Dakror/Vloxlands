package de.dakror.vloxlands.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.util.math.MathHelper;

/**
 * @author Dakror
 */
public class NonStackingInventoryListItem extends Table {
	Tooltip tooltip;
	Label label;
	Image image;
	Item item;
	int amount;
	boolean hideOnZero;
	boolean showName;
	boolean format = true;
	
	public NonStackingInventoryListItem(Stage stage, Item item, int amount) {
		this(stage, item, amount, true);
	}
	
	public NonStackingInventoryListItem(Stage stage, Item item, int amount, boolean hideOnZero) {
		this(stage, item, amount, true, hideOnZero);
	}
	
	public NonStackingInventoryListItem(Stage stage, Item item, int amount, boolean format, boolean hideOnZero) {
		this(stage, item, amount, format, hideOnZero, true);
	}
	
	public NonStackingInventoryListItem(Stage stage, Item item, int amount, boolean hideOnZero, boolean format, boolean showName) {
		setWidth(200);
		setName((item.getId() + 128) + "");
		this.hideOnZero = hideOnZero;
		this.format = format;
		this.showName = showName;
		this.item = item;
		this.amount = amount;
		left();
		row();
		
		tooltip = new Tooltip(amount + " " + item.getName(), item.getDescription(), this);
		stage.addActor(tooltip);
		image = new Image();
		add(image).size(24, 24);
		
		label = new Label(amount + " " + item.getName(), Vloxlands.skin);
		if (!showName) {
			label.setAlignment(Align.right);
			add(label).right().expandX();
		} else add(label);
		
		onChange();
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
		onChange();
	}
	
	@Override
	public float getHeight() {
		if (!isVisible()) return 0;
		return super.getHeight();
	}
	
	@Override
	public float getPrefHeight() {
		if (!isVisible()) return 0;
		return super.getPrefHeight();
	}
	
	@Override
	public float getPrefWidth() {
		return getWidth();
	}
	
	private void onChange() {
		String amount = format ? MathHelper.formatNumber(this.amount, 0, 1000) : this.amount + "";
		
		tooltip.set(amount + " " + item.getName(), item.getDescription());
		label.setText(amount + (showName ? " " + item.getName() : ""));
		Texture tex = Vloxlands.assets.get("img/icons.png", Texture.class);
		TextureRegion region = new TextureRegion(tex, item.getIconX() * Item.SIZE, item.getIconY() * Item.SIZE, Item.SIZE, Item.SIZE);
		image.setDrawable(new TextureRegionDrawable(region));
		image.setSize(24, 24);
		
		if (hideOnZero) setVisible(this.amount > 0);
	}
}
