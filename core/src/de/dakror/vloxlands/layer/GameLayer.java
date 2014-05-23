package de.dakror.vloxlands.layer;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.ai.AStar;
import de.dakror.vloxlands.ai.BFS;
import de.dakror.vloxlands.ai.node.AStarNode;
import de.dakror.vloxlands.ai.node.BFSNode;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.entity.structure.Warehouse;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Chunk;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.game.world.World;
import de.dakror.vloxlands.render.MeshingThread;
import de.dakror.vloxlands.util.Direction;
import de.dakror.vloxlands.util.event.EventDispatcher;
import de.dakror.vloxlands.util.event.VoxelSelection;

/**
 * @author Dakror
 */
public class GameLayer extends Layer
{
	public static final long seed = (long) (Math.random() * Long.MAX_VALUE);
	public static final float velocity = 10;
	public static final float rotateSpeed = 0.2f;
	public static final float pickRayMaxDistance = 30f;
	
	public static GameLayer instance;
	
	public static World world;
	public static PerspectiveCamera camera;
	public static ShapeRenderer shapeRenderer;
	
	public Environment lights;
	
	ModelBatch modelBatch;
	FirstPersonCameraController controller;
	Vector3 worldMiddle;
	
	boolean middleDown;
	boolean doneLoading;
	
	public Vector3 intersection = new Vector3();
	public Vector3 intersection2 = new Vector3();
	
	ModelInstance sky;
	
	// -- temp -- //
	public final Vector3 tmp = new Vector3();
	public final Vector3 tmp1 = new Vector3();
	public final Vector3 tmp2 = new Vector3();
	public final Vector3 tmp3 = new Vector3();
	public final Vector3 tmp4 = new Vector3();
	public final Vector3 tmp5 = new Vector3();
	public final Vector3 tmp6 = new Vector3();
	public final Vector3 tmp7 = new Vector3();
	public final Vector3 tmp8 = new Vector3();
	public final Matrix4 m4 = new Matrix4();
	public final BoundingBox bb = new BoundingBox();
	public final BoundingBox bb2 = new BoundingBox();
	public final BoundingBox bb3 = new BoundingBox();
	
	@Override
	public void show()
	{
		instance = this;
		
		Gdx.app.log("GameLayer.create", "Seed: " + seed + "");
		MathUtils.random.setSeed(seed);
		
		modelBatch = new ModelBatch(Gdx.files.internal("shader/shader.vs"), Gdx.files.internal("shader/shader.fs"));
		camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.1f;
		camera.far = 10000;
		controller = new FirstPersonCameraController(camera)
		{
			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer)
			{
				if (middleDown || Gdx.app.getType() == ApplicationType.Android) super.touchDragged(screenX, screenY, pointer);
				return false;
			}
		};
		controller.setDegreesPerPixel(rotateSpeed);
		controller.setVelocity(velocity);
		Vloxlands.currentGame.getMultiplexer().addProcessor(controller);
		
		shapeRenderer = new ShapeRenderer();
		
