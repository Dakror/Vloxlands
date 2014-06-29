package test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.badlogic.gdx.math.MathUtils;

import de.dakror.vloxlands.util.Compressor;
import de.dakror.vloxlands.util.math.Interval;
import de.dakror.vloxlands.util.math.SplayTree;

/**
 * @author Dakror
 */
public class IntervalTreeTest
{
	static SplayTree<Interval> tree;
	static final Interval tmp = new Interval();

	static float ab = 0;
	static float ac = 0;

	static final String TYPES = "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		for (int ind = 0; ind < 20; ind++)
		{
			tree = SplayTree.create();
			
			tree.insert(new Interval((byte) 0, 0, 256 * 256 * 256));
			
			int muts = MathUtils.random(0, 1024);
			System.out.println(muts);
			for (int i = 0; i < muts; i++)
			{
				int x = MathUtils.random(0, 256 * 256 * 256 - 1);
				byte b = (byte) MathUtils.random(TYPES.length() - 1);
				set(x, b);
			}
			
			b();
		}
		
		System.out.println(ab + ", " + ac);
	}
	
	public static void set(int x, byte b)
	{
		Interval i = search(x);
		Interval j = null;
		if (i == null) throw new IllegalArgumentException();
		
		if (i.getType() == b) return;
		
		if (i.getLength() == 0)
		{
			i.setType(b);
		}
		else if (i.getStart() == x)
		{
			i.setStart(x + 1);
			i.shrink();
			
			j = search(x - 1);
			if (j != null && j.getType() == b) j.extend();
			else tree.insert(new Interval(b, x, 0));
		}
		else if (i.getStart() + i.getLength() == x)
		{
			i.shrink();
			
			j = search(x + 1);
			if (j != null && j.getType() == b)
			{
				j.setStart(j.getStart() - 1);
				j.extend();
			}
			else tree.insert(new Interval(b, x, 0));
		}
		else
		{
			int length = i.getLength();
			i.setLength(x - i.getStart() - 1);
			length -= x - i.getStart();
			tree.insert(new Interval(b, x, 0));
			tree.insert(new Interval(i.getType(), x + 1, length - 1));
		}
	}
	
	public static byte get(int x)
	{
		Interval i = search(x);
		if (i == null) throw new IllegalArgumentException();
		return i.getType();
	}
	
	public static Interval search(int x)
	{
		for (int i = x; i > -1; i--)
		{
			tmp.set((byte) -1, i, 1);
			if (tree.contains(tmp)) return tree.find(tmp);
		}
		
		return null;
	}
	
	public static void p()
	{
		for (Interval i : tree)
			System.out.print(i + ",");
		System.out.println();
	}
	
	public static void p2()
	{
		for (Interval i : tree)
			System.out.print((i.getLength() + 1) + "" + TYPES.charAt(i.getType()));
		System.out.println();
	}
	
	public static void b() throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (Interval i : tree)
			i.save(baos);
		
		byte[] b = Compressor.compress(baos.toByteArray());
		byte[] c = Compressor.compressRow(baos.toByteArray());

		float abb = b.length / (float) baos.size() * 100f;
		float acc = c.length / (float) baos.size() * 100f;



		if (ab == 0) ab = abb;
		else ab = (ab + abb) / 2f;

		if (ac == 0) ac = acc;
		else ac = (ac + acc) / 2f;

		// System.out.println(abb + ", " + acc);
	}
}
