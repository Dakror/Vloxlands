package de.dakror.vloxlands.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.tablelayout.Cell;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.job.IdleJob;
import de.dakror.vloxlands.game.job.Job;
import de.dakror.vloxlands.ui.ItemSlot;
import de.dakror.vloxlands.ui.PinnableWindow;
import de.dakror.vloxlands.ui.Tooltip;
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
	public void onCreatureSelection(final Creature creature, boolean lmb)
	{
		selectedEntityWindow.setTitle(creature.getName());
		selectedEntityWindow.clearChildren();
		selectedEntityWindow.clearActions();
		selectedEntityWindow.addActor(selectedEntityWindow.getButtonTable());
		
		if (creature instanceof Human)
		{
			selectedEntityWindow.row().pad(0).colspan(4).width(220);
			final List<Job> selectedEntityJobs = new List<Job>(Vloxlands.skin);
			selectedEntityJobs.setItems(new IdleJob((Human) creature));
			selectedEntityWindow.addAction(new Action()
			{
				@Override
				public boolean act(float delta)
				{
					if (((Human) creature).getJobQueue().size == 0 && selectedEntityJobs.getItems().get(0) instanceof IdleJob) return false;
					
					if (((Human) creature).getJobQueue().size != selectedEntityJobs.getItems().size || selectedEntityJobs.getItems().get(0) instanceof IdleJob)
					{
						if (((Human) creature).getJobQueue().size > 0) selectedEntityJobs.setItems(((Human) creature).getJobQueue());
						else selectedEntityJobs.setItems(new IdleJob((Human) creature));
						selectedEntityJobs.getSelection().setDisabled(true);
						selectedEntityJobs.setSelectedIndex(-1);
						selectedEntityWindow.pack();
					}
					
					return false;
				}
			});
			selectedEntityJobs.getSelection().setDisabled(true);
			selectedEntityJobs.setSelectedIndex(-1);
			ScrollPane jobsWrap = new ScrollPane(selectedEntityJobs, Vloxlands.skin);
			jobsWrap.setScrollbarsOnTop(false);
			jobsWrap.setFadeScrollBars(false);
			final Cell<?> cell = selectedEntityWindow.add(jobsWrap).maxHeight(100).ignore();
			
			selectedEntityWindow.row();
			selectedEntityWindow.left().add(new ItemSlot(stage, ((Human) creature).getTool()));
			ItemSlot slot = new ItemSlot(stage, ((Human) creature).getCarryingItemStack());
			selectedEntityWindow.add(slot);
			selectedEntityWindow.add(new ItemSlot(stage, new ItemStack())); // armor / jetpack
			
			ImageButtonStyle style = new ImageButtonStyle(Vloxlands.skin.get(ButtonStyle.class));
			style.imageUp = Vloxlands.skin.getDrawable("gears");
			style.imageUp.setMinWidth(ItemSlot.size);
			style.imageUp.setMinHeight(ItemSlot.size);
			style.imageDown = Vloxlands.skin.getDrawable("gears");
			style.imageDown.setMinWidth(ItemSlot.size);
			style.imageDown.setMinHeight(ItemSlot.size);
			final ImageButton job = new ImageButton(style);
			job.addListener(new ClickListener()
			{
				@Override
				public void clicked(InputEvent event, float x, float y)
				{
					cell.ignore(!cell.getIgnore());
					selectedEntityWindow.invalidateHierarchy();
					selectedEntityWindow.pack();
				}
			});
			job.pad(4);
			selectedEntityWindow.add(job);
			stage.addActor(new Tooltip("Job Queue", "Toggle Job Queue display", job));
			
			selectedEntityWindow.pack();
		}
		
		selectedEntityWindow.setVisible(true);
	}
	
	@Override
	public void onVoxelSelection(VoxelSelection vs, boolean lmb)
	{
		if (selectedEntityWindow.setShown(false))
		{
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
		}
	}
	
	@Override
	public void onStructureSelection(Structure structure, boolean lmb)
	{
		if (selectedEntityWindow.setShown(false))
		{
			selectedEntityWindow.clearChildren();
			selectedEntityWindow.clearActions();
		}
	}
	
	@Override
	public void render(float delta)
	{
		stage.act();
		if (Vloxlands.currentGame.getActiveLayer() == this || !Vloxlands.currentGame.getActiveLayer().isModal()) stage.draw();
	}
}
