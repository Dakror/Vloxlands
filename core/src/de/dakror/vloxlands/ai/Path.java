package de.dakror.vloxlands.ai;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * @author Dakror
 */
public class Path
{
	Array<Vector3> nodes;
	/**
	 * If the target is not walkable (e.g. a embeded resource in the ground) this is the actual target.
	 */
	Vector3 ghostTarget;
	
	int index;
	
	public Path(Array<Vector3> nodes)
	{
		this.nodes = nodes;
		index = 0;
	}
	
	public void pop()
	{
		nodes.pop();
	}
	
	public boolean isLast()
	{
		return index == nodes.size - 1;
	}
	
	public void next()
	{
		index++;
	}
	
	public Vector3 getLast()
	{
		return nodes.peek();
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
	
	public Vector3 getGhostTarget()
	{
		return ghostTarget;
	}
	
	public void setGhostTarget(Vector3 ghostTarget)
	{
		this.ghostTarget = ghostTarget;
	}
}