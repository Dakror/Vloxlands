package de.dakror.vloxlands.game.world;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btGhostPairCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw.DebugDrawModes;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.render.Mesher;
import de.dakror.vloxlands.util.Tickable;



/**
 * @author Dakror
 */
public class World implements RenderableProvider, Tickable
{
	public static final int MAXHEIGHT = 512;
	
	public static final short GROUND_FLAG = 1 << 8;
	public static final short ENTITY_FLAG = 1 << 9;
	public static final short ALL_FLAG = -1;
	
	static Material opaque, transp, highlight, wireframe;
	
	Island[] islands;
	
	int width, depth;
	
	public int visibleChunks, chunks, visibleEntities;
	
	public static Mesh chunkCube, blockCube, pointCube;
	public static final float gap = 0.025f;
	
	Array<Entity> entities = new Array<Entity>();
	
	btCollisionConfiguration collisionConfiguration;
	btBroadphaseInterface broadphaseInterface;
	btCollisionDispatcher collisionDispatcher;
	btDiscreteDynamicsWorld collisionWorld;
	btSequentialImpulseConstraintSolver constraintSolver;
	btGhostPairCallback ghostPairCallback;
	
	DebugDrawer debugDrawer;
	
	public World(int width, int depth)
	{
		this.width = width;
		this.depth = depth;
		
		islands = new Island[width * depth];
		
		Texture tex = new Texture(Gdx.files.internal("img/voxelTextures.png"));
		Texture tex2 = new Texture(Gdx.files.internal("img/transparent.png"));
		
		opaque = new Material(TextureAttribute.createDiffuse(tex));
		transp = new Material(TextureAttribute.createDiffuse(tex), new BlendingAttribute());
		highlight = new Material(TextureAttribute.createDiffuse(tex2), ColorAttribute.createDiffuse(Color.ORANGE));
		wireframe = new Material(TextureAttribute.createDiffuse(tex2), ColorAttribute.createDiffuse(Color.BLACK));
		
		chunkCube = Mesher.genCube(Chunk.SIZE + gap);
		blockCube = Mesher.genCube(1 + gap);
		pointCube = Mesher.genCube(0.05f);
		
		collisionConfiguration = new btDefaultCollisionConfiguration();
		collisionDispatcher = new btCollisionDispatcher(collisionConfiguration);
		
		broadphaseInterface = new btDbvtBroadphase();
		
		ghostPairCallback = new btGhostPairCallback();
		broadphaseInterface.getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallback);
		
		constraintSolver = new btSequentialImpulseConstraintSolver();
		
		collisionWorld = new btDiscreteDynamicsWorld(collisionDispatcher, broadphaseInterface, constraintSolver, collisionConfiguration);
		collisionWorld.setGravity(new Vector3(0, -9.81f, 0));
		
		debugDrawer = new DebugDrawer();
		debugDrawer.setDebugMode(DebugDrawModes.DBG_DrawAabb);
		collisionWorld.setDebugDrawer(debugDrawer);
	}
	
	/**
	 * @param x in index space
	 * @param y in pos space
	 * @param z in index space
	 */
	public void addIsland(int x, int z, Island island)
	{
		islands[z * width + x] = island;
		chunks += Island.CHUNKS * Island.CHUNKS * Island.CHUNKS;
	}
	
	public void update()
	{
		for (Iterator<Entity> iter = entities.iterator(); iter.hasNext();)
		{
			Entity e = iter.next();
			e.update();
		}
		
		collisionWorld.stepSimulation(Gdx.graphics.getDeltaTime(), 5, 1 / 60f);
		
		for (Iterator<Entity> iter = entities.iterator(); iter.hasNext();)
		{
			Entity e = iter.next();
			e.updateTransform();
		}
	}
	
	@Override
	public void tick(int tick)
	{
		for (Island island : islands)
			if (island != null) island.tick(tick);
		
		for (Iterator<Entity> iter = entities.iterator(); iter.hasNext();)
		{
			Entity e = iter.next();
			if (e.isMarkedForRemoval())
			{
				e.dispose();
				iter.remove();
			}
			else e.tick(tick);
		}
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
	
	public int getEntityCount()
	{
		return entities.size;
	}
	
	public void addEntity(Entity e)
	{
		entities.add(e);
		e.onSpawn();
	}
	
	public btDiscreteDynamicsWorld getCollisionWorld()
	{
		return collisionWorld;
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
	}
	
	public void render(ModelBatch batch, Environment environment)
	{
		// if (debugDrawer != null && debugDrawer.getDebugMode() > 0)
		// {
		// debugDrawer.begin(batch.getCamera());
		// collisionWorld.debugDrawWorld();
		// debugDrawer.end();
		// System.gc();
		// }
		batch.render(this, environment);
		
		visibleEntities = 0;
		for (Iterator<Entity> iter = entities.iterator(); iter.hasNext();)
		{
			Entity e = iter.next();
			if (e.inFrustum)
			{
				batch.render(e.modelInstance, environment);
				visibleEntities++;
			}
		}
	}
	
	public static float calculateUplift(float height)
	{
		return (1 - height / MAXHEIGHT) * 4 + 0.1f;
	}
}
