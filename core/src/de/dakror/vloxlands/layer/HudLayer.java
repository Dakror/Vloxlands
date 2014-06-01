package de.dakror.vloxlands.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.ui.HidingClickListener;
import de.dakror.vloxlands.util.event.VoxelSelection;

/**
 * @author Dakror
 */
public class HudLayer extends Layer
{
	Window selectedEntityWindow;
	
	@Override
	public void show()
	{
		stage = new Stage(new ScreenViewport());
		selectedEntityWindow = new Window("", Vloxlands.skin);
		TextButton x = new TextButton("X", Vloxlands.skin);
		x.addListener(new HidingClickListener(selectedEntityWindow));
		selectedEntityWindow.getButtonTable().add(x).height(selectedEntityWindow.getPadTop());
		selectedEntityWindow.setSize(300, 100);
		selectedEntityWindow.setPosition(Gdx.graphics.getWidth() - selectedEntityWindow.getWidth(), 0);
		selectedEntityWindow.defaults().spaceBottom(10);
		selectedEntityWindow.setVisible(false);
		
		stage.addActor(selectedEntityWindow);
	}
	
	@Override
	public void onCreatureSelection(Creature creature, boolean lmb)
	{
		selectedEntityWindow.setVisible(true);
	}
	
	@Override
	public void onVoxelSelection(VoxelSelection vs, boolean lmb)
	{
		selectedEntityWindow.setVisible(false);
	}
	
	@Override
	public void onStructureSelection(Structure structure, boolean lmb)
	{
		selectedEntityWindow.setVisible(false);
	}
	
	@Override
	public void render(float delta)
	{
		stage.act();
		if (Vloxlands.currentGame.getActiveLayer() == this) stage.draw();
	}
}
