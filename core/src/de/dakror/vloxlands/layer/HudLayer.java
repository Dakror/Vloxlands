package de.dakror.vloxlands.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.ui.IslandInfo;
import de.dakror.vloxlands.ui.Minimap;
import de.dakror.vloxlands.ui.PinnableWindow;
import de.dakror.vloxlands.ui.Revolver;
import de.dakror.vloxlands.ui.RevolverSlot;
import de.dakror.vloxlands.util.D;
import de.dakror.vloxlands.util.event.SelectionListener;
import de.dakror.vloxlands.util.event.VoxelSelection;

/**
 * @author Dakror
 */
public class HudLayer extends Layer implements SelectionListener
{
	PinnableWindow selectedEntityWindow;
	PinnableWindow selectedStructureWindow;
	
	ShapeRenderer shapeRenderer;
	RevolverSlot selected;
	Revolver actions;
	
	int buttonDown = -1;
	
	final Vector2 dragStart = new Vector2(-1, -1);
	final Vector2 dragEnd = new Vector2(-1, -1);
	
	@Override
	public void show()
	{
		modal = true;
		Game.instance.addListener(this);
		
		stage = new Stage(new ScreenViewport());
		
		shapeRenderer = new ShapeRenderer();
		
		selectedEntityWindow = new PinnableWindow("", Vloxlands.skin);
		selectedEntityWindow.setPosition(Gdx.graphics.getWidth() - selectedEntityWindow.getWidth(), 0);
		selectedEntityWindow.setTitleAlignment(Align.left);
		selectedEntityWindow.setVisible(false);
		stage.addActor(selectedEntityWindow);
		
		selectedStructureWindow = new PinnableWindow("", Vloxlands.skin);
		selectedStructureWindow.setPosition(Gdx.graphics.getWidth() - selectedStructureWindow.getWidth(), 0);
		selectedStructureWindow.setTitleAlignment(Align.left);
		selectedStructureWindow.setVisible(false);
		stage.addActor(selectedStructureWindow);
		
		addActionsMenu();
		
		stage.addActor(new Minimap());
		stage.addActor(new IslandInfo());
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		Game.instance.removeListener(this);
	}
	
	public void addActionsMenu()
	{
		actions = new Revolver();
		actions.setPosition(10, 10);
		
		RevolverSlot s = new RevolverSlot(stage, new Vector2(3, 0), "mine");
		s.getTooltip().set("Mine", "Mine or dig terrain.");
		actions.addSlot(0, null, s);
		s = new RevolverSlot(stage, new Vector2(3, 0), "clear|region");
		s.getTooltip().set("Clear", "Clear a selected region of natural materials.");
		actions.addSlot(1, "mine", s);
		
		s = new RevolverSlot(stage, new Vector2(1, 5), "build");
		s.getTooltip().set("Build", "Build various building and structures.");
		actions.addSlot(0, null, s);
		s = new RevolverSlot(stage, new Vector2(1, 5), "entity:129");
		s.setDisabled(true);
		s.getTooltip().set("Towncenter", "Functions as the central point and warehouse of an island.\nA prerequisite for settling on an island.");
		actions.addSlot(1, "build", s);
		s = new RevolverSlot(stage, new Vector2(0, 3), "entity:130");
		s.getTooltip().set("Lumberjack", "Chops nearby trees for wooden logs using an axe.");
		actions.addSlot(1, "build", s);
		
		if (D.android())
		{
			s = new RevolverSlot(stage, new Vector2(1, 6), "controls");
			s.getTooltip().set("Controls", "Selection modes and controls.");
			actions.addSlot(0, null, s);
			s = new RevolverSlot(stage, new Vector2(2, 6), "rect|android");
			s.getTooltip().set("Rectangle Selection", "Select a group of entities by dragging a rectangle.");
			actions.addSlot(1, "controls", s);
		}
		
		selected = new RevolverSlot(stage, new Vector2(5, 0), "selected");
		actions.addSlot(0, null, selected);
		
		stage.addActor(actions);
	}
	
