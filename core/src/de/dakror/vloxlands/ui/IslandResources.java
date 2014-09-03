package de.dakror.vloxlands.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.game.item.Item;

/**
 * @author Dakror
 */
public class IslandResources extends VerticalGroup
{
	public static final int[] displayedResources = { 254, 255, 1, 11, 17 }; // TODO: Let User choose displayedResources via a dialog and store that in either settings or save file
	
	public IslandResources(Stage stage)
	{
		pad(12);
		align(Align.left);
		setBounds(0, 0, 200, 0);
		setOrigin(getX(), getY());
		
		for (int id : displayedResources)
		{
			NonStackingInventoryListItem nsili = new NonStackingInventoryListItem(stage, Item.getForId(id), 0, false);
			addActor(nsili);
		}
		
		addAction(new Action()
		{
			@Override
			public boolean act(float d)
			{
				for (int i = 0; i < displayedResources.length; i++)
				{
					NonStackingInventoryListItem nsili = (NonStackingInventoryListItem) getChildren().get(i);
					nsili.setAmount(Game.instance.activeIsland.availableResources.get((byte) displayedResources[i]));
				}
				return false;
			}
		});
		
		pack();
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		if (getHeight() == 0) return;
		
		setPosition(0, Gdx.graphics.getHeight() - getHeight());
		
		Drawable bg = Vloxlands.skin.getDrawable("paper_container");
		bg.draw(batch, getX(), getY(), getWidth(), getHeight());
		super.draw(batch, parentAlpha);
	}
}
