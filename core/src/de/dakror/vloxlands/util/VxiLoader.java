package de.dakror.vloxlands.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.util.VxiLoader.VxiParameter;

/**
 * @author Dakror
 */
public class VxiLoader extends AsynchronousAssetLoader<Model, VxiParameter>
{
	public static class VxiParameter extends AssetLoaderParameters<Model>
	{}
	
	class ReferencePoint
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
	
	class Face
	{
		Vector3 tl, bl, br, tr;
		int direction;
	}
	
	class Block
	{
		Face[] faces = new Face[Direction.values().length];
		int color;
	}
	
	class Vertex
	{
		Vector3 pos;
		Vector3 nor;
		Color col;
		
		public Vertex(Vector3 pos, Vector3 nor, Color col)
		{
			this.pos = pos;
			this.nor = nor;
			this.col = col;
		}
	}
	
	public static final float blockSize = 0.25f;
	
	Color[] colors;
	Block[] blocks;
	ReferencePoint[] referencePoints;
	int[] data;
	
	int width, height, depth, offsetX, offsetY, offsetZ;
	
	AssetManager assets;
	
	public VxiLoader(AssetManager assets, FileHandleResolver resolver)
	{
		super(resolver);
		this.assets = assets;
	}
	
	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, VxiParameter parameter)
	{
		byte[] fileData = new byte[(int) file.length()];
		file.readBytes(fileData, 0, fileData.length);
		
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
		
		colors = new Color[256];
		for (int i = 0; i < colors.length; i++)
			colors[i] = new Color((bb.get() & 0xff) / 255f, (bb.get() & 0xff) / 255f, (bb.get() & 0xff) / 255f, 1);
		
		int refPointCount = bb.get() & 0xff;
		referencePoints = new ReferencePoint[refPointCount];
		
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
		
		blocks = new Block[data.length];
		generateBlocks();
	}
	
	@Override
	public Model loadSync(AssetManager manager, String fileName, FileHandle file, VxiParameter parameter)
	{
		ModelBuilder mb = new ModelBuilder();
		mb.begin();
		Material m = new Material(new BlendingAttribute());
		MeshPartBuilder mpb = mb.part(file.nameWithoutExtension(), GL20.GL_TRIANGLES, new VertexAttributes(VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.ColorPacked()), m);
		Array<Vertex> vertices = new Array<Vertex>();
		
		for (Block b : blocks)
		{
			if (b == null) continue;
			
			for (Face f : b.faces)
			{
				if (f == null) continue;
				
				Direction d = Direction.values()[f.direction];
				Vector3 dir = new Vector3(d.dir.x, d.dir.z, d.dir.y);
				
				Color c = colors[b.color];
				
				Vertex tl = new Vertex(f.tl, dir, c);
				Vertex bl = new Vertex(f.bl, dir, c);
				Vertex br = new Vertex(f.br, dir, c);
				Vertex tr = new Vertex(f.tr, dir, c);
				
				if (!vertices.contains(tl, true))
				{
					mpb.vertex(f.tl, dir, c, null);
					vertices.add(tl);
				}
				if (!vertices.contains(bl, true))
				{
					mpb.vertex(f.bl, dir, c, null);
					vertices.add(bl);
				}
				if (!vertices.contains(br, true))
				{
					mpb.vertex(f.br, dir, c, null);
					vertices.add(br);
				}
				if (!vertices.contains(tr, true))
				{
					mpb.vertex(f.tr, dir, c, null);
					vertices.add(tr);
				}
				
				mpb.index((short) vertices.indexOf(tl, true), (short) vertices.indexOf(bl, true), (short) vertices.indexOf(br, true));
				mpb.index((short) vertices.indexOf(tl, true), (short) vertices.indexOf(br, true), (short) vertices.indexOf(tr, true));
			}
		}
		Model model = mb.end();
		
		for (ReferencePoint p : referencePoints)
		{
			Node node = new Node();
			node.id = p.name;
			node.parent = model.nodes.get(0);
			node.translation.set(p.x, p.z, p.y);
			model.nodes.get(0).children.add(node);
		}
		
		return model;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, VxiParameter parameter)
	{
		return null;
	}
	
	private void generateBlocks()
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
				
				float tmp = 0;
				f.tl.add(pos);
				tmp = f.tl.y;
				f.tl.y = f.tl.z;
				f.tl.z = tmp;
				tmp = 0;
				
				f.tr.add(pos);
				tmp = f.tr.y;
				f.tr.y = f.tr.z;
				f.tr.z = tmp;
				tmp = 0;
				
				f.bl.add(pos);
				tmp = f.bl.y;
				f.bl.y = f.bl.z;
				f.bl.z = tmp;
				tmp = 0;
				
				f.br.add(pos);
				tmp = f.br.y;
				f.br.y = f.br.z;
				f.br.z = tmp;
				
				b.faces[d.ordinal()] = f;
			}
			
			blocks[i] = b;
		}
	}
}
