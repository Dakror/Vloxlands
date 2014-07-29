package de.dakror.vloxlands.ai;

import java.util.ArrayDeque;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.ai.node.BFSNode;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.layer.GameLayer;

/**
 * @author Dakror
 */
public class BFS
{
	public static ArrayDeque<BFSNode> queue = new ArrayDeque<BFSNode>();
	public static BFSNode lastTarget;
	
	// TODO multi island support
	public static Path findClosestVoxel(Vector3 pos, byte voxel, Creature c)
	{
		queue.clear();
		queue.add(new BFSNode(pos.x, pos.y, pos.z, GameLayer.instance.activeIsland.get(pos.x, pos.y, pos.z), null));
		
		while (queue.size() > 0)
		{
			BFSNode v = queue.poll();
			
			if (v.voxel == voxel)
			{
				lastTarget = v;
				return AStar.findPath(pos, new Vector3(v.x, v.y, v.z), c, true);
			}
			
			addNeighbors(v, voxel, c);
		}
		
		Gdx.app.log("BFS.findClosestVoxel", "null");
		return null;
	}
	
	public static void addNeighbors(BFSNode selected, byte voxel, Creature c)
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
					
					if (!GameLayer.instance.activeIsland.isTargetable(v.x, v.y, v.z)) break;
					
					byte vxl = GameLayer.instance.activeIsland.get(v.x, v.y, v.z);
					if (vxl == air && !c.canFly()) continue;
					
					// don't need parents here
					BFSNode node = new BFSNode(v.x, v.y, v.z, vxl, null);
					
					if (queue.contains(node)) continue;
					
					boolean free = true;
					
					if (vxl == voxel)
					{
						if (GameLayer.instance.activeIsland.isWrapped(v.x, v.y + 1, v.z, c.getHeight())) free = false;
						if (GameLayer.instance.activeIsland.get(v.x, v.y + 1, v.z) == voxel && !GameLayer.instance.activeIsland.isWrapped(v.x, v.y + 1, v.z, c.getHeight())) free = false; // first mine available blocks above
					}
					
					if (free) queue.add(node);
				}
			}
		}
	}
}
