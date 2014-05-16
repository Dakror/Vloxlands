package de.dakror.vloxlands.game.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import de.dakror.vloxlands.util.CSVReader;

/**
 * @author Dakror
 */
public class Item
{
	public static enum Categories
	{
		classpath,
		id,
		name,
		model,
		description,
		resource,
		tool,
		equipable,
		custom,
		stack
	}
	
	public static final int ITEMS = 256;
	
	private static ObjectMap<String, Item> items = new ObjectMap<String, Item>();
	
	private static Item[] itemList = new Item[ITEMS];
	
	public void registerItem(int id)
	{
		if (itemList[id + 128] == null) itemList[id + 128] = this;
		else
		{
			Gdx.app.error("Item.registerItem", "The ID " + id + " was already taken up by \"" + itemList[id + 128].name + "\"");
			Gdx.app.exit();
		}
		this.id = (byte) id;
	}
	
	protected String name = "NA";
	protected String model;
	protected String description;
	protected String custom;
	protected byte id;
	protected int stack;
	protected boolean resource;
	protected boolean tool;
	protected boolean equipable;
	
	public String getName()
	{
		return name;
	}
	
	public String getModel()
	{
		return model;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getCustom()
	{
		return custom;
	}
	
	public byte getId()
	{
		return id;
	}
	
	public boolean isResource()
	{
		return resource;
	}
	
	public boolean isTool()
	{
		return tool;
	}
	
	public boolean isEquipable()
	{
		return equipable;
	}
	
	public int getStack()
	{
		return stack;
	}
	
	public void onLoaded()
	{}
	
	@Override
	public String toString()
	{
		return getClass().getName() + "." + name.toUpperCase().replace(" ", "_");
	}
	
	public static Item getForId(byte id)
	{
		return itemList[id + 128];
	}
	
	public synchronized static Item getForId(int id)
	{
		return itemList[id];
	}
	
	public static int getIdForName(String name)
	{
		for (int i = 0; i < itemList.length; i++)
		{
			Item v = Item.getForId(i);
			if (v.getName().equals(name)) return i;
		}
		
		Gdx.app.error("Item.getIdForName", name + " not found");
		return -1;
	}
	
	public static Item get(String name)
	{
		return items.get(name);
	}
	
	public static Array<Item> getAll()
	{
		return items.values().toArray();
	}
	
	public static void loadItems()
	{
		CSVReader csv = new CSVReader(Gdx.files.internal("data/items.csv"));
		String[] categories = csv.readRow();
		String[] defaults = csv.readRow();
		String cell;
		Item item = null;
		while ((cell = csv.readNext()) != null)
		{
			if (csv.getIndex() == 0)
			{
				try
				{
					if (cell.length() > 0) item = (Item) Class.forName("de.dakror.vloxlands.game.item." + cell).newInstance();
					else item = new Item();
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
					item.registerItem(Integer.valueOf(cell) - 128);
					break;
				}
				case name:
				{
					if (cell.length() > 0) item.name = cell;
					else item.name = defaults[csv.getIndex()];
					items.put(item.getName().toUpperCase().replace(" ", "_"), item);
					break;
				}
				case description:
				{
					if (cell.length() > 0) item.description = cell;
					else item.description = defaults[csv.getIndex()];
					break;
				}
				case model:
				{
					if (cell.length() > 0) item.model = cell;
					else item.model = defaults[csv.getIndex()];
					break;
				}
				case resource:
				{
					if (cell.length() > 0) item.resource = cell.equals("1");
					else item.resource = defaults[csv.getIndex()].equals("1");
					break;
				}
				case tool:
				{
					if (cell.length() > 0) item.tool = cell.equals("1");
					else item.tool = defaults[csv.getIndex()].equals("1");
					break;
				}
				case equipable:
				{
					if (cell.length() > 0) item.equipable = cell.equals("1");
					else item.equipable = defaults[csv.getIndex()].equals("1");
					break;
				}
				case stack:
				{
					if (cell.length() > 0) item.stack = Integer.parseInt(cell);
					else item.stack = Integer.parseInt(defaults[csv.getIndex()]);
					break;
				}
				case custom:
				{
					if (cell.length() > 0) item.custom = cell;
					else item.custom = null;
					break;
				}
				default:
					Gdx.app.debug("Item.loadItems", "Unhandled item data column: " + c);
					break;
			}
		}
		
		items.put(item.getName().toUpperCase().replace(" ", "_"), item);
		
		Gdx.app.debug("Item.loadItems", items.size + " items loaded.");
	}
}
