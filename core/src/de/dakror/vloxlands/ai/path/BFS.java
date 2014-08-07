package de.dakror.vloxlands.ai.path;

import java.util.ArrayDeque;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.ai.path.node.BFSNode;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.voxel.Voxel;

/**
 * @author Dakror
 */
public class BFS
{
	static ArrayDeque<BFSNode> queue = new ArrayDeque<BFSNode>();
	final static Vector3 start = new Vector3();
	static boolean allowLowest;
	
	public static Path findClosestVoxel(Vector3 pos, byte voxel, boolean allowLowest, Creature c)
	{
		return findClosestVoxel(pos, voxel, 0, allowLowest, c);
	}
	
	public static Path findClosestVoxel(Vector3 pos, byte voxel, float maxRange, boolean allowLowest, Creature c)
	{
		BFS.allowLowest = allowLowest;
		start.set(pos);
		queue.clear();
		queue.add(new BFSNode(pos.x, pos.y, pos.z, c.getIsland().get(pos.x, pos.y, pos.z), null));
		
		while (queue.size() > 0)
		{
			BFSNode v = queue.poll();
			
			if (v.voxel == voxel) return AStar.findPath(pos, new Vector3(v.x, v.y, v.z), c, maxRange, true);
			
			addNeighbors(v, voxel, maxRange, c);
		}
		
		return null;
	}
	
	static void addNeighbors(BFSNode selected, byte voxel, float maxRange, Creature c)
	{
		byte air = Voxel.get("AIR").getId();
		
		final Vector3 v = new Vector3();
		
		for (int x = -1; x < 2; x++)
		{
			for (int y = -1; y < 3; y++)
			{
				for (int z = -1; z < 2; z++)
				{
					if (x != 0 && z != 0 && y != 0) continue;
					
					v.set(selected.x + x, selected.y + y, selected.z + z);
					
					if (!c.getIsland().isTargetable(v.x, v.y, v.z)) break;
					
					byte vxl = c.getIsland().get(v.x, v.y, v.z);
					
					if (vxl == air && !c.canFly()) continue;
					if (maxRange > 0 && Math.sqrt((v.x - start.x) * (v.x - start.x) + (v.z - start.z) * (v.z - start.z)) > maxRange) continue;
					
					// don't need parents here
					BFSNode node = new BFSNode(v.x, v.y, v.z, vxl, null);
					
					if (queue.contains(node)) continue;
					
					boolean free = true;
					
					if (vxl == voxel)
					{
						// if (GameLayer.instance.activeIsland.isWrapped(v.x, v.y + 1, v.z, c.getHeight())) free = false;
						// if (!allowLowest && GameLayer.instance.activeIsland.get(v.x, v.y + 1, v.z) == voxel && !GameLayer.instance.activeIsland.isWrapped(v.x, v.y + 1, v.z, c.getHeight())) free = false; // first mine available blocks above
					}
					
					if (free) queue.add(node);
				}
			}
		}
	}
}
