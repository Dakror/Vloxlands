package de.dakror.vloxlands.ai.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.ai.job.WalkJob;
import de.dakror.vloxlands.ai.path.node.AStarNode;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.voxel.Voxel;

/**
 * @author Dakror
 */
public class AStar
{
	static final Comparator<AStarNode> COMPARATOR = new Comparator<AStarNode>()
	{
		@Override
		public int compare(AStarNode o1, AStarNode o2)
		{
			return Float.compare(o1.F, o2.F);
		}
	};
	static ArrayList<AStarNode> openList = new ArrayList<AStarNode>();
	static ArrayList<AStarNode> closedList = new ArrayList<AStarNode>();
	static AStarNode target;
	static Vector3 neighbor;
	static int targetedOthers;
	static boolean takeNeighbor; // because other human goes to target already
	
	public synchronized static Path findPath(Vector3 from, Vector3 to, Creature c, boolean useGhostTarget)
	{
		return findPath(from, to, c, 0, useGhostTarget);
	}
	
	public synchronized static Path findPath(Vector3 from, Vector3 to, Creature c, float maxRange, boolean useGhostTarget)
	{
		if (from == null || to == null) return null;
		
		if (maxRange == 0) maxRange = Math.max(from.dst(to), 1) * 5;
		
		if (!c.getIsland().isSpaceAbove(to.x, to.y, to.z, c.getHeight()) && !useGhostTarget) return null;
		
		takeNeighbor = false;
		targetedOthers = 0;
		for (Entity e : c.getIsland().getEntities())
		{
			if (e instanceof Human && e != c)
			{
				if (((Human) e).firstJob() instanceof WalkJob || ((Human) e).path != null)
				{
					Path p = (WalkJob) ((Human) e).firstJob() instanceof WalkJob ? ((WalkJob) (((Human) e).firstJob())).getPath() : ((Human) e).path;
					if ((p.realTarget != null && p.realTarget.equals(to)) || p.getLast().equals(to))
					{
						takeNeighbor = true;
						targetedOthers++;
					}
				}
			}
		}
		
		openList.clear();
		closedList.clear();
		target = null;
		neighbor = null;
		
		openList.add(new AStarNode(from.x, from.y, from.z, 0, from.dst(to), null));
		
		if (useGhostTarget || takeNeighbor)
		{
			if (from.equals(to)) target = openList.get(0);
			else target = new AStarNode(to.x, to.y, to.z, 1, 0, null);
		}
		AStarNode selected = null;
		AStarNode ghostNode = null;
		while (true)
		{
			if (openList.size() == 0) return null; // no way
			
			Collections.sort(openList, COMPARATOR);
			selected = openList.get(0);
			openList.remove(0);
			while (selected.cantBeNeighborForGhostTarget && openList.size() > 0)
			{
				selected = openList.get(0);
				openList.remove(0);
			}
			
			closedList.add(selected);
			
			if (selected.H == 0 && !useGhostTarget) break;
			
			if ((ghostNode = addNeighbors(selected, from, to, c, maxRange, useGhostTarget)) != null) break;
		}
		
		Array<Vector3> v = new Array<Vector3>();
		while (selected != null)
		{
			v.add(new Vector3(selected.x, selected.y, selected.z));
			selected = (AStarNode) selected.parent;
		}
		
		v.reverse();
		Vector3 firstNode = v.removeIndex(0); // remove start vector
		
		if (neighbor != null && !takeNeighbor) v.add(neighbor);
		
		Path p = new Path(v);
		p.removedFirstNode = firstNode;
		if (takeNeighbor) p.realTarget = to;
		if (ghostNode != null && useGhostTarget) p.setGhostTarget(new Vector3(ghostNode.x, ghostNode.y, ghostNode.z));
		
		return p;
	}
	
