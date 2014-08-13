package de.dakror.vloxlands.ai.path;

import java.util.ArrayDeque;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.ai.path.node.BFSNode;
import de.dakror.vloxlands.game.voxel.Voxel;

/**
 * @author Dakror
 */
public class BFS
{
	public static Array<BFSNode> visited = new Array<BFSNode>();
	static ArrayDeque<BFSNode> queue = new ArrayDeque<BFSNode>();
	final static Vector3 start = new Vector3();
	static BFSConfig cfg;
	
	public synchronized static Path findClosestVoxel(Vector3 pos, BFSConfig cfg)
	{
		BFS.cfg = cfg;
		start.set(pos);
		visited.clear();
		queue.clear();
		queue.add(new BFSNode(pos.x, pos.y, pos.z, cfg.creature.getIsland().get(pos.x, pos.y, pos.z), cfg.creature.getIsland().getMeta(pos.x, pos.y, pos.z), null));
		
		while (queue.size() > 0)
		{
			BFSNode v = queue.poll();
			
			boolean done = v.voxel == cfg.voxel;
			if (cfg.meta != 0 && done)
			{
				boolean sameMeta = cfg.meta == v.meta;
				done &= sameMeta != cfg.notMeta;
			}
			if (cfg.neighborMeta != 0 && done)
			{
				done &= checkNeighbors(v);
			}
			
			if (done)
			{
				Path p = AStar.findPath(pos, new Vector3(v.x, v.y, v.z), cfg.creature, cfg.maxRange, true);
				if (p != null) return p;
			}
			
			addNeighbors(v);
		}
		
		return null;
	}
	
	static boolean checkNeighbors(BFSNode node)
	{
		for (int i = -cfg.neighborRangeX; i <= cfg.neighborRangeX; i++)
		{
			for (int j = -cfg.neighborRangeY; j <= cfg.neighborRangeY; j++)
			{
				for (int k = -cfg.neighborRangeZ; k <= cfg.neighborRangeZ; k++)
				{
					if ((cfg.creature.getIsland().getMeta(i + node.x, j + node.y, k + node.z) == cfg.neighborMeta) == cfg.notNeighbor) return false;
				}
			}
		}
		
		return true;
	}
	
	static void addNeighbors(BFSNode selected)
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
					
					
					byte vxl = cfg.creature.getIsland().get(v.x, v.y, v.z);
					
					// don't need parents here
					BFSNode node = new BFSNode(v.x, v.y, v.z, vxl, cfg.creature.getIsland().getMeta(v.x, v.y, v.z), null);
					
					if (queue.contains(node) || visited.contains(node, false)) continue;
					
					if (vxl == air && !cfg.creature.canFly()) continue;
					if (cfg.maxRange > 0 && Math.sqrt((v.x - start.x) * (v.x - start.x) + (v.z - start.z) * (v.z - start.z)) > cfg.maxRange) continue;
					if (!cfg.creature.canFly() && (!cfg.creature.getIsland().hasNeighbors(v.x, v.y - 1, v.z) || !cfg.creature.getIsland().hasNeighbors(v.x, v.y - 2, v.z) || !cfg.creature.getIsland().hasNeighbors(v.x, v.y - 3, v.z))) continue;
					if (!cfg.creature.getIsland().isTargetable(v.x, v.y, v.z)) continue;
					
					queue.add(node);
					visited.add(node);
				}
			}
		}
	}
}
