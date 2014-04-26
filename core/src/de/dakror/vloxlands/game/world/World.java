package de.dakror.vloxlands.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import de.dakror.vloxlands.generate.IslandGenerator;
import de.dakror.vloxlands.render.Mesher;
import de.dakror.vloxlands.util.Tickable;



/**
 * @author Dakror
 */
public class World implements RenderableProvider, Tickable
{
	public static final int MAXHEIGHT = 512;
	
	static Material opaque, transp, highlight;
	
	Island[] islands;
	
	int width, depth;
	
	public int visibleChunks, chunks;
	
	public static Mesh chunkCube, blockCube, pointCube;
	public static final float gap = 0.01f;
	
	public World(int width, int depth)
	{
		this.width = width;
		this.depth = depth;
		
		islands = new Island[width * depth];
		
		Texture tex = new Texture(Gdx.files.internal("img/voxelTextures.png"));
		
		opaque = new Material(TextureAttribute.createDiffuse(tex));
		transp = new Material(TextureAttribute.createDiffuse(tex), new BlendingAttribute());
		highlight = new Material(TextureAttribute.createDiffuse(tex), ColorAttribute.createDiffuse(Color.ORANGE));
		
		chunkCube = Mesher.genCube(Chunk.SIZE + gap);
		blockCube = Mesher.genCube(1 + gap);
		pointCube = Mesher.genCube(0.05f);
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