	public static AStarNode addNeighbors(AStarNode selected, Vector3 from, Vector3 to, Creature c, float maxRange, boolean useGhostTarget)
	{
		int height = c.getHeight();
		
		byte air = Voxel.get("AIR").getId();
		
		final Vector3 v = new Vector3();
		final BoundingBox b = new BoundingBox();
		final BoundingBox b2 = new BoundingBox();
		final float malus = 0.01f;
		
		for (int x = -1; x < 2; x++)
		{
			for (int z = -1; z < 2; z++)
			{
				for (int y = -1; y < 3; y++)
				{
					if (x != 0 && z != 0 && y != 0) continue;
					if (x == 0 && z == 0 && y == 0) continue;
					
					v.set(selected.x + x, selected.y + y, selected.z + z);
					
					if (!c.getIsland().isTargetable(v.x, v.y, v.z)) continue;
					if (c.getIsland().get(v.x, v.y, v.z) == air && !c.canFly()) continue;
					
					if (from.dst(v) > maxRange || to.dst(v) > maxRange) continue;
					
					float g = c.getIsland().get(v.x, v.y, v.z) == air ? 1.5f : 1;
					
					AStarNode node = new AStarNode(v.x, v.y, v.z, selected.G + g * v.dst(selected.x, selected.y, selected.z), v.dst(to), selected);
					
					if (closedList.contains(node)) continue;
					
					int index = openList.indexOf(node);
					boolean ctn = openList.contains(node);
					
					if (ctn && openList.get(index).G > node.G)
					{
						openList.get(index).G = node.G;
						openList.get(index).parent = selected;
					}
					else if (!ctn)
					{
						boolean free = true;
						
						if (!c.getIsland().isSpaceAbove(v.x, v.y, v.z, height)) free = false;
						
						if (x != 0 && z != 0 && free)
						{
							if (!c.getIsland().isSpaceAbove(selected.x, v.y, v.z, height)) free = false;
							else if (!c.getIsland().isSpaceAbove(v.x, v.y, selected.z, height)) free = false;
						}
						
						if (y != 0)
						{
							if (y < 0 && !c.getIsland().isSpaceAbove(v.x, v.y, v.z, height + 1)) free = false;
							else if (y > 0 && !c.getIsland().isSpaceAbove(selected.x, selected.y, selected.z, height + 1)) free = false;
						}
						
						if (free)
						{
							for (Entity e : c.getIsland().getEntities())
							{
								if (e instanceof Human && e != c)
								{
									if (((Human) e).firstJob() instanceof WalkJob || ((Human) e).path != null)
									{
										Path p = (WalkJob) ((Human) e).firstJob() instanceof WalkJob ? ((WalkJob) (((Human) e).firstJob())).getPath() : ((Human) e).path;
										if (p.getLast().equals(v) || (p.ghostTarget != null && p.ghostTarget.equals(v)))
										{
											free = false;
											break;
										}
									}
								}
								else if (e instanceof Structure)
								{
									e.getWorldBoundingBox(b);
									
									b2.min.set(v).add(c.getIsland().pos).add(malus, 1, malus);
									b2.max.set(b2.min).add(1 - 2 * malus, height, 1 - 2 * malus);
									b2.set(b2.min, b2.max);
									if (b.intersects(b2))
									{
										free = false;
										break;
									}
									if (x != 0 && z != 0 && free)
									{
										b2.min.set(selected.x, v.y, v.z).add(c.getIsland().pos).add(malus, 1, malus);
										b2.max.set(b2.min).add(1 - 2 * malus, height, 1 - 2 * malus);
										b2.set(b2.min, b2.max);
										
										if (b.intersects(b2))
										{
											free = false;
											break;
										}
										
										b2.min.set(v.x, v.y, selected.z).add(c.getIsland().pos).add(malus, 1, malus);
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
						}
						
						if (useGhostTarget || takeNeighbor)
						{
							int cd = chebyshevDistance(to, v) + 1;
							boolean ringFull = targetedOthers % 9 == 0;
							boolean takeThisAsNeighbor = takeNeighbor && (cd == Math.ceil(targetedOthers / 9f) || (ringFull && cd == Math.ceil(targetedOthers / 9f) + 1));
							boolean targetable = v.equals(to) || (from.equals(to) && v.dst(to) < Math.sqrt(3) && free) || takeThisAsNeighbor;
							boolean close = true;
							
							if (x == 0 && z == 0) close = false;
							// if (y == 1) close = false;
							// TODO ^ needed for anything?
							if (targetable && close)
							{
								if (!v.equals(to)) neighbor = v;
								return v.equals(to) || takeThisAsNeighbor ? node : target;
							}
							else if (targetable) node.cantBeNeighborForGhostTarget = true;
						}
						
						if (free && y < 2) openList.add(node);
					}
				}
			}
		}
		
		return null;
	}
	
	public static int chebyshevDistance(Vector3 o1, Vector3 o2)
	{
		return (int) Math.max(Math.abs(o1.x - o2.x), Math.abs(o1.z - o2.z));
	}
}
