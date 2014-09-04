package de.dakror.vloxlands.game.voxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ObjectMap;

import de.dakror.vloxlands.render.Face;
import de.dakror.vloxlands.util.CSVReader;
import de.dakror.vloxlands.util.Direction;

public class Voxel
{
	public static enum Categories
	{
		classpath,
		id,
		textureX,
		textureY,
		name,
		opaque,
		weight,
		uplift,
		brightness,
		mining,
		itemdrop,
		tool,
		custom,
	}
	
	static short[] indices;
	static FloatArray verts = new FloatArray();
	
	public static final int VOXELS = 256;
	public static final float TEXSIZE = 16 / 512f;
	
	private static ObjectMap<String, Voxel> voxels = new ObjectMap<String, Voxel>();
	
	private static Voxel[] voxelList = new Voxel[VOXELS];
	
	String name = "NA";
	String custom;
	Class<?> tool;
	boolean opaque = true;
	boolean replaceable = false;
	float smoothness = 0;
	byte id;
	float weight = 1f;
	float uplift = 0;
	float brightness;
	int textureX;
	int textureY;
	int mining;
	byte itemdrop;
	Mesh mesh;
	
	public void registerVoxel(int id)
	{
		if (voxelList[id + 128] == null) voxelList[id + 128] = this;
		else
		{
			Gdx.app.error("Voxel.registerVoxel", "The ID " + id + " was already taken up by \"" + voxelList[id + 128].name + "\"");
			Gdx.app.exit();
		}
		this.id = (byte) id;
	}
	
	public static Voxel getForId(byte id)
	{
		return voxelList[id + 128];
	}
	
	public synchronized static Voxel getForId(int id)
	{
		return voxelList[id];
	}
	
	public Mesh getMesh()
	{
		return mesh;
	}
	