	@Override
	public void onCreatureSelection(final Creature creature, boolean lmb)
	{
		if (lmb && selectedStructureWindow.setShown(false))
		{
			selectedStructureWindow.clearChildren();
			selectedStructureWindow.clearActions();
		}
		
		if (lmb && selectedEntityWindow.setShown(false) && creature == null)
		{
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
		}
		
		if (lmb && creature != null)
		{
			boolean jobsWereExpanded = selectedEntityWindow.isVisible() && selectedEntityWindow.getTitle().equals(creature.getName()) && creature instanceof Human && ((Button) selectedEntityWindow.findActor("job")).isChecked();
			
			selectedEntityWindow.setTitle(creature.getName());
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
			selectedEntityWindow.addActor(selectedEntityWindow.getButtonTable());
			selectedEntityWindow.addAction(new Action()
			{
				@Override
				public boolean act(float delta)
				{
					selectedEntityWindow.setTitle(creature.getName());
					if (!creature.isVisible())
					{
						onCreatureSelection(null, true);
						return true;
					}
					return false;
				}
			});
			
			creature.setUI(selectedEntityWindow, jobsWereExpanded);
			
			selected.getTooltip().setTitle("");
			selected.setIcon(new Vector2(5, 0));
			actions.removeGroup("selected");
			creature.setActions(selected);
			
			selectedEntityWindow.pack();
			selectedEntityWindow.setVisible(true);
			selectedEntityWindow.toFront();
		}
	}
	
	@Override
	public void onVoxelSelection(VoxelSelection vs, boolean lmb)
	{
		if (lmb && selectedEntityWindow.setShown(false))
		{
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
		}
		if (lmb && selectedStructureWindow.setShown(false))
		{
			selectedStructureWindow.clearChildren();
			selectedStructureWindow.clearActions();
		}
		
		selected.getTooltip().setTitle("");
		selected.setIcon(new Vector2(5, 0));
		actions.removeGroup("selected");
	}
	
	@Override
	public void onStructureSelection(final Structure structure, boolean lmb)
	{
		if (lmb && selectedEntityWindow.setShown(false))
		{
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
		}
		
		if (lmb && selectedStructureWindow.setShown(false) && structure == null)
		{
			selectedStructureWindow.clearChildren();
			selectedStructureWindow.clearActions();
		}
		
		if (lmb && structure != null)
		{
			selectedStructureWindow.setTitle(structure.getName());
			selectedStructureWindow.clearChildren();
			selectedStructureWindow.clearActions();
			selectedStructureWindow.addActor(selectedStructureWindow.getButtonTable());
			
			structure.setUI(selectedStructureWindow);
			
			selected.getTooltip().setTitle("");
			selected.setIcon(new Vector2(5, 0));
			actions.removeGroup("selected");
			structure.setActions(selected);
			
			selectedStructureWindow.pack();
			selectedStructureWindow.setVisible(true);
			selectedStructureWindow.toFront();
		}
	}
	
	@Override
	public void onVoxelRangeSelection(Island island, Vector3 start, Vector3 end, boolean lmb)
	{}
	
	@Override
	public void onNoSelection(boolean lmb)
	{
		if (lmb && selectedEntityWindow.setShown(false))
		{
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
		}
		if (lmb && selectedStructureWindow.setShown(false))
		{
			selectedStructureWindow.clearChildren();
			selectedStructureWindow.clearActions();
		}
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		if (button == Buttons.LEFT)
		{
			if (dragEnd.x > -1)
			{
				float x = Math.min(dragStart.x, dragEnd.x) / Gdx.graphics.getWidth();
				float y = Math.min(dragStart.y, dragEnd.y) / Gdx.graphics.getHeight();
				float width = Math.abs(dragStart.x - dragEnd.x) / Gdx.graphics.getWidth();
				float height = Math.abs(dragStart.y - dragEnd.y) / Gdx.graphics.getHeight();
				
				Game.instance.selectionBox(new Rectangle(x, y, width, height));
				
				Game.instance.activeAction = "";
			}
			dragStart.set(-1, -1);
			dragEnd.set(-1, -1);
		}
		buttonDown = -1;
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		buttonDown = button;
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		if ((!D.android() && buttonDown == Buttons.LEFT) || Game.instance.activeAction.equals("rect|android"))
		{
			if (dragStart.x == -1)
			{
				dragStart.set(screenX, Gdx.graphics.getHeight() - screenY);
				dragEnd.set(screenX, Gdx.graphics.getHeight() - screenY);
			}
			else dragEnd.set(screenX, Gdx.graphics.getHeight() - screenY);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void render(float delta)
	{
		stage.act();
		if (Vloxlands.instance.getActiveLayer() == this || !Vloxlands.instance.getActiveLayer().isModal())
		{
			stage.draw();
			if (dragStart.x > -1)
			{
				shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
				shapeRenderer.begin(ShapeType.Line);
				shapeRenderer.identity();
				shapeRenderer.setColor(Color.WHITE);
				shapeRenderer.rect(Math.min(dragStart.x, dragEnd.x), Math.min(dragStart.y, dragEnd.y), Math.abs(dragStart.x - dragEnd.x), Math.abs(dragStart.y - dragEnd.y));
				shapeRenderer.end();
			}
		}
	}
}
