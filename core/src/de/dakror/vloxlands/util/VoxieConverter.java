package de.dakror.vloxlands.util;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector3;

/**
 * @author Dakror
 */
@Deprecated
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
		Vector3 tl, bl, br, tr;
		int direction;
	}
	
	static class Block
	{
		Face[] faces = new Face[Direction.values().length];
		int color;
	}
	
	static final String nl = "\r\n";
	static final float blockSize = 0.25f;
	
	static Block[] blocks;
	
	static String out;
	
	static int[] data;
	static int width, height, depth, offsetX, offsetY, offsetZ;
	
	@Deprecated
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
		
		width = bb.getInt();
		height = bb.getInt();
		depth = bb.getInt();
		
		offsetX = bb.getInt();
		offsetY = bb.getInt();
		offsetZ = bb.getInt();
		
		data = new int[width * height * depth];
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
		
		D.p("Generating mesh...");
		
		blocks = new Block[data.length];
		generateBlocks();
		
		D.p("Writing output file...");
		
		out = "";
		
		/*
		 * Wondering why I hardcoded all this JSON?
		 * 1. I don't like libgdx's default JSON API
		 * 2. I wanted to exactly match the default format
		 */
		
		out += "{" + nl;
		out += "	\"version\": [  0,   1]," + nl;
		out += "	\"id\": \"\"," + nl;
		out += "	\"meshes\": [" + nl;
		out += "		{" + nl;
		out += "			\"attributes\": [\"POSITION\", \"NORMAL\"]," + nl; // ,
																																	// \"COLOR\",
																																	// \"TEXCOORD0\"
		out += "			\"vertices\": [" + nl;
		
		ArrayList<Vector3> vertices = new ArrayList<Vector3>();
		
		String indexArray = "";
		
		int indices = 0;
		
		DecimalFormat dm = new DecimalFormat();
		dm.setParseIntegerOnly(true);
		dm.setGroupingUsed(false);
		
		for (Block b : blocks)
		{
			if (b == null) continue;
			
			for (Face f : b.faces)
			{
				if (f == null) continue;
				
				if (!vertices.contains(f.tl))
				{
					printVertex(f.tl, f.direction, colors[b.color]);
					vertices.add(f.tl);
				}
				if (!vertices.contains(f.bl))
				{
					printVertex(f.bl, f.direction, colors[b.color]);
					vertices.add(f.bl);
				}
				if (!vertices.contains(f.br))
				{
					printVertex(f.br, f.direction, colors[b.color]);
					vertices.add(f.br);
				}
				if (!vertices.contains(f.tr))
				{
					printVertex(f.tr, f.direction, colors[b.color]);
					vertices.add(f.tr);
				}
				
				if (indices % 12 == 0) indexArray += "						";
				indexArray += format(dm, vertices.indexOf(f.tl), 4) + ",";
				if (indices % 12 == 11) indexArray += nl;
				indices++;
				if (indices % 12 == 0) indexArray += "						";
				indexArray += format(dm, vertices.indexOf(f.bl), 4) + ",";
				if (indices % 12 == 11) indexArray += nl;
				indices++;
				if (indices % 12 == 0) indexArray += "						";
				indexArray += format(dm, vertices.indexOf(f.br), 4) + ",";
				if (indices % 12 == 11) indexArray += nl;
				indices++;
				if (indices % 12 == 0) indexArray += "						";
				indexArray += format(dm, vertices.indexOf(f.tl), 4) + ",";
				if (indices % 12 == 11) indexArray += nl;
				indices++;
				if (indices % 12 == 0) indexArray += "						";
				indexArray += format(dm, vertices.indexOf(f.br), 4) + ",";
				if (indices % 12 == 11) indexArray += nl;
				indices++;
				if (indices % 12 == 0) indexArray += "						";
				indexArray += format(dm, vertices.indexOf(f.tr), 4) + ",";
				if (indices % 12 == 11) indexArray += nl;
				indices++;
			}
		}
		
		out += "			]," + nl;
		out += "			\"parts\": [" + nl;
		out += "				{" + nl;
		out += "					\"id\": \"shape1_part1\", " + nl;
		out += "					\"type\": \"TRIANGLES\", " + nl;
		out += "					\"indices\": [" + nl;
		
		out += indexArray;
		
		out += "					]" + nl;
		out += "				}" + nl;
		out += "			]" + nl;
		out += "		}" + nl;
		out += "	]," + nl;
		out += "	\"materials\": [" + nl;
		out += "		{" + nl;
		out += "			\"id\": \"mtl1\", " + nl;
		out += "			\"diffuse\": [ 0.800000,  0.800000,  0.800000], " + nl;
		out += "			\"emissive\": [ 0.800000,  0.800000,  0.800000]" + nl;
		out += "		}" + nl;
		out += "	]," + nl;
		out += "	\"nodes\": [" + nl;
		out += "		{" + nl;
		out += "			\"id\": \"" + src.getName().substring(0, src.getName().lastIndexOf(".")) + "\", " + nl;
		out += "			\"rotation\": [ 0.000000,  0.000000,  0.000000,  1.000000], " + nl;
		out += "			\"scale\": [ 1.000000,  1.000000,  1.000000], " + nl;
		out += "			\"parts\": [" + nl;
		out += "				{" + nl;
		out += "					\"meshpartid\": \"shape1_part1\", " + nl;
		out += "					\"materialid\": \"mtl1\"" + nl;
		out += "				}" + nl;
		out += "			]" + nl;
		out += "		}" + nl;
		out += "	], " + nl;
		out += "	\"animations\": [" + nl;
		out += "		{" + nl;
		out += "			\"id\": \"Default Take\", " + nl;
		out += "			\"bones\": []" + nl;
		out += "		}" + nl;
		out += "	]" + nl;
		out += "}" + nl;
		
		FileWriter fw = new FileWriter(new File(src.getParentFile(), src.getName().replace(".vxi", ".g3dj")));
		fw.write(out);
		fw.close();
		
		D.p("Done!");
	}
	
	static void printVertex(Vector3 v, int dir, Color c) throws IOException
	{
		Direction d = Direction.values()[dir];
		
		DecimalFormat dm = new DecimalFormat();
		dm.setMinimumFractionDigits(6);
		dm.setMaximumFractionDigits(6);
		
		// @formatter:off
		out+="				" + 
				format(dm, v.x, 10) + "," + 
				format(dm, v.z, 10) + "," + 
				format(dm, v.y, 10) + "," + 
				format(dm, d.dir.x, 10) + "," + 
				format(dm, d.dir.z, 10) + "," + 
				format(dm, d.dir.y, 10) + "," + 
//				format(dm, c.getRed() / 255f, 10) + "," + 
//				format(dm, c.getGreen() / 255f, 10) + "," + 
//				format(dm, c.getBlue() / 255f, 10) + "," + 
//				format(dm, 0, 10) + "," + 
//				format(dm, 0, 10) + "," + 
//				format(dm, 0, 10) + "," + 
				nl;
		// @formatter:on
	}
	
	static String format(DecimalFormat dm, float f, int w)
	{
		String s = dm.format(f);
		
		int origWidth = s.length();
		
		while (s.length() < w + (w - origWidth < 1 ? origWidth - w + 1 : 0))
			s = " " + s;
		return s;
	}
	
	static void generateBlocks()
	{
		for (int i = 0; i < blocks.length; i++)
		{
			int c = data[i];
			if (c == 255) continue;
			Block b = new Block();
			b.color = c;
			
			int x = i / (depth * height);
			int y = (i / depth) % height;
			int z = i % depth;
			
			Vector3 pos = new Vector3((offsetX + x) * blockSize, (offsetY + y) * blockSize, (offsetZ + z) * blockSize);
			
			for (Direction d : Direction.values())
			{
				int index2 = (int) ((x + d.dir.x) * height * depth + (y + d.dir.y) * depth + (z + d.dir.z));
				if (x + d.dir.x > -1 && y + d.dir.y > -1 && z + d.dir.z > -1 && x + d.dir.x < width && y + d.dir.y < height && z + d.dir.z < depth && data[index2] != 255) continue;
				
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
				
				b.faces[d.ordinal()] = f;
			}
			
			blocks[i] = b;
		}
	}
	
	static void setupFaceVertices(Face face)
	{	
		
	}
}
