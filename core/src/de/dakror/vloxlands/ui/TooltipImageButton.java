package de.dakror.vloxlands.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

/**
 * @author Dakror
 */
public class TooltipImageButton extends ImageButton
{
	protected Tooltip tooltip;
	
	public TooltipImageButton(Stage stage, ImageButtonStyle style)
	{
		super(style);
		
		tooltip = new Tooltip("", "", this);
		stage.addActor(tooltip);
	}
	
	@Override
	protected void setStage(Stage stage)
	{
		super.setStage(stage);
		if (stage == null)
		{
			onRemove();
			tooltip.remove();
		}
	}
	
	public Tooltip getTooltip()
	{
		return tooltip;
	}
	
	protected void onRemove()
	{}
}