	public boolean isReplaceable()
	{
		return false;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Class<?> getTool()
	{
		return tool;
	}
	
	protected Vector2 getTexCoord(int x, int y, int z, Direction d)
	{
		return new Vector2(textureX, textureY);
	}
	
	public Vector2 getTextureUV(int x, int y, int z, Direction d)
	{
		return getTexCoord(x, y, z, d).cpy().scl(TEXSIZE);
	}
	
	public boolean isOpaque()
	{
		return opaque;
	}
	
	public float getSmoothness()
	{
		return smoothness;
	}
	
	public byte getId()
	{
		return id;
	}
	
	public float getWeight()
	{
		return weight;
	}
	
	public float getUplift()
	{
		return uplift;
	}
	
	public float getBrightness()
	{
		return brightness;
	}
	
	public String getCustom()
	{
		return custom;
	}
	
	public int getMining()
	{
		return mining;
	}
	
	public byte getItemdrop()
	{
		if (itemdrop == 127) return id;
		return itemdrop;
	}
	
	@Override
	public String toString()
	{
		return getClass().getName() + "." + name.toUpperCase().replace(" ", "_");
	}
	
	public boolean hasItemdrop()
	{
		return itemdrop != (byte) 0;
	}
	
	public static int getIdForName(String name)
	{
		for (int i = 0; i < voxelList.length; i++)
		{
			Voxel v = Voxel.getForId(i);
			if (v.getName().equals(name)) return i;
		}
		
		Gdx.app.error("Voxel.getIdForName", name + " not found");
		return -1;
	}
	
	public static Voxel get(String name)
	{
		return voxels.get(name);
	}
	
	public static Array<Voxel> getAll()
	{
		return voxels.values().toArray();
	}
	
	public static void loadVoxels()
	{
		CSVReader csv = new CSVReader(Gdx.files.internal("data/voxels.csv"));
		String[] categories = csv.readRow();
		String[] defaults = csv.readRow();
		String cell;
		Voxel voxel = null;
		while ((cell = csv.readNext()) != null)
		{
			if (csv.getIndex() == 0)
			{
				try
				{
					if (cell.length() > 0) voxel = (Voxel) Class.forName("de.dakror.vloxlands.game.voxel." + cell).newInstance();
					else voxel = new Voxel();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			Categories c = Categories.valueOf(categories[csv.getIndex()]);
			switch (c)
			{
				case classpath:
					break;
				case id:
				{
					voxel.registerVoxel(Integer.valueOf(cell) - 128);
					break;
				}
				case textureX:
				{
					if (cell.length() > 0) voxel.textureX = Integer.parseInt(cell);
					else voxel.textureX = Integer.parseInt(defaults[csv.getIndex()]);
					break;
				}
				case textureY:
				{
					if (cell.length() > 0) voxel.textureY = Integer.parseInt(cell);
					else voxel.textureY = Integer.parseInt(defaults[csv.getIndex()]);
					break;
				}
				case name:
				{
					if (cell.length() > 0) voxel.name = cell;
					else voxel.name = defaults[csv.getIndex()];
					voxels.put(voxel.getName().toUpperCase().replace(" ", "_"), voxel);
					break;
				}
				case opaque:
				{
					if (cell.length() > 0) voxel.opaque = cell.equals("1");
					else voxel.opaque = defaults[csv.getIndex()].equals("1");
					break;
				}
				case weight:
				{
					if (cell.length() > 0) voxel.weight = Float.parseFloat(cell);
					else voxel.weight = Float.parseFloat(defaults[csv.getIndex()]);
					break;
				}
				case uplift:
				{
					if (cell.length() > 0) voxel.uplift = Float.parseFloat(cell);
					else voxel.uplift = Float.parseFloat(defaults[csv.getIndex()]);
					break;
				}
				case brightness:
				{
					if (cell.length() > 0) voxel.brightness = Float.parseFloat(cell);
					else voxel.brightness = Float.parseFloat(defaults[csv.getIndex()]);
					break;
				}
				case mining:
				{
					if (cell.length() > 0) voxel.mining = Integer.parseInt(cell);
					else voxel.mining = Integer.parseInt(defaults[csv.getIndex()]);
					break;
				}
				case itemdrop:
				{
					if (cell.length() > 0) voxel.itemdrop = (byte) (Integer.parseInt(cell) - 128);
					else voxel.itemdrop = (byte) (Integer.parseInt(defaults[csv.getIndex()]) - 128);
					break;
				}
				case tool:
				{
					if (cell.length() > 0)
					{
						try
						{
							voxel.tool = Class.forName("de.dakror.vloxlands.game.item.tool." + cell);
						}
						catch (ClassNotFoundException e)
						{
							e.printStackTrace();
						}
					}
					else voxel.tool = null;
					break;
				}
				case custom:
				{
					if (cell.length() > 0) voxel.custom = cell;
					else voxel.custom = null;
					break;
				}
				default:
					Gdx.app.log("Voxel.loadVoxels", "Unhandled voxel data column: " + c);
					break;
			}
		}
		
		voxels.put(voxel.getName().toUpperCase().replace(" ", "_"), voxel);
		
		Gdx.app.log("Voxel.loadVoxels", voxels.size + " voxels loaded.");
		
	}
	
	public static void buildMeshes()
	{
		if (indices == null)
		{
			int len = 3 * 6 * 6 / 3;
			indices = new short[len];
			short j = 0;
			for (int i = 0; i < len; i += 6, j += 4)
			{
				indices[i + 0] = (short) (j + 0);
				indices[i + 1] = (short) (j + 1);
				indices[i + 2] = (short) (j + 2);
				indices[i + 3] = (short) (j + 2);
				indices[i + 4] = (short) (j + 3);
				indices[i + 5] = (short) (j + 0);
			}
		}
		
		for (Voxel v : voxels.values())
		{
			v.mesh = new Mesh(true, 24, indices.length, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.ColorPacked(), VertexAttribute.TexCoords(0), VertexAttribute.TexCoords(1));
			v.mesh.setIndices(indices);
			verts = new FloatArray();
			for (Direction d : Direction.values())
				new Face(d, new Vector3(), v.getTextureUV(0, 0, 0, d)).getVertexData(verts);
			v.mesh.setVertices(verts.items, 0, verts.size);
		}
	}
	
	public static String capitalizeFirstLetter(String string)
	{
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}
}
