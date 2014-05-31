package de.dakror.vloxlands.layer;

import de.dakror.vloxlands.Vloxlands;

/**
 * @author Dakror
 */
public class HudLayer extends Layer
{
	@Override
	public void show()
	{}
	
	@Override
	public void render(float delta)
	{
		stage.act();
		if (Vloxlands.currentGame.getActiveLayer() == this) stage.draw();
	}
}
