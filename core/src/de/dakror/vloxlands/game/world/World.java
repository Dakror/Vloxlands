package de.dakror.vloxlands.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.glutils.MipMapGenerator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import de.dakror.vloxlands.gen.IslandGenerator;
import de.dakror.vloxlands.util.Tickable;



/**
 * @author Dakror
 */
public class World implements RenderableProvider, Tickable
{
	public static final int MAXHEIGHT = 512;
	
	static Material opaque;// , transp;
	
	Island[] islands;
	
	int width, depth;
	
	public int visibleChunks, chunks;
	
	public World(int width, int depth)
	{
		this.width = width;
		this.depth = depth;
		
		islands = new Island[width * depth];
		
		Texture tex = new Texture(Gdx.files.internal("voxelTextures.png"), true);
		MipMapGenerator.setUseHardwareMipMap(false);
		tex.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		opaque = new Material(TextureAttribute.createDiffuse(tex));
	}
	
	/**
	 * @param x in index space
	 * @param y in pos space
	 * @param z in index space
	 */
	public void addIsland(int x, int z)
	{
		Island island = IslandGenerator.generate();
		island.setPos(new Vector3(x * Island.SIZE, island.getPos().y, z * Island.SIZE));
		islands[z * width + x] = island;
		chunks += Island.CHUNKS * Island.CHUNKS * Island.CHUNKS;
	}
	
	@Override
	public void tick(int tick)
	{
		for (Island island : islands)
			if (island != null) island.tick(tick);
	}
	
	public Island[] getIslands()
	{
		return islands;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getDepth()
	{
		return depth;
	}
	
	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool)
	{
		visibleChunks = 0;
		for (Island island : islands)
		{
			if (island != null)
			{
				island.getRenderables(renderables, pool);
				visibleChunks += island.visibleChunks;
			}
		}
		
		// entities
	}
	
	public static float calculateUplift(float height)
	{
		return (1 - height / MAXHEIGHT) * 4 + 0.1f;
	}
}
