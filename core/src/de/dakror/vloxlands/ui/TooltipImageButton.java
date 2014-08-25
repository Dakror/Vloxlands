package de.dakror.vloxlands.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import de.dakror.vloxlands.util.interf.provider.ResourceListProvider;

/**
 * @author Dakror
 */
public class TooltipImageButton extends ImageButton
{
	protected Tooltip tooltip;
	
	public TooltipImageButton(ImageButtonStyle style)
	{
		super(style);
		pad(12);
		tooltip = new Tooltip("", "", this);
	}
	
	public TooltipImageButton(ImageButtonStyle style, ResourceListProvider provider)
	{
		super(style);
		pad(12);
		
		tooltip = new ResourceListTooltip("", "", provider, this);
	}
	
	public Tooltip getTooltip()
	{
		return tooltip;
	}
}
