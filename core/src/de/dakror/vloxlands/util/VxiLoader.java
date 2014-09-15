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
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import de.dakror.vloxlands.render.ColorFace;
import de.dakror.vloxlands.render.Mesher;
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
	
	public static final float defaultResolution = 0.25f;
	
	Color[] colors;
	ReferencePoint[] referencePoints;
	IntMap<ColorFace> faces;
	int[] data;
	
	float resolution;
	
	int width, height, depth, offsetX, offsetY, offsetZ;
	
	final Vector3 tmp = new Vector3();
	
	AssetManager assets;
	
	public VxiLoader(AssetManager assets, FileHandleResolver resolver)
	{
		super(resolver);
		this.assets = assets;
	}
	
	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, VxiParameter parameter)
	{
		resolution = getResolution(fileName);
		faces = new IntMap<ColorFace>();
		
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
		
		generateFaces();
	}
	
	@Override
	public Model loadSync(AssetManager manager, String fileName, FileHandle file, VxiParameter parameter)
	{
		ModelBuilder mb = new ModelBuilder();
		mb.begin();
		Material m = new Material();
		MeshPartBuilder mpb = mb.part(file.nameWithoutExtension(), GL20.GL_TRIANGLES, new VertexAttributes(VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.ColorPacked()), m);
		
		Array<Vertex> vertices = new Array<Vertex>();
		
		offsetZ -= depth / 3f;
		
		for (ColorFace f : faces.values())
		{
			Vertex tl = new Vertex(f.tl.cpy().add(f.pos).add(offsetX, offsetY, offsetZ).scl(resolution), f.dir.dir, f.c);
			Vertex bl = new Vertex(f.bl.cpy().add(f.pos).add(offsetX, offsetY, offsetZ).scl(resolution), f.dir.dir, f.c);
			Vertex br = new Vertex(f.br.cpy().add(f.pos).add(offsetX, offsetY, offsetZ).scl(resolution), f.dir.dir, f.c);
			Vertex tr = new Vertex(f.tr.cpy().add(f.pos).add(offsetX, offsetY, offsetZ).scl(resolution), f.dir.dir, f.c);
			
			rotate(tl.pos);
			rotate(bl.pos);
			rotate(br.pos);
			rotate(tr.pos);
			
			if (!vertices.contains(tr, true))
			{
				mpb.vertex(tr.pos, tr.nor, f.c, null);
				vertices.add(tr);
			}
			if (!vertices.contains(br, true))
			{
				mpb.vertex(br.pos, br.nor, f.c, null);
				vertices.add(br);
			}
			if (!vertices.contains(tl, true))
			{
				mpb.vertex(tl.pos, tl.nor, f.c, null);
				vertices.add(tl);
			}
			if (!vertices.contains(bl, true))
			{
				mpb.vertex(bl.pos, bl.nor, f.c, null);
				vertices.add(bl);
			}
			
			mpb.index((short) vertices.indexOf(br, true), (short) vertices.indexOf(bl, true), (short) vertices.indexOf(tl, true));
			mpb.index((short) vertices.indexOf(tr, true), (short) vertices.indexOf(br, true), (short) vertices.indexOf(tl, true));
		}
		
		Model model = mb.end();
		
		for (ReferencePoint p : referencePoints)
		{
			Node node = new Node();
			node.id = p.name;
			node.parent = model.nodes.get(0);
			node.translation.add(p.x, p.y, p.z - depth / 4f).scl(resolution);
			rotate(node.translation);
			model.nodes.get(0).children.add(node);
		}
		
		return model;
	}
	
	public void rotate(Vector3 v)
	{
		tmp.set(offsetX * resolution, offsetY * resolution, offsetZ * resolution).sub(v);
		v.add(tmp);
		v.rotate(Vector3.X, -90);
		tmp.rotate(Vector3.X, -90);
		v.sub(tmp);
	}
	
	public float getResolution(String fileName)
	{
		if (!fileName.contains("[") || !fileName.contains("]")) return defaultResolution;
		int res = Integer.parseInt(fileName.substring(fileName.lastIndexOf("[") + 1, fileName.lastIndexOf("]")));
		
		return 1.0f / res;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, VxiParameter parameter)
	{
		return null;
	}
	
	private void generateFaces()
	{
		for (int i = 0; i < data.length; i++)
		{
			int c = data[i];
			if (c == 255) continue;
			
			int x = i / (depth * height);
			int y = (i / depth) % height;
			int z = i % depth;
			
			Vector3 pos = new Vector3(x, y, z);
			
			for (Direction d : Direction.values())
			{
				int index2 = (int) ((x + d.dir.x) * height * depth + (y + d.dir.y) * depth + (z + d.dir.z));
				if (x + d.dir.x > -1 && y + d.dir.y > -1 && z + d.dir.z > -1 && x + d.dir.x < width && y + d.dir.y < height && z + d.dir.z < depth && data[index2] != 255) continue;
				
				ColorFace f = new ColorFace(d, pos, colors[c]);
				
				faces.put(f.hashCode(), f);
			}
		}
		
		Mesher.generateGreedyMesh(0, 0, 0, width, height, depth, faces);
	}
}
