package test;

import com.binarydreamers.trees.IntegerInterval;
import com.binarydreamers.trees.Interval;
import com.binarydreamers.trees.IntervalTree;
import com.binarydreamers.trees.IntervalTree.SearchNearest;

import de.dakror.vloxlands.util.math.VoxelInterval;

/**
 * @author Dakror
 */
public class IntervalTreeTest
{
	static final String TYPES = "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	static IntervalTree<Integer> tree;
	static final VoxelInterval tmp = new VoxelInterval();
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		tree = new IntervalTree<Integer>(IntegerInterval.comparator);
		tree.add(new VoxelInterval((byte) 0, 0, 256 * 256 * 256 - 1));
		
		p();

		set(0, (byte) 2);

		p();
	}

	public static void set(int x, byte b)
	{
		VoxelInterval i = (VoxelInterval) tree.searchNearestElement(tmp.set(b, x, x), SearchNearest.SEARCH_NEAREST_ROUNDED_DOWN);
		VoxelInterval j = null;
		if (i == null) throw new IllegalArgumentException();

		if (i.getType() == b) return;

		tree.remove(i);
		
		if (i.getLower() == i.getUpper())
		{
			i.setType(b);
		}
		else if (x == i.getLower())
		{
			i.setLower(i.getLower() + 1);
			
			j = (VoxelInterval) tree.searchNearestElement(tmp.set(b, x - 1, x - 1));
			if (j != null && j.getType() == b)
			{
				tree.remove(j);
				j.setUpper(x);
				tree.add(j);
			}
			else
			{
				tree.add(new VoxelInterval(b, x, x));
			}
		}
		else if (x == i.getUpper())
		{
			i.setUpper(i.getUpper() + 1);

			j = (VoxelInterval) tree.searchNearestElement(tmp.set(b, x + 1, x + 1));
			if (j != null && j.getType() == b)
			{
				tree.remove(j);
				j.setLower(x);
				tree.add(j);
			}
			else
			{
				tree.add(new VoxelInterval(b, x, x));
			}
		}
		else
		{
			int oldUpper = i.getUpper();
			i.setUpper(x - 1);
			tree.add(new VoxelInterval(b, x, x));
			tree.add(new VoxelInterval(i.getType(), x + 1, oldUpper));
		}

		tree.add(i);
	}

	public static void p()
	{
		for (Interval<Integer> i : tree)
			System.out.print(i + ",");
		System.out.println();
	}


	public static void p2()
	{
		for (Interval<Integer> i : tree)
			System.out.print((i.getUpper() - i.getLower() + 1) + "" + TYPES.charAt(((VoxelInterval) i).getType()));
		System.out.println();
	}
}
