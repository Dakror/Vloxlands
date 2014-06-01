package de.dakror.vloxlands.layer;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.util.event.EventListener;
import de.dakror.vloxlands.util.event.VoxelSelection;

/**
 * @author Dakror
 */
public abstract class Layer implements Screen, InputProcessor, GestureListener, EventListener
{
	Stage stage;
	
	public Stage getStage()
	{
		return stage;
	}
	
	@Override
	public void resize(int width, int height)
	{}
	
	public void tick(int tick)
	{}
	
	@Override
	public void pause()
	{}
	
	@Override
	public void hide()
	{}
	
	@Override
	public void resume()
	{}
	
	@Override
	public void dispose()
	{
		if (stage != null) stage.dispose();
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button)
	{
		return false;
	}
	
	@Override
	public boolean tap(float x, float y, int count, int button)
	{
		return false;
	}
	
	@Override
	public boolean longPress(float x, float y)
	{
		return false;
	}
	
	@Override
	public boolean fling(float velocityX, float velocityY, int button)
	{
		return false;
	}
	
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY)
	{
		return false;
	}
	
	@Override
	public boolean panStop(float x, float y, int pointer, int button)
	{
		return false;
	}
	
	@Override
	public boolean zoom(float initialDistance, float distance)
	{
		return false;
	}
	
	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2)
	{
		return false;
	}
	
	@Override
	public boolean keyDown(int keycode)
	{
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode)
	{
		return false;
	}
	
	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		return false;
	}
	
	@Override
	public boolean scrolled(int amount)
	{
		return false;
	}
	
	@Override
	public void onVoxelSelection(VoxelSelection vs, boolean lmb)
	{}
	
	@Override
	public void onStructureSelection(Structure structure, boolean lmb)
	{}
	
	@Override
	public void onCreatureSelection(Creature creature, boolean lmb)
	{}
}