		new MeshingThread();
		
		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f), new ColorAttribute(ColorAttribute.Fog, 0.5f, 0.8f, 0.85f, 1.f));
		lights.add(new DirectionalLight().set(255, 255, 255, 0, -1, 1));
		
		int w = MathUtils.random(1, 1);
		int d = MathUtils.random(1, 1);
		
		world = new World(w, d);
		Gdx.app.log("GameLayer.create", "World size: " + w + "x" + d);
	}
	
	public void doneLoading()
	{
		for (Item item : Item.getAll())
			item.onLoaded();
		
		Vector3 p = world.getIslands()[0].pos;
		Human human = new Human(Island.SIZE / 2 - 5, Island.SIZE / 4 * 3 + p.y, Island.SIZE / 2);
		human.setTool(Item.get("PICKAXE"));
		world.addEntity(human);
		
		world.getIslands()[0].addStructure(new Warehouse(Island.SIZE / 2 - 2, Island.SIZE / 4 * 3, Island.SIZE / 2 - 2), false);
		
		worldMiddle = new Vector3(p.x * Island.SIZE + Island.SIZE / 2, p.y + Island.SIZE, p.z * Island.SIZE + Island.SIZE / 2);
		
		camera.position.set(worldMiddle);
		camera.position.y -= Island.SIZE / 4;
		camera.position.z -= Island.SIZE / 2;
		camera.rotate(Vector3.Y, 180);
		
		doneLoading = true;
		// sky = new ModelInstance(assets.get("models/sky/sky.g3db", Model.class));
	}
	
	@Override
	public void render(float delta)
	{
		if (!doneLoading) return;
		Gdx.gl.glClearColor(0.5f, 0.8f, 0.85f, 1);
		controller.update();
		
		world.update();
		modelBatch.begin(camera);
		world.render(modelBatch, lights);
		// modelBatch.render(sky, lights);
		modelBatch.end();
		
		if (Vloxlands.showPathDebug)
		{
			renderBFS();
			renderAStar();
		}
		
		if (BFS.lastTarget != null)
		{
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glLineWidth(2);
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.identity();
			shapeRenderer.translate(world.getIslands()[0].pos.x + BFS.lastTarget.x, world.getIslands()[0].pos.y + BFS.lastTarget.y + 1.01f, world.getIslands()[0].pos.z + BFS.lastTarget.z);
			shapeRenderer.rotate(1, 0, 0, 90);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.x(0.5f, 0.5f, 0.49f);
			shapeRenderer.end();
			Gdx.gl.glLineWidth(1);
		}
	}
	
	public void renderAStar()
	{
		for (AStarNode node : AStar.openList)
		{
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glLineWidth(2);
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.identity();
			shapeRenderer.translate(world.getIslands()[0].pos.x + node.x, world.getIslands()[0].pos.y + node.y + 1.01f, world.getIslands()[0].pos.z + node.z);
			shapeRenderer.rotate(1, 0, 0, 90);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.x(0.5f, 0.5f, 0.49f);
			shapeRenderer.end();
			Gdx.gl.glLineWidth(1);
		}
		for (AStarNode node : AStar.closedList)
		{
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.identity();
			shapeRenderer.translate(world.getIslands()[0].pos.x + node.x, world.getIslands()[0].pos.y + node.y + 1.01f, world.getIslands()[0].pos.z + node.z);
			shapeRenderer.rotate(1, 0, 0, 90);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.BLUE);
			shapeRenderer.x(0.5f, 0.5f, 0.49f);
			shapeRenderer.end();
		}
		if (AStar.lastPath != null)
		{
			for (Vector3 v : AStar.lastPath)
			{
				Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
				shapeRenderer.setProjectionMatrix(camera.combined);
				shapeRenderer.identity();
				shapeRenderer.translate(world.getIslands()[0].pos.x + v.x, world.getIslands()[0].pos.y + v.y + 1.01f, world.getIslands()[0].pos.z + v.z);
				shapeRenderer.rotate(1, 0, 0, 90);
				shapeRenderer.begin(ShapeType.Line);
				shapeRenderer.setColor(Color.RED);
				shapeRenderer.x(0.5f, 0.5f, 0.49f);
				shapeRenderer.end();
			}
		}
	}
	
	public void renderBFS()
	{
		for (BFSNode node : BFS.queue)
		{
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glLineWidth(2);
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.identity();
			shapeRenderer.translate(world.getIslands()[0].pos.x + node.x, world.getIslands()[0].pos.y + node.y + 1.01f, world.getIslands()[0].pos.z + node.z);
			shapeRenderer.rotate(1, 0, 0, 90);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.x(0.5f, 0.5f, 0.49f);
			shapeRenderer.end();
			Gdx.gl.glLineWidth(1);
		}
		
		if (BFS.lastTarget != null)
		{
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glLineWidth(2);
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.identity();
			shapeRenderer.translate(world.getIslands()[0].pos.x + BFS.lastTarget.x, world.getIslands()[0].pos.y + BFS.lastTarget.y + 1.01f, world.getIslands()[0].pos.z + BFS.lastTarget.z);
			shapeRenderer.rotate(1, 0, 0, 90);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.MAGENTA);
			shapeRenderer.x(0.5f, 0.5f, 0.49f);
			shapeRenderer.end();
			Gdx.gl.glLineWidth(1);
		}
	}
	
	@Override
	public void tick(int tick)
	{
		world.tick(tick++);
	}
	
	@Override
	public void resize(int width, int height)
	{
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}
	
	public void pickRay(boolean hover, boolean lmb, int x, int y)
	{
		Ray ray = camera.getPickRay(x, y);
		
		if (hover)
		{
			Entity hovered = null;
			float distance = 0;
			
			for (Entity entity : world.getEntities())
			{
				entity.hovered = false;
				if (!entity.inFrustum) continue;
				
				entity.getWorldBoundingBox(bb);
				
				if (Intersector.intersectRayBounds(ray, bb, tmp))
				{
					float dst = ray.origin.dst(tmp);
					if (hovered == null || dst < distance)
					{
						hovered = entity;
						distance = dst;
					}
				}
			}
			
			for (Island i : world.getIslands())
			{
				if (i == null) continue;
				for (Structure structure : i.getStructures())
				{
					structure.hovered = false;
					if (!structure.inFrustum) continue;
					
					structure.getWorldBoundingBox(bb);
					
					if (Intersector.intersectRayBounds(ray, bb, tmp))
					{
						float dst = ray.origin.dst(tmp);
						if (hovered == null || dst < distance)
						{
							hovered = structure;
							distance = dst;
						}
					}
				}
			}
			
			if (hovered != null) hovered.hovered = true;
		}
		else
		{
			boolean entitySelected = false;
			for (Entity entity : world.getEntities())
			{
				entity.wasSelected = entity.selected;
				entity.selected = false;
				if (entity.inFrustum && entity.hovered)
				{
					entity.selected = true;
					entitySelected = true;
				}
			}
			
			for (Island i : world.getIslands())
			{
				if (i == null) continue;
				for (Structure structure : i.getStructures())
				{
					structure.wasSelected = structure.selected;
					structure.selected = false;
					if (structure.inFrustum && structure.hovered)
					{
						structure.selected = true;
						entitySelected = true;
					}
				}
			}
			
			for (int i = 0; i < world.getIslands().length; i++)
			{
				Island island = world.getIslands()[i];
				if (island == null) continue;
				
				float distance = 0;
				Chunk chunk = null;
				Vector3 voxel = null;
				
				for (Chunk c : island.getChunks())
				{
					if (c.inFrustum && !c.isEmpty())
					{
						tmp1.set(island.pos.x + c.pos.x, island.pos.y + c.pos.y, island.pos.z + c.pos.z);
						tmp2.set(tmp1.cpy().add(Chunk.SIZE, Chunk.SIZE, Chunk.SIZE));
						
						bb.set(tmp1, tmp2);
						c.selectedVoxel.set(-1, 0, 0);
						if (Intersector.intersectRayBounds(ray, bb, null) && c.pickVoxel(ray, tmp5, tmp6))
						{
							float dist = ray.origin.dst(tmp5);
							if ((chunk == null || dist < distance) && dist <= pickRayMaxDistance)
							{
								intersection.set(tmp5);
								distance = dist;
								voxel = tmp6.cpy();
								chunk = c;
							}
						}
					}
				}
				
				if (chunk != null && !entitySelected)
				{
					// -- determine selectedVoxelFace -- //
					Direction dir = null;
					float distanc = 0;
					Vector3 is2 = new Vector3();
					byte air = Voxel.get("AIR").getId();
					
					for (Direction d : Direction.values())
					{
						tmp7.set(island.pos.x + chunk.pos.x + voxel.x + d.dir.x, island.pos.y + chunk.pos.y + voxel.y + d.dir.y, island.pos.z + chunk.pos.z + voxel.z + d.dir.z);
						tmp8.set(tmp7.cpy().add(1, 1, 1));
						bb3.set(tmp7, tmp8);
						
						if (island.get(chunk.pos.x + voxel.x + d.dir.x, chunk.pos.y + voxel.y + d.dir.y, chunk.pos.z + voxel.z + d.dir.z) != air) continue;
						
						if (Intersector.intersectRayBounds(ray, bb3, is2))
						{
							float dist = ray.origin.dst(is2);
							if (dir == null || dist < distanc)
							{
								intersection2.set(is2);
								distanc = dist;
								dir = d;
							}
						}
					}
					
					chunk.selectedVoxel.set(voxel);
					EventDispatcher.dispatchVoxelSelection(new VoxelSelection(i, Voxel.getForId(chunk.get((int) voxel.x, (int) voxel.y, (int) voxel.z)), voxel.cpy().add(chunk.pos), dir), lmb);
				}
			}
		}
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		pickRay(true, false, screenX, screenY);
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		if (button == Buttons.MIDDLE)
		{
			middleDown = true;
			Gdx.input.setCursorCatched(true);
		}
		else pickRay(false, button == Buttons.LEFT, screenX, screenY);
		
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		if (button == Buttons.MIDDLE)
		{
			middleDown = false;
			Gdx.input.setCursorCatched(false);
		}
		return false;
	}
}
