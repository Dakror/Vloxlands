package de.dakror.vloxlands.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import de.dakror.vloxlands.util.ResourceListProvider;

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
	}
	
	public TooltipImageButton(Stage stage, ImageButtonStyle style, ResourceListProvider provider)
	{
		super(style);
		
		tooltip = new ResourceListTooltip("", "", provider, this);
	}
	
	public Tooltip getTooltip()
	{
		return tooltip;
	}
}
