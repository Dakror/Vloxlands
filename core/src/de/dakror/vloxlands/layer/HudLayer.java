/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


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
import de.dakror.vloxlands.ui.IslandResources;
import de.dakror.vloxlands.ui.Minimap;
import de.dakror.vloxlands.ui.PinnableWindow;
import de.dakror.vloxlands.ui.Revolver;
import de.dakror.vloxlands.ui.RevolverSlot;
import de.dakror.vloxlands.util.event.SelectionListener;
import de.dakror.vloxlands.util.event.VoxelSelection;

/**
 * @author Dakror
 */
public class HudLayer extends Layer implements SelectionListener {
	PinnableWindow selectedEntityWindow;
	PinnableWindow selectedStructureWindow;
	
	ShapeRenderer shapeRenderer;
	RevolverSlot selected;
	Revolver actions;
	
	int buttonDown = -1;
	
	final Vector2 dragStart = new Vector2(-1, -1);
	final Vector2 dragEnd = new Vector2(-1, -1);
	
	@Override
	public void show() {
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
		stage.addActor(new IslandResources(stage));
	}
	
	@Override
	public void dispose() {
		super.dispose();
		Game.instance.removeListener(this);
	}
	
	public void addActionsMenu() {
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
		
		s = new RevolverSlot(stage, new Vector2(0, 3), "wood");
		s.getTooltip().set("Wood", "Buildings specialized on working with the Wood resource.");
		actions.addSlot(1, "build", s);
		s = new RevolverSlot(stage, new Vector2(0, 3), "entity:130");
		s.getTooltip().set("Lumberjack", "Chops nearby trees for wooden logs using an axe.");
		actions.addSlot(2, "wood", s);
		s = new RevolverSlot(stage, new Vector2(0, 7), "entity:131");
		s.getTooltip().set("Forester", "Places tree saplings to regrow the forest.\nSaplings take 5 days to fully grow.");
		actions.addSlot(2, "wood", s);
		s = new RevolverSlot(stage, new Vector2(1, 3), "entity:134");
		s.getTooltip().set("Sawmill", "Cuts wooden logs into multiple wooden planks using the energy of a mill.");
		actions.addSlot(2, "wood", s);
		
		s = new RevolverSlot(stage, new Vector2(2, 4), "food");
		s.getTooltip().set("Food", "Buildings that produce all kinds of food.");
		actions.addSlot(1, "build", s);
		s = new RevolverSlot(stage, new Vector2(1, 7), "entity:132");
		s.getTooltip().set("Farm", "Plants wheat and harvests it when grown.\nWheat takes 2 days to fully grow.");
		actions.addSlot(2, "food", s);
		s = new RevolverSlot(stage, new Vector2(0, 4), "entity:33|cont");
		s.getTooltip().set("Wheatfield", "A field of wheat which takes 2 days to fully grow.");
		actions.addSlot(2, "food", s);
		
		s = new RevolverSlot(stage, new Vector2(2, 0), "entity:133");
		s.getTooltip().set("Mine", "Mines the specified ore or stone out of the ground.");
		actions.addSlot(1, "build", s);
		
		selected = new RevolverSlot(stage, new Vector2(5, 1), "selected");
		actions.addSlot(0, null, selected);
		
		stage.addActor(actions);
	}
	
	@Override
	public void onCreatureSelection(final Creature creature, boolean lmb) {
		if (lmb && selectedStructureWindow.setShown(false)) {
			selectedStructureWindow.clearChildren();
			selectedStructureWindow.clearActions();
		}
		
		if (creature == null && lmb && selectedEntityWindow.setShown(false)) {
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
		}
		
		if (lmb && creature != null) {
			boolean jobsWereExpanded = selectedEntityWindow.isVisible() && selectedEntityWindow.getTitle().equals(creature.getName()) && creature instanceof Human && ((Button) selectedEntityWindow.findActor("job")).isChecked();
			
			selectedEntityWindow.setTitle(creature.getName());
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
			selectedEntityWindow.addActor(selectedEntityWindow.getButtonTable());
			selectedEntityWindow.addAction(new Action() {
				@Override
				public boolean act(float delta) {
					selectedEntityWindow.setTitle(creature.getName());
					if (!creature.isVisible()) {
						onCreatureSelection(null, true);
						return true;
					}
					return false;
				}
			});
			
			creature.setUI(selectedEntityWindow, jobsWereExpanded);
			
			selected.getTooltip().setTitle("");
			selected.setIcon(new Vector2(5, 1));
			actions.removeGroup("selected");
			creature.setActions(selected);
			
			selectedEntityWindow.pack();
			selectedEntityWindow.setVisible(true);
			selectedEntityWindow.toFront();
		}
	}
	
	@Override
	public void onVoxelSelection(VoxelSelection vs, boolean lmb) {
		if (lmb && selectedEntityWindow.setShown(false)) {
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
		}
		if (lmb && selectedStructureWindow.setShown(false)) {
			selectedStructureWindow.clearChildren();
			selectedStructureWindow.clearActions();
		}
		
		selected.getTooltip().setTitle("");
		selected.setIcon(new Vector2(5, 1));
		actions.removeGroup("selected");
	}
	
	@Override
	public void onStructureSelection(final Structure structure, boolean lmb) {
		if (lmb && selectedEntityWindow.setShown(false)) {
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
		}
		
		if (structure == null && lmb && selectedStructureWindow.setShown(false)) {
			selectedStructureWindow.clearChildren();
			selectedStructureWindow.clearActions();
		}
		
		if (lmb && structure != null) {
			boolean tasksWereExpanded = selectedStructureWindow.isVisible() && selectedStructureWindow.getTitle().equals(structure.getName()) && ((Button) selectedStructureWindow.findActor("queue")) != null && ((Button) selectedStructureWindow.findActor("queue")).isChecked();
			
			selectedStructureWindow.setTitle(structure.getName());
			selectedStructureWindow.clearChildren();
			selectedStructureWindow.clearActions();
			selectedStructureWindow.addActor(selectedStructureWindow.getButtonTable());
			
			structure.setUI(selectedStructureWindow, tasksWereExpanded);
			
			selected.getTooltip().setTitle("");
			selected.setIcon(new Vector2(5, 1));
			actions.removeGroup("selected");
			structure.setActions(selected);
			
			selectedStructureWindow.pack();
			selectedStructureWindow.setVisible(true);
			selectedStructureWindow.toFront();
		}
	}
	
	@Override
	public void onVoxelRangeSelection(Island island, Vector3 start, Vector3 end, boolean lmb) {}
	
	@Override
	public void onNoSelection(boolean lmb) {
		if (lmb && selectedEntityWindow.setShown(false)) {
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
		}
		if (lmb && selectedStructureWindow.setShown(false)) {
			selectedStructureWindow.clearChildren();
			selectedStructureWindow.clearActions();
		}
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Buttons.LEFT) {
			if (dragEnd.x > -1) {
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
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		buttonDown = button;
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (buttonDown == Buttons.LEFT) {
			if (dragStart.x == -1) {
				dragStart.set(screenX, Gdx.graphics.getHeight() - screenY);
				dragEnd.set(screenX, Gdx.graphics.getHeight() - screenY);
			} else dragEnd.set(screenX, Gdx.graphics.getHeight() - screenY);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void render(float delta) {
		stage.act();
		if (Vloxlands.instance.getActiveLayer() == this || !Vloxlands.instance.getActiveLayer().isModal()) {
			stage.draw();
			if (dragStart.x > -1) {
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
