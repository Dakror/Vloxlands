package de.dakror.vloxlands.ai.job;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.item.tool.ChopTool;
import de.dakror.vloxlands.game.voxel.Voxel;

/**
 * @author Dakror
 */
public class RemoveLeavesJob extends Job {
	/**
	 * according to {@link de.dakror.vloxlands.generate.Beziers#TREE Beziers#TREE}
	 */
	public static final int MAX_TREE_DIAMETER = 5;
	
	final Vector3 treeTrunkBottom = new Vector3();
	int treeHeight;
	Array<Vector3> leaves = new Array<Vector3>();
	
	public RemoveLeavesJob(Human human, Vector3 treeTrunkBottom, int treeHeight, boolean persistent) {
		super(human, "mine" /* chop */, "Harvesting leaves", -1, persistent);
		
		this.treeHeight = treeHeight + 5 /* approximation */;
		this.treeTrunkBottom.set(treeTrunkBottom);
		
		tool = ChopTool.class;
		fetchRegion();
	}
	
	public void fetchRegion() {
		byte b = Voxel.get("LEAVES").getId();
		
		leaves.clear();
		for (int i = 0; i < treeHeight; i++) {
			for (int j = 0; j < MAX_TREE_DIAMETER; j++) {
				for (int k = 0; k < MAX_TREE_DIAMETER; k++) {
					int x = (int) (j - 2 + treeTrunkBottom.x);
					int y = (int) (i + treeTrunkBottom.y);
					int z = (int) (k - 2 + treeTrunkBottom.z);
					byte g = human.getIsland().get(x, y, z);
					if (g == b) leaves.add(new Vector3(x, y, z));
				}
			}
		}
		
		if (leaves.size == 0) done = true;
	}
	
	@Override
	protected void onAnimationFinished() {
		for (int i = 0; i < 5; i++) // remove multiple at a time
		{
			Vector3 v = leaves.removeIndex(MathUtils.random(leaves.size - 1));
			human.getIsland().set(v.x, v.y, v.z, (byte) 0);
			
			if (leaves.size == 0) {
				done = true;
				return;
			}
		}
	}
}
