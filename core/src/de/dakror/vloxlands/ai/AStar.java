package de.dakror.vloxlands.ai;

import java.util.Comparator;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.voxel.Voxel;

/**
 * @author Dakror
 */
public class AStar
{
	public static final Comparator<Node> COMPARATOR = new Comparator<Node>()
	{
		@Override
		public int compare(Node o1, Node o2)
		{
			return Float.compare(o1.F, o2.F);
		}
	};
	static Array<Node> openList = new Array<Node>();
	static Array<Node> closedList = new Array<Node>();
	
	public static Path findPath(Vector3 from, Vector3 to, Vector3 bodySize)
	{
		openList.clear();
		closedList.clear();
		
		openList.add(new Node(from.x, from.y, from.z, 0, from.dst(to), null));
		
		Node selected = null;
		while (true)
		{
			if (openList.size == 0) return null; // no way
			
			openList.sort(COMPARATOR);
			selected = openList.get(0);
			openList.removeIndex(0);
			closedList.add(selected);
			
			if (selected.H == 0) break;
			
			addNeighbors(selected, to, bodySize);
		}
		
		Array<Vector3> v = new Array<Vector3>();
		while (selected != null)
		{
			v.add(new Vector3(selected.x, selected.y, selected.z));
			selected = selected.parent;
		}
		
		v.reverse();
		// v.removeIndex(0); // remove start vector
		
		return new Path(v);
	}
	
	public static void addNeighbors(Node selected, Vector3 to, Vector3 bodySize)
	{
		byte air = Voxel.get("AIR").getId();
		
		Vector3 v = new Vector3();
		
		for (int x = -1; x < 2; x++)
		{
			for (int y = -1; y < 2; y++)
			{
				for (int z = -1; z < 2; z++)
				{
					if (Math.sqrt(x * x + y * y + z * z) != 1) continue;
					
					v.set(selected.x + x, selected.y + y, selected.z + z);
					
					if (Vloxlands.world.getIslands()[0].get(v.x, v.y, v.z) == air) continue;
					
					Node node = new Node(v.x, v.y, v.z, selected.G + 1, v.dst(to), selected);
					
					if (closedList.contains(node, false)) continue;
					
					int i = openList.indexOf(node, false);
					boolean ctn = openList.contains(node, false);
					
					if (ctn && openList.get(i).G > node.G)
					{
						openList.get(i).G = node.G;
						openList.get(i).parent = selected;
					}
					else if (!ctn)
					{
						// TODO check if model has enough space above this voxel to stand on it.
						openList.add(node);
					}
				}
			}
		}
	}
}
