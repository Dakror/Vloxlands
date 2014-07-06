package de.dakror.vloxlands.layer;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.Config;
import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.ai.AStar;
import de.dakror.vloxlands.ai.BFS;
import de.dakror.vloxlands.ai.node.AStarNode;
import de.dakror.vloxlands.ai.node.BFSNode;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.creature.Creature;
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
import de.dakror.vloxlands.util.event.SelectionListener;
import de.dakror.vloxlands.util.event.VoxelSelection;
import de.dakror.vloxlands.util.math.CustomizableFrustum;

/**
 * @author Dakror
 */
public class GameLayer extends Layer
{
	public static long seed = (long) (Math.random() * Long.MAX_VALUE);
	public static final float velocity = 10;
	public static final float rotateSpeed = 0.2f;
	public static final float pickRayMaxDistance = 150f;

	public static GameLayer instance;

	public static World world;
	public static Camera camera;
	public static ShapeRenderer shapeRenderer;

	public Environment env;

	public Array<SelectionListener> listeners = new Array<SelectionListener>();

	public Environment minimapEnv;
	public Camera minimapCamera;
	public ModelBatch minimapBatch;
	public Island activeIsland;

	ModelBatch modelBatch;
	CameraInputController controller;

	boolean middleDown;
	boolean doneLoading;

	ModelInstance sky;

	int tick;
	int ticksForTravel;
	int startTick;
	Vector3 controllerTarget = new Vector3();
	Vector3 cameraPos = new Vector3();
	Vector3 target = new Vector3();
	Vector3 targetDirection = new Vector3();
	Vector3 targetUp = new Vector3();

	Vector2 mouseDown = new Vector2();

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
		modal = true;
		instance = this;

		Gdx.app.log("GameLayer.show", "Seed: " + seed + "");
		MathUtils.random = new Random(seed);

		minimapBatch = new ModelBatch(Gdx.files.internal("shader/shader.vs"), Gdx.files.internal("shader/shader.fs"));
		minimapCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		minimapCamera.near = 0.1f;
		minimapCamera.far = pickRayMaxDistance;
		minimapEnv = new Environment();
		minimapEnv.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		minimapEnv.add(new DirectionalLight().set(255, 255, 255, 0, -1, 1));

		modelBatch = new ModelBatch(Gdx.files.internal("shader/shader.vs"), Gdx.files.internal("shader/shader.fs"));

