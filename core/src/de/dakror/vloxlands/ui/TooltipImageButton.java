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
	
	public Tooltip getTooltip()
	{
		return tooltip;
	}
}
