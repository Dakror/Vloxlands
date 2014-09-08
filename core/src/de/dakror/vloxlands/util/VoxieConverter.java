package de.dakror.vloxlands.util;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.math.Vector3;

/**
 * @author Dakror
 */
public class VoxieConverter
{
	static class ReferencePoint
	{
		String name;
		int x, y, z;
		
		ReferencePoint(String name, int x, int y, int z)
		{
			this.name = name;
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
	static class Face
	{
		Vector3 tl, br, bl, tr;
		int direction;
	}
	
	static class Block
	{
		Face[] faces = new Face[Direction.values().length];
		int color;
	}
	
	public static void main(String[] args) throws IOException
	{
		if (args.length == 0) throw new InvalidParameterException("no file specified");
		
		File src = new File(args[0]);
		if (!src.getName().endsWith(".vxi")) throw new InvalidParameterException("given file is no .vxi file");
		
		byte[] fileData = new byte[(int) src.length()];
		
		D.p("Parsing input file...");
		DataInputStream dis = new DataInputStream(new FileInputStream(src));
		dis.readFully(fileData);
		dis.close();
		
		ByteBuffer bb = ByteBuffer.wrap(fileData);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		int width = bb.getInt();
		int height = bb.getInt();
		int depth = bb.getInt();
		
		int offsetX = bb.getInt();
		int offsetY = bb.getInt();
		int offsetZ = bb.getInt();
		
		int[] data = new int[width * height * depth];
		for (int i = 0; i < data.length; i++)
			data[i] = bb.get() & 0xff;
		
		Color[] colors = new Color[256];
		for (int i = 0; i < colors.length; i++)
			colors[i] = new Color(bb.get() & 0xff, bb.get() & 0xff, bb.get() & 0xff);
		
		int refPointCount = bb.get() & 0xff;
		ReferencePoint[] referencePoints = new ReferencePoint[refPointCount];
		
		for (int i = 0; i < referencePoints.length; i++)
		{
			StringBuffer sb = new StringBuffer();
			byte lastRead = 0;
			while ((lastRead = (bb.get())) != 0)
				sb.append((char) lastRead);
			
			int x = bb.getInt();
			int y = bb.getInt();
			int z = bb.getInt();
			
			referencePoints[i] = new ReferencePoint(sb.toString(), x, y, z);
		}
		
		
		
		
		String nl = "\r\n";
		float blockSize = 0.25f;
		
		
		
		
		D.p("Writing materials...");
		BufferedWriter mtl = new BufferedWriter(new FileWriter(new File(src.getParentFile(), src.getName().replace(".vxi", ".mtl"))));
		for (int i = 0; i < colors.length - 1 /* don't need 255 */; i++)
		{
			Color c = colors[i];
			if ((c.getRed() != 0 && c.getGreen() != 0 && c.getBlue() != 0 && c.getAlpha() != 0))
			{
				mtl.write("newmtl mtl" + i + nl);
				mtl.write("Ka 1.000 1.000 1.000" + nl);
				mtl.write("Kd " + (c.getRed() / 255f) + " " + (c.getGreen() / 255f) + " " + (c.getGreen() / 255f) + nl);
				mtl.write("d " + (c.getAlpha() / 255f) + nl);
				mtl.write("Tr " + (c.getAlpha() / 255f) + nl);
				mtl.write("illum 2" + nl + nl);
			}
		}
		mtl.close();
		
		BufferedWriter obj = new BufferedWriter(new FileWriter(new File(src.getParentFile(), src.getName().replace(".vxi", ".obj"))));
		obj.write("mtllib " + src.getName().replace(".vxi", ".mtl") + nl + nl);
		
		D.p("Generating mesh...");
		
		ArrayList<Vector3> vertices = new ArrayList<Vector3>();
		Block[] blocks = new Block[data.length];
		
		for (int i = 0; i < blocks.length; i++)
		{
			int c = data[i];
			if (c == 255) continue;
			Block b = new Block();
			b.color = c;
			
			int x = i / (depth * height);
			int y = (i / depth) % height;
			int z = i % depth;
			
			Vector3 pos = new Vector3(offsetX + x * blockSize, offsetY + y * blockSize, offsetZ + z * blockSize);
			
			for (Direction d : Direction.values())
			{
				Face f = new Face();
				f.direction = d.ordinal();
				
				f.tl = new Vector3(0, blockSize, 0);
				f.tr = new Vector3(blockSize, blockSize, 0);
				f.bl = new Vector3(0, 0, 0);
				f.br = new Vector3(blockSize, 0, 0);
				switch (d)
				{
					case NORTH:
					{
						f.tl.x = blockSize;
						f.bl.x = blockSize;
						f.tr.z = blockSize;
						f.br.z = blockSize;
						break;
					}
					case SOUTH:
					{
						f.tl.z = blockSize;
						f.bl.z = blockSize;
						f.tr.x = 0;
						f.br.x = 0;
						break;
					}
					case WEST:
					{
						f.tl.z = blockSize;
						f.bl.z = blockSize;
						f.tr.z = blockSize;
						f.br.z = blockSize;
						f.tl.x = blockSize;
						f.bl.x = blockSize;
						f.tr.x = 0;
						f.br.x = 0;
						break;
					}
					case UP:
					{
						f.tl.z = blockSize;
						f.tr.z = blockSize;
						f.bl.y = blockSize;
						f.br.y = blockSize;
						break;
					}
					case DOWN:
					{
						f.tl.y = 0;
						f.tr.y = 0;
						f.bl.z = blockSize;
						f.br.z = blockSize;
						break;
					}
					default:
						break;
				}
				
				f.tl.add(pos);
				f.tr.add(pos);
				f.bl.add(pos);
				f.br.add(pos);
				
				if (!vertices.contains(f.tl)) vertices.add(f.tl);
				if (!vertices.contains(f.tr)) vertices.add(f.tr);
				if (!vertices.contains(f.bl)) vertices.add(f.bl);
				if (!vertices.contains(f.br)) vertices.add(f.br);
				
				b.faces[d.ordinal()] = f;
			}
			
			blocks[i] = b;
		}
		
		
		D.p("Writing vertices...");
		for (Vector3 v : vertices)
			obj.write("v " + v.x + " " + v.z + " " + v.y + nl);
		
		obj.write(nl);
		D.p("Writing normals...");
		for (Direction d : Direction.values())
			obj.write("vn " + d.dir.x + " " + d.dir.y + " " + d.dir.z + nl);
		
		obj.write(nl);
		D.p("Writing faces...");
		Arrays.sort(blocks, new Comparator<Block>()
		{
			@Override
			public int compare(Block o1, Block o2)
			{
				if (o1 == null) return -1;
				if (o2 == null) return 1;
				return Integer.compare(o1.color, o2.color);
			}
		});
		
		int lastColor = 255;
		
		for (Block b : blocks)
		{
			if (b == null) continue;
			
			if (lastColor == 255 || lastColor != b.color) obj.write("usemtl mtl" + b.color + nl);
			lastColor = b.color;
			
			for (Face f : b.faces)
			{
				obj.write("f " + (vertices.indexOf(f.tl) + 1) + "//" + (f.direction + 1) + " " + (vertices.indexOf(f.bl) + 1) + "//" + (f.direction + 1) + " " + (vertices.indexOf(f.br) + 1) + "//" + (f.direction + 1) + " " + (vertices.indexOf(f.tr) + 1) + "//" + (f.direction + 1) + nl);
			}
		}
		
		obj.close();
		D.p("Done!");
	}
}