		camera = new PerspectiveCamera(Config.pref.getInteger("fov"), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.1f;
		camera.far = pickRayMaxDistance;
		controller = new CameraInputController(camera)
		{
			private final Vector3 tmpV1 = new Vector3();
			private final Vector3 tmpV2 = new Vector3();


			@Override
			protected boolean process(float deltaX, float deltaY, int button)
			{
				if (button == rotateButton && Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) return false;
				return super.process(deltaX, deltaY, button);
			}

			@Override
			public boolean zoom(float amount)
			{
				if (!alwaysScroll && activateKey != 0 && !activatePressed) return false;

				tmpV1.set(camera.direction).scl(amount);
				tmpV2.set(camera.position).add(tmpV1);

				if (tmpV2.dst(target) > 5)
				{
					camera.translate(tmpV1);
					if (scrollTarget) target.add(tmpV1);
					if (autoUpdate) camera.update();
					return true;
				}

				return false;
			}
		};
		controller.translateUnits = 20;
		controller.rotateLeftKey = -1;
		controller.rotateRightKey = -1;
		controller.forwardKey = -1;
		controller.backwardKey = -1;
		controller.translateButton = -1;
		controller.rotateButton = Buttons.MIDDLE;
		Vloxlands.currentGame.getMultiplexer().addProcessor(controller);

		shapeRenderer = new ShapeRenderer();

		new MeshingThread();

		env = new Environment();
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f), new ColorAttribute(ColorAttribute.Fog, 0.5f, 0.8f, 0.85f, 1.f));
		env.add(new DirectionalLight().set(255, 255, 255, 0, -1, 1));

		int w = MathUtils.random(1, 5);
		int d = MathUtils.random(1, 5);

		world = new World(w, d);
		Gdx.app.log("GameLayer.show", "World size: " + w + "x" + d);
	}

	public void doneLoading()
	{
		for (Item item : Item.getAll())
			item.onLoaded();

		Vector3 p = world.getIslands()[0].pos;
		Human human = new Human(Island.SIZE / 2 - 5, Island.SIZE / 4 * 3 + p.y, Island.SIZE / 2);
		human.setTool(Item.get("PICKAXE"));
		world.addEntity(human);
		
		world.getIslands()[0].addStructure(new Warehouse(Island.SIZE / 2 - 2, Island.SIZE / 4 * 3, Island.SIZE / 2 - 2), false, true);
		world.getIslands()[0].calculateInitBalance();

		focusIsland(world.getIslands()[0], true);

		doneLoading = true;

		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// for (int i = 0; i < 512; i++)
		// {
		// world.getIslands()[0].getChunk(i).encode(baos);
		// if (baos.size() > 0) break;
		// }
		// sky = new ModelInstance(assets.get("models/sky/sky.g3db", Model.class));
	}

	public void focusIsland(Island island, boolean initial)
	{
		Vector3 islandCenter = new Vector3(island.pos.x + Island.SIZE / 2, island.pos.y + Island.SIZE / 4 * 3, island.pos.z + Island.SIZE / 2);

		if (!initial)
		{
			target.set(islandCenter).add(-Island.SIZE / 3, Island.SIZE / 3, -Island.SIZE / 3);
			if (target.equals(camera.position))
			{
				camera.position.set(islandCenter).add(-Island.SIZE / 3, Island.SIZE / 3, -Island.SIZE / 3);
				controller.target.set(islandCenter);
				camera.lookAt(islandCenter);

				controller.update();
				camera.update();
				return;
			}

			ticksForTravel = (int) camera.position.dst(target);
			activeIsland = island;

			Vector3 pos = camera.position.cpy();
			Vector3 dir = camera.direction.cpy();
			Vector3 up = camera.up.cpy();

			camera.position.set(islandCenter).add(-Island.SIZE / 3, Island.SIZE / 3, -Island.SIZE / 3);
			controller.target.set(islandCenter);
			camera.lookAt(islandCenter);

			targetDirection.set(camera.direction);
			targetUp.set(camera.up);

			camera.position.set(pos);
			camera.direction.set(dir);
			camera.up.set(up);

			startTick = tick;
		}
		else
		{
			camera.position.set(islandCenter).add(-Island.SIZE / 3, Island.SIZE / 3, -Island.SIZE / 3);
			controller.target.set(islandCenter);
			camera.lookAt(islandCenter);

			controller.update();
			camera.update();
		}
	}

	@Override
	public void render(float delta)
	{
		if (!doneLoading) return;
		Gdx.gl.glClearColor(0.5f, 0.8f, 0.85f, 1);
		controller.update();

		world.update();
		modelBatch.begin(camera);
		world.render(modelBatch, env);
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
		this.tick = tick;
		world.tick(tick);

		if (activeIsland != null && startTick > 0)
		{
			camera.position.interpolate(target, (tick - startTick) / (float) ticksForTravel, Interpolation.linear);
			camera.direction.interpolate(targetDirection, (tick - startTick) / (float) ticksForTravel, Interpolation.linear);
			camera.up.interpolate(new Vector3(0, 1, 0), (tick - startTick) / (float) ticksForTravel, Interpolation.linear);

			if (tick >= startTick + ticksForTravel || camera.position.dst(target) < 0.1f)
			{
				Vector3 islandCenter = new Vector3(activeIsland.pos.x + Island.SIZE / 2, activeIsland.pos.y + Island.SIZE / 4 * 3, activeIsland.pos.z + Island.SIZE / 2);
				controller.target.set(islandCenter);
				camera.position.set(islandCenter).add(-Island.SIZE / 3, Island.SIZE / 3, -Island.SIZE / 3);
				camera.lookAt(islandCenter);
				startTick = 0;
			}

			controller.update();
			camera.update();
		}
	}

	@Override
	public void resize(int width, int height)
	{
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();

		minimapCamera.viewportWidth = width;
		minimapCamera.viewportHeight = height;
		minimapCamera.update();
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
			Entity selectedEntity = null;
			Structure selectedStructure = null;
			Island selectedIsland = null;
			Chunk selectedChunk = null;
			Vector3 selectedVoxel = new Vector3();

			float distance = 0;
			for (Entity entity : world.getEntities())
			{
				entity.wasSelected = entity.selected;
				if (lmb) entity.selected = false;
				float dst = ray.origin.dst(entity.posCache);
				if (entity.inFrustum && entity.hovered && (distance == 0 || dst < distance) && dst < pickRayMaxDistance)
				{
					distance = dst;
					selectedEntity = entity;
					break;
				}
			}

			for (Island i : world.getIslands())
			{
				if (i == null) continue;
				for (Structure structure : i.getStructures())
				{
					structure.wasSelected = structure.selected;
					if (lmb) structure.selected = false;
					float dst = ray.origin.dst(structure.posCache);
					if (structure.inFrustum && structure.hovered && (distance == 0 || dst < distance) && dst < pickRayMaxDistance)
					{
						distance = dst;
						selectedStructure = structure;
					}
				}

				for (Chunk c : i.getChunks())
				{
					if (c.inFrustum && !c.isEmpty())
					{
						tmp1.set(i.pos.x + c.pos.x, i.pos.y + c.pos.y, i.pos.z + c.pos.z);
						tmp2.set(tmp1.cpy().add(Chunk.SIZE, Chunk.SIZE, Chunk.SIZE));

						bb.set(tmp1, tmp2);
						c.selectedVoxel.set(-1, 0, 0);
						if (Intersector.intersectRayBounds(ray, bb, null) && c.pickVoxel(ray, tmp5, tmp6))
						{
							float dst = ray.origin.dst(tmp5);
							if ((distance == 0 || dst < distance) && dst <= pickRayMaxDistance)
							{
								distance = dst;
								selectedVoxel.set(tmp6);
								selectedChunk = c;
								selectedIsland = i;
							}
						}
					}
				}
			}

			if (selectedChunk != null)
			{
				// -- determine selectedVoxelFace -- //
				Direction dir = null;
				float distanc = 0;
				Vector3 is2 = new Vector3();
				byte air = Voxel.get("AIR").getId();

				for (Direction d : Direction.values())
				{
					tmp7.set(selectedIsland.pos.x + selectedChunk.pos.x + selectedVoxel.x + d.dir.x, selectedIsland.pos.y + selectedChunk.pos.y + selectedVoxel.y + d.dir.y, selectedIsland.pos.z + selectedChunk.pos.z + selectedVoxel.z + d.dir.z);
					tmp8.set(tmp7.cpy().add(1, 1, 1));
					bb3.set(tmp7, tmp8);

					if (selectedIsland.get(selectedChunk.pos.x + selectedVoxel.x + d.dir.x, selectedChunk.pos.y + selectedVoxel.y + d.dir.y, selectedChunk.pos.z + selectedVoxel.z + d.dir.z) != air) continue;

					if (Intersector.intersectRayBounds(ray, bb3, is2))
					{
						float dist = ray.origin.dst(is2);
						if (dir == null || dist < distanc)
						{
							distanc = dist;
							dir = d;
						}
					}
				}

				selectedChunk.selectedVoxel.set(selectedVoxel);

				for (SelectionListener sl : listeners)
					sl.onVoxelSelection(new VoxelSelection(selectedIsland, Voxel.getForId(selectedChunk.get((int) selectedVoxel.x, (int) selectedVoxel.y, (int) selectedVoxel.z)), selectedVoxel.cpy().add(selectedChunk.pos), dir), lmb);
			}
			else if (selectedStructure != null)
			{
				selectedStructure.selected = true;
				for (SelectionListener sl : listeners)
					sl.onStructureSelection(selectedStructure, lmb);
			}
			else if (selectedEntity != null && selectedEntity instanceof Creature)
			{
				selectedEntity.selected = true;
				for (SelectionListener sl : listeners)
					sl.onCreatureSelection((Creature) selectedEntity, lmb);
			}
		}
	}

	public void selectionBox(Rectangle rectangle)
	{
		CustomizableFrustum frustum = new CustomizableFrustum(rectangle);
		camera.update();
		frustum.update(camera.invProjectionView);
		Vector3 origin = camera.unproject(new Vector3(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		boolean anyEntitySelected = false;

		for (Entity entity : world.getEntities())
		{
			entity.wasSelected = entity.selected;
			entity.selected = false;
			entity.getWorldBoundingBox(bb);

			float dst = origin.dst(entity.posCache);
			if (entity.inFrustum && frustum.boundsInFrustum(bb) && dst < pickRayMaxDistance)
			{
				entity.selected = true;
				anyEntitySelected = true;
				break;
			}
		}

		if (!anyEntitySelected)
		{
			for (Island i : world.getIslands())
			{
				if (i == null) continue;
				for (Structure structure : i.getStructures())
				{
					structure.wasSelected = structure.selected;
					structure.selected = false;
					structure.getWorldBoundingBox(bb);

					float dst = origin.dst(structure.posCache);
					if (structure.inFrustum && frustum.boundsInFrustum(bb) && dst < pickRayMaxDistance) structure.selected = true;
				}
			}
		}
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		if (middleDown && Gdx.input.isKeyPressed(Keys.CONTROL_LEFT))
		{
			float f = 0.1f;

			controller.target.y = controllerTarget.y + (screenY - mouseDown.y) * f;
			camera.position.y = cameraPos.y + (screenY - mouseDown.y) * f;
			camera.update();
			controller.update();
		}
		return false;
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
		mouseDown.set(screenX, screenY);

		if (button == Buttons.MIDDLE)
		{
			controllerTarget.set(controller.target);
			cameraPos.set(camera.position);
			middleDown = true;
			Gdx.input.setCursorCatched(true);
		}
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button)
	{
		if (button != Buttons.MIDDLE) pickRay(false, button == Buttons.LEFT, (int) x, (int) y);
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

	public void addListener(SelectionListener value)
	{
		listeners.insert(0, value);
	}

	public boolean removeListener(SelectionListener value)
	{
		return listeners.removeValue(value, true);
	}
}
