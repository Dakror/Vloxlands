package de.dakror.vloxlands.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.ui.ItemSlot;
import de.dakror.vloxlands.ui.PinnableWindow;
import de.dakror.vloxlands.util.event.SelectionListener;
import de.dakror.vloxlands.util.event.VoxelSelection;

/**
 * @author Dakror
 */
public class HudLayer extends Layer implements SelectionListener
{
	PinnableWindow selectedEntityWindow;
	
	@Override
	public void show()
	{
		modal = true;
		GameLayer.instance.addListener(this);
		
		stage = new Stage(new ScreenViewport());
		selectedEntityWindow = new PinnableWindow("", Vloxlands.skin);
		selectedEntityWindow.setPosition(Gdx.graphics.getWidth() - selectedEntityWindow.getWidth(), 0);
		selectedEntityWindow.setTitleAlignment(Align.left);
		selectedEntityWindow.defaults().spaceBottom(10);
		
		selectedEntityWindow.setVisible(false);
		stage.addActor(selectedEntityWindow);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		GameLayer.instance.removeListener(this);
	}
	
	@Override
	public void onCreatureSelection(Creature creature, boolean lmb)
	{
		selectedEntityWindow.setTitle(creature.getName());
		selectedEntityWindow.clearChildren();
		selectedEntityWindow.addActor(selectedEntityWindow.getButtonTable());
		
		if (creature instanceof Human)
		{
			selectedEntityWindow.row();
			selectedEntityWindow.left().add(new ItemSlot(((Human) creature).getTool()));
			selectedEntityWindow.left().add(new ItemSlot(((Human) creature).getCarryingItemStack()));
			selectedEntityWindow.left().add(new ItemSlot(new ItemStack())); // armor / jetpack
			selectedEntityWindow.left().add(new ItemSlot(new ItemStack())); // armor / jetpack
			selectedEntityWindow.pack();
		}
		
		selectedEntityWindow.setVisible(true);
	}
	
	@Override
	public void onVoxelSelection(VoxelSelection vs, boolean lmb)
	{
		if (selectedEntityWindow.setShown(false)) selectedEntityWindow.clearChildren();
	}
	
	@Override
	public void onStructureSelection(Structure structure, boolean lmb)
	{
		if (selectedEntityWindow.setShown(false)) selectedEntityWindow.clearChildren();
	}
	
	@Override
	public void render(float delta)
	{
		stage.act();
		if (Vloxlands.currentGame.getActiveLayer() == this || !Vloxlands.currentGame.getActiveLayer().isModal()) stage.draw();
	}
}
