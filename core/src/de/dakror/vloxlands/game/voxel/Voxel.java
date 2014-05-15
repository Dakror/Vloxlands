package de.dakror.vloxlands.game.voxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

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
		brightness
	}
	
	public static final int VOXELS = 256;
	public static final float TEXSIZE = 16 / 512f;
	
	private static ObjectMap<String, Voxel> voxels = new ObjectMap<String, Voxel>();
	
	private static Voxel[] voxelList = new Voxel[VOXELS];
	
	private String name = "NA";
	int textureX, textureY;
	boolean opaque = true;
	boolean replaceable = false;
	float smoothness = 0;
	private byte id;
	private float weight = 1f;
	private float uplift = 0;
	private float brightness;
	
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
	
	public boolean isReplaceable()
	{
		return false;
	}
	
	public void setReplaceable(boolean replaceable)
	{
		this.replaceable = replaceable;
	}
	
	public Voxel setName(String s)
	{
		name = s;
		return this;
	}
	
	public String getName()
	{
		return name;
	}
	
	protected Vector2 getTexCoord(int x, int y, int z, Direction d)
	{
		return new Vector2(textureX, textureY);
	}
	
	public Vector2 getTextureUV(int x, int y, int z, Direction d)
	{
		return getTexCoord(x, y, z, d).cpy().scl(TEXSIZE);
	}
	
	public void setTextureY(int textureY)
	{
		this.textureY = textureY;
	}
	
	public void setTextureX(int textureX)
	{
		this.textureX = textureX;
	}
	
	public Voxel setOpaque(boolean b)
	{
		opaque = b;
		return this;
	}
	
	public boolean isOpaque()
	{
		return opaque;
	}
	
	public float getSmoothness()
	{
		return smoothness;
	}
	
	public Voxel setSmoothness(float smooth)
	{
		smoothness = smooth;
		return this;
	}
	
	public byte getId()
	{
		return id;
	}
	
	public float getWeight()
	{
		return weight;
	}
	
	public Voxel setWeight(float weight)
	{
		this.weight = weight;
		return this;
	}
	
	public float getUplift()
	{
		return uplift;
	}
	
	public Voxel setUplift(float uplift)
	{
		this.uplift = uplift;
		return this;
	}
	
	public float getBrightness()
	{
		return brightness;
	}
	
	public Voxel setBrightness(float brightness)
	{
		this.brightness = brightness;
		return this;
	}
	
	@Override
	public String toString()
	{
		return getClass().getName() + "." + name.toUpperCase().replace(" ", "_");
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
					if (cell.length() > 0) voxel.setTextureX(Integer.parseInt(cell));
					else voxel.setTextureX(Integer.parseInt(defaults[csv.getIndex()]));
					break;
				}
				case textureY:
				{
					if (cell.length() > 0) voxel.setTextureY(Integer.parseInt(cell));
					else voxel.setTextureY(Integer.parseInt(defaults[csv.getIndex()]));
					break;
				}
				case name:
				{
					if (cell.length() > 0) voxel.setName(cell);
					else voxel.setName(defaults[csv.getIndex()]);
					voxels.put(voxel.getName().toUpperCase().replace(" ", "_"), voxel);
					break;
				}
				case opaque:
				{
					if (cell.length() > 0) voxel.setOpaque(cell.equals("1"));
					else voxel.setOpaque(defaults[csv.getIndex()].equals("1"));
					break;
				}
				case weight:
				case uplift:
				case brightness:
				{
					try
					{
						if (cell.length() > 0) voxel.getClass().getMethod("set" + capitalizeFirstLetter(categories[csv.getIndex()]), Float.TYPE).invoke(voxel, Float.parseFloat(cell));
						else voxel.getClass().getMethod("set" + capitalizeFirstLetter(categories[csv.getIndex()]), Float.TYPE).invoke(voxel, Float.parseFloat(defaults[csv.getIndex()]));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
					break;
				}
				default:
					Gdx.app.debug("Voxel.loadVoxels", "Unhandled voxel data column: " + c);
					break;
			}
		}
		
		voxels.put(voxel.getName().toUpperCase().replace(" ", "_"), voxel);
		
		Gdx.app.debug("Voxel.loadVoxels", voxels.size + " voxels loaded.");
		
	}
	
	public static String capitalizeFirstLetter(String string)
	{
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}
}
