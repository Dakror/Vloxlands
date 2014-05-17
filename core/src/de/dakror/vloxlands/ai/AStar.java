package de.dakror.vloxlands.ai;

import java.util.Comparator;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.ai.node.AStarNode;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.layer.GameLayer;

/**
 * @author Dakror
 */
public class AStar
{
	public static final Comparator<AStarNode> COMPARATOR = new Comparator<AStarNode>()
	{
		@Override
		public int compare(AStarNode o1, AStarNode o2)
		{
			return Float.compare(o1.F, o2.F);
		}
	};
	public static Array<AStarNode> openList = new Array<AStarNode>();
	public static Array<AStarNode> closedList = new Array<AStarNode>();
	public static Array<Vector3> lastPath;
	
	// TODO: multi island support
	public static Path findPath(Vector3 from, Vector3 to, Creature c, boolean useGhostTarget)
	{
		if (!GameLayer.world.getIslands()[0].isSpaceAbove(to.x, to.y, to.z, c.getHeight()) && !useGhostTarget) return null;
		
		openList.clear();
		closedList.clear();
		
		openList.add(new AStarNode(from.x, from.y, from.z, 0, from.dst(to), null));
		
		AStarNode selected = null;
		AStarNode ghostNode = null;
		while (true)
		{
			if (openList.size == 0) return null; // no way
			
			openList.sort(COMPARATOR);
			selected = openList.get(0);
			openList.removeIndex(0);
			closedList.add(selected);
			
			if (selected.H == 0) break;
			
			if ((ghostNode = addNeighbors(selected, from, to, c)) != null) break;
		}
		
		Array<Vector3> v = new Array<Vector3>();
		while (selected != null)
		{
			v.add(new Vector3(selected.x, selected.y, selected.z));
			selected = (AStarNode) selected.parent;
		}
		
		v.reverse();
		v.removeIndex(0); // remove start vector
		
		lastPath = v;
		
		Path p = new Path(v);
		if (ghostNode != null) p.setGhostTarget(new Vector3(ghostNode.x, ghostNode.y, ghostNode.z));
		return p;
	}
	
	public static AStarNode addNeighbors(AStarNode selected, Vector3 from, Vector3 to, Creature c)
	{
		float maxDistance = from.dst(to);
		int height = c.getHeight();
		
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
					
					if (!GameLayer.world.getIslands()[0].isTargetable(v.x, v.y, v.z)) break;
					if (GameLayer.world.getIslands()[0].get(v.x, v.y, v.z) == air && !c.canFly()) continue;
					
					if (from.dst(v) > maxDistance || to.dst(v) > maxDistance) continue;
					
					float g = GameLayer.world.getIslands()[0].get(v.x, v.y, v.z) == air ? 1.5f : 1;
					
					AStarNode node = new AStarNode(v.x, v.y, v.z, selected.G + g, v.dst(to), selected);
					
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
						
						if (!GameLayer.world.getIslands()[0].isSpaceAbove(v.x, v.y, v.z, height)) free = false;
						
						if (x != 0 && z != 0 && free)
						{
							if (!GameLayer.world.getIslands()[0].isSpaceAbove(selected.x, v.y, v.z, height)) free = false;
							else if (!GameLayer.world.getIslands()[0].isSpaceAbove(v.x, v.y, selected.z, height)) free = false;
						}
						
						if (y != 0)
						{
							if (y < 0 && !GameLayer.world.getIslands()[0].isSpaceAbove(v.x, v.y, v.z, height + 1)) free = false;
							else if (y > 0 && !GameLayer.world.getIslands()[0].isSpaceAbove(selected.x, selected.y, selected.z, height + 1)) free = false;
						}
						
						if (free)
						{
							for (Entity e : GameLayer.world.getIslands()[0].getStructures())
							{
								e.getWorldBoundingBox(b);
								
								b2.min.set(v).add(GameLayer.world.getIslands()[0].pos).add(malus, 1, malus);
								b2.max.set(b2.min).add(1 - 2 * malus, height, 1 - 2 * malus);
								b2.set(b2.min, b2.max);
								if (b.intersects(b2))
								{
									free = false;
									break;
								}
								
								if (x != 0 && z != 0 && free)
								{
									b2.min.set(selected.x, v.y, v.z).add(GameLayer.world.getIslands()[0].pos).add(malus, 1, malus);
									b2.max.set(b2.min).add(1 - 2 * malus, height, 1 - 2 * malus);
									b2.set(b2.min, b2.max);
									
									if (b.intersects(b2))
									{
										free = false;
										break;
									}
									
									b2.min.set(v.x, v.y, selected.z).add(GameLayer.world.getIslands()[0].pos).add(malus, 1, malus);
									b2.max.set(b2.min).add(1 - 2 * malus, height, 1 - 2 * malus);
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
						if (v.equals(to)) return node;
					}
				}
			}
		}
		
		return null;
	}
}
