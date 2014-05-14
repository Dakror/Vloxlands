package de.dakror.vloxlands.ai;

import java.util.Comparator;
import java.util.Iterator;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.structure.Structure;
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
		bodySize.set((int) Math.ceil(bodySize.x), (int) Math.ceil(bodySize.y), (int) Math.ceil(bodySize.z)); // round up size
		
		
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
		v.removeIndex(0); // remove start vector
		
		return new Path(v);
	}
	
	public static void addNeighbors(Node selected, Vector3 to, Vector3 bodySize)
	{
		byte air = Voxel.get("AIR").getId();
		
		final Vector3 v = new Vector3();
		final BoundingBox b = new BoundingBox();
		final BoundingBox b2 = new BoundingBox();
		final float malus = 0.01f;
		
		for (int x = -1; x < 2; x++)
		{
			for (int y = -1; y < 2; y++)
			{
				for (int z = -1; z < 2; z++)
				{
					if (Math.sqrt(x * x + y * y + z * z) == Math.sqrt(3)) continue;
					
					v.set(selected.x + x, selected.y + y, selected.z + z);
					
					if (Vloxlands.world.getIslands()[0].get(v.x, v.y, v.z) == air) continue;
					
					Node node = new Node(v.x, v.y, v.z, selected.G + 1, v.dst(to), selected);
					
					if (closedList.contains(node, false)) continue;
					
					int index = openList.indexOf(node, false);
					boolean ctn = openList.contains(node, false);
					
					if (ctn && openList.get(index).G > node.G)
					{
						openList.get(index).G = node.G;
						openList.get(index).parent = selected;
					}
					else if (!ctn)
					{
						boolean free = true;
						
						if (!isSpaceAbove(v.x, v.y, v.z, bodySize)) free = false;
						
						if (x != 0 && z != 0 && free)
						{
							if (!isSpaceAbove(selected.x, v.y, v.z, bodySize)) free = false;
							else if (!isSpaceAbove(v.x, v.y, selected.z, bodySize)) free = false;
						}
						
						if (free)
						{
							for (Iterator<Entity> iter = Vloxlands.world.getEntities().iterator(); iter.hasNext();)
							{
								Entity e = iter.next();
								
								if (!(e instanceof Structure)) continue;
								
								e.getWorldBoundingBox(b);
								
								b2.min.set(v).add(Vloxlands.world.getIslands()[0].pos).add(malus, 1, malus);
								b2.max.set(b2.min).add(1 - 2 * malus, bodySize.y, 1 - 2 * malus);
								b2.set(b2.min, b2.max);
								if (b.intersects(b2))
								{
									free = false;
									break;
								}
								
								if (x != 0 && z != 0 && free)
								{
									b2.min.set(selected.x, v.y, v.z).add(Vloxlands.world.getIslands()[0].pos).add(malus, 1, malus);
									b2.max.set(b2.min).add(1 - 2 * malus, bodySize.y, 1 - 2 * malus);
									b2.set(b2.min, b2.max);
									
									if (b.intersects(b2))
									{
										free = false;
										break;
									}
									
									b2.min.set(v.x, v.y, selected.z).add(Vloxlands.world.getIslands()[0].pos).add(malus, 1, malus);
									b2.max.set(b2.min).add(1 - 2 * malus, bodySize.y, 1 - 2 * malus);
									b2.set(b2.min, b2.max);
									
									if (b.intersects(b2))
									{
										free = false;
										break;
									}
								}
							}
						}
						
						
						if (free) openList.add(node);
					}
				}
			}
		}
	}
	
	public static boolean isSpaceAbove(float x, float y, float z, Vector3 bodySize)
	{
		byte air = Voxel.get("AIR").getId();
		for (int i = 0; i < bodySize.y; i++)
		{
			byte b = Vloxlands.world.getIslands()[0].get(x, y + i + 1, z);
			if (b != 0 && b != air) return false;
		}
		
		return true;
	}
}
