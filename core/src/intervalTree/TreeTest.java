package intervalTree;

import java.util.List;


/**
 * @author Dakror
 */
public class TreeTest
{
	static IntervalTree<Byte> tree;
	static int maxSize;
	
	public static void main(String[] args)
	{
		tree = new IntervalTree<Byte>();
		int size = 5;
		int types = 5;
		maxSize = size * size * size;
		tree.addInterval(0, maxSize, (byte) 0);
		
		int l = 0;
		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				for (int k = 0; k < size; k++, l++)
				{
					Byte b = (byte) 1;// (Math.random() * types);
					set(l, b);
				}
			}
		}
	}
	
	public static void set(int i, byte b)
	{
		List<Interval<Byte>> list = tree.getIntervals(i);
		if (list.size() > 1) System.out.println("MULTIPLE?");
		
		Interval<Byte> interval = list.get(0);
		if (interval.getData() == b) return; // all good then
		
		long start = interval.getStart();
		long end = interval.getEnd();
		
		if (start == end)
		{
			interval.setData(b);
		}
		else
		{
			Interval<Byte> interval2 = null;
			if (start == i)
			{
				interval.setStart(i + 1);
				if (i > 0)
				{
					System.out.println(i);
					interval2 = tree.getIntervals(i - 1).get(0);
					if (interval2.getData() == b) interval2.setEnd(i);
					else tree.addInterval(i, i, b);
				}
				else tree.addInterval(i, i, b);
			}
			else if (end == i)
			{
				interval.setEnd(i - 1);
				if (i < maxSize)
				{
					interval2 = tree.getIntervals(i + 1).get(0);
					if (interval2.getData() == b) interval2.setStart(i);
					else tree.addInterval(i, i, b);
				}
				else tree.addInterval(i, i, b);
			}
			else
			{
				interval.setEnd(i - 1);
				tree.addInterval(i, i, b);
				tree.addInterval(i + 1, end, interval.getData());
			}
		}
	}
}
