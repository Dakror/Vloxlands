package de.dakror.vloxlands.ai;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * @author Dakror
 */
public class Path
{
	Array<Vector3> nodes;
	
	int index;
	
	public Path(Array<Vector3> nodes)
	{
		this.nodes = nodes;
		index = 0;
	}
	
	public void next()
	{
		index++;
	}
	
	public Vector3 get()
	{
		return nodes.get(index);
	}
	
	public boolean isDone()
	{
		return index == nodes.size - 1;
	}
	
	public int size()
	{
		return nodes.size;
	}
}
