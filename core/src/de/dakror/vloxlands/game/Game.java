/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package de.dakror.vloxlands.game;

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
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
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
import de.dakror.vloxlands.ai.path.BFS;
import de.dakror.vloxlands.ai.path.node.BFSNode;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.statics.StaticEntity;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.entity.structure.Towncenter;
import de.dakror.vloxlands.game.item.Item;
import de.dakror.vloxlands.game.item.ItemStack;
import de.dakror.vloxlands.game.query.VoxelPos;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Chunk;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.game.world.World;
import de.dakror.vloxlands.layer.Layer;
import de.dakror.vloxlands.render.DDirectionalShadowLight;
import de.dakror.vloxlands.render.MeshingThread;
import de.dakror.vloxlands.util.Direction;
import de.dakror.vloxlands.util.event.SelectionListener;
import de.dakror.vloxlands.util.event.VoxelSelection;
import de.dakror.vloxlands.util.math.CustomizableFrustum;

/**
 * @author Dakror
 */
@SuppressWarnings("deprecation")
public class Game extends Layer {
	public static long seed = (long) (Math.random() * Long.MAX_VALUE);
	public static final float velocity = 10;
	public static final float rotateSpeed = 0.2f;
	public static float pickRayMaxDistance = 150f;
	
	public static final int dayInTicks = 72020; // 1 ingame day = 72020 ticks = 1200s = 20min
	
	public static Game instance;
	
	public static World world;
	public static Camera camera;
	public static float time = 0.99999999999f;
	
	public Environment env;
	
	public Array<SelectionListener> listeners = new Array<SelectionListener>();
	
	public Environment minimapEnv;
	public Camera minimapCamera;
	public ModelBatch minimapBatch;
	
	public StaticEntity cursorEntity;
	boolean cursorEntityPlacable;
	boolean cursorEntityContinousPlacing;
	Array<Material> defaultCursorEntityMaterials;
	
	public String activeAction = "";
	public Island activeIsland;
	public DirectionalShadowLight shadowLight;
	DirectionalLight directionalLight;
	public CameraInputController controller;
	
	ModelBatch modelBatch;
	ModelBatch shadowBatch;
	
	boolean middleDown;
	boolean doneLoading;
	
	ModelInstance sky;
	
	int tick;
	int ticksForTravel;
	int startTick;
	
	public boolean regionSelectionMode = false;
	boolean regionSelectionLMB;
	
	public Vector3 hoveredVoxel = new Vector3();
	public Vector3 selectedVoxel = new Vector3();
	public Vector3 selectionStartVoxel = new Vector3(-1, 0, 0);
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
	public void show() {
		modal = true;
		instance = this;
		
		Gdx.app.log("GameLayer.show", "Seed: " + seed + "");
		MathUtils.random = new Random(seed);
		
		modelBatch = new ModelBatch(Gdx.files.internal("shader/shader.vs"), Gdx.files.internal("shader/shader.fs"));
		minimapBatch = new ModelBatch(Gdx.files.internal("shader/shader.vs"), Gdx.files.internal("shader/shader.fs"));
		
		camera = new PerspectiveCamera(Config.fov, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.1f;
		camera.far = pickRayMaxDistance;
		controller = new CameraInputController(camera) {
			private final Vector3 tmpV1 = new Vector3();
			private final Vector3 tmpV2 = new Vector3();
			
			@Override
			protected boolean process(float deltaX, float deltaY, int button) {
				if (button == rotateButton && Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) return false;
				
				if (button == rotateButton) {
					tmpV1.set(camera.direction).crs(camera.up).y = 0f;
					camera.rotateAround(target, tmpV1.nor(), deltaY * rotateAngle);
					
					float dot = camera.direction.dot(Vector3.Y);
					if (dot < -0.95f) camera.rotateAround(target, tmpV1.nor(), -deltaY * rotateAngle);
					camera.rotateAround(target, Vector3.Y, deltaX * -rotateAngle);
				} else if (button == translateButton) {
					camera.translate(tmpV1.set(camera.direction).crs(camera.up).nor().scl(-deltaX * translateUnits));
					camera.translate(tmpV2.set(camera.up).scl(-deltaY * translateUnits));
					if (translateTarget) target.add(tmpV1).add(tmpV2);
				} else if (button == forwardButton) {
					camera.translate(tmpV1.set(camera.direction).scl(deltaY * translateUnits));
					if (forwardTarget) target.add(tmpV1);
				}
				if (autoUpdate) camera.update();
				return true;
			}
			
			@Override
			public boolean zoom(float amount) {
				if (!alwaysScroll && activateKey != 0 && !activatePressed) return false;
				
				tmpV1.set(camera.direction).scl(amount);
				tmpV2.set(camera.position).add(tmpV1);
				
				if (tmpV2.dst(target) > 5) {
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
		Vloxlands.instance.getMultiplexer().addProcessor(controller);
		minimapCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		minimapCamera.near = 0.1f;
		minimapCamera.far = pickRayMaxDistance;
		minimapEnv = new Environment();
		minimapEnv.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		minimapEnv.add(new DirectionalLight().set(1f, 1f, 1f, -0.5f, -0.5f, -0.5f));
		minimapEnv.add(new DirectionalLight().set(0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f));
		
		shadowBatch = new ModelBatch(new DepthShaderProvider());
		
		Vloxlands.shapeRenderer = new ShapeRenderer();
		
		new MeshingThread();
		
		env = new Environment();
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f), new ColorAttribute(ColorAttribute.Fog, 0.5f, 0.8f, 0.85f, 1.f));
		env.add(directionalLight = new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -0.5f, -0.5f));
		
		env.add((shadowLight = new DDirectionalShadowLight(Config.shadowQuality, 128, 128, camera.near, camera.far)).set(0.6f, 0.6f, 0.6f, 0, -0.5f, time));
		env.shadowMap = shadowLight;
		
		// int w = MathUtils.random(1, 5);
		// int d = MathUtils.random(1, 5);
		
		world = new World(1, 1);
		// world = new World(w, d);
		// Gdx.app.log("GameLayer.show", "World size: " + w + "x" + d);
	}
	
	public void doneLoading() {
		for (Item item : Item.getAll())
			item.onLoaded();
		
		focusIsland(world.getIslands()[0], true);
		
		Human human = new Human(Island.SIZE / 2 - 5, Island.SIZE / 4 * 3, Island.SIZE / 2);
		activeIsland.addEntity(human, false, false);
		human = new Human(Island.SIZE / 2 - 4, Island.SIZE / 4 * 3, Island.SIZE / 2);
		activeIsland.addEntity(human, false, false);
		
		Towncenter tc = new Towncenter(Island.SIZE / 2 - 2, Island.SIZE / 4 * 3, Island.SIZE / 2 - 2);
		activeIsland.addEntity(tc, false, true);
		tc.setBuilt(true);
		tc.getInnerInventory().add(new ItemStack(Item.get("AXE"), 5));
		tc.getInnerInventory().add(new ItemStack(Item.get("PICKAXE"), 5));
		tc.getInnerInventory().add(new ItemStack(Item.get("SHOVEL"), 5));
		tc.getInnerInventory().add(new ItemStack(Item.get("HAMMER"), 5));
		tc.getInnerInventory().add(new ItemStack(Item.get("HOE"), 5));
		
		tc.getInnerInventory().add(new ItemStack(Item.get("WOODEN_LOG"), 40));
		tc.getInnerInventory().add(new ItemStack(Item.get("IRON_INGOT"), 5));
		
		doneLoading = true;
	}
	
	public void focusIsland(Island island, boolean initial) {
		Vector3 islandCenter = new Vector3(island.pos.x + Island.SIZE / 2, island.pos.y + Island.SIZE / 4 * 3, island.pos.z + Island.SIZE / 2);
		activeIsland = island;
		selectedVoxel.set(-1, 0, 0);
		if (!initial) {
			target.set(islandCenter).add(-Island.SIZE / 3, Island.SIZE / 3, -Island.SIZE / 3);
			if (target.equals(camera.position)) {
				camera.position.set(islandCenter).add(-Island.SIZE / 3, Island.SIZE / 3, -Island.SIZE / 3);
				controller.target.set(islandCenter);
				camera.lookAt(islandCenter);
				
				controller.update();
				camera.update();
				return;
			}
			
			ticksForTravel = (int) camera.position.dst(target) * Config.getGameSpeed();
			
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
		} else {
			camera.position.set(islandCenter).add(-Island.SIZE / 3, Island.SIZE / 3, -Island.SIZE / 3);
			controller.target.set(islandCenter);
			camera.lookAt(islandCenter);
			
			controller.update();
			camera.update();
		}
	}
	
	@Override
	public void render(float delta) {
		if (!doneLoading) return;
		controller.update();
		((PerspectiveCamera) camera).fieldOfView = Config.fov;
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		shadowLight.begin(controller.target, camera.direction);
		shadowBatch.begin(shadowLight.getCamera());
		world.render(shadowBatch, null);
		shadowBatch.end();
		shadowLight.end();
		
		Gdx.gl.glClearColor(0.5f, 0.8f, 0.85f, 1);
		
		modelBatch.begin(camera);
		world.render(modelBatch, env);
		if (!Config.paused) world.update(delta);
		// modelBatch.render(sky, env);
		if (cursorEntity != null) {
			cursorEntity.update(delta);
			cursorEntity.render(modelBatch, env, false);
		}
		modelBatch.end();
		
		if (selectionStartVoxel.x > -1 && selectedVoxel.x > -1) {
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glEnable(GL20.GL_BLEND);
			
			float minX = Math.min(selectionStartVoxel.x, selectedVoxel.x);
			float maxX = Math.max(selectionStartVoxel.x, selectedVoxel.x);
			
			float minY = Math.min(selectionStartVoxel.y, selectedVoxel.y);
			float maxY = Math.max(selectionStartVoxel.y, selectedVoxel.y);
			
			float minZ = Math.min(selectionStartVoxel.z, selectedVoxel.z);
			float maxZ = Math.max(selectionStartVoxel.z, selectedVoxel.z);
			
			Vloxlands.shapeRenderer.begin(ShapeType.Filled);
			Vloxlands.shapeRenderer.setProjectionMatrix(camera.combined);
			Vloxlands.shapeRenderer.identity();
			Vloxlands.shapeRenderer.translate(activeIsland.pos.x + minX, activeIsland.pos.y + minY, activeIsland.pos.z + maxZ + 1.01f);
			Vloxlands.shapeRenderer.setColor(0, 1, 0, 0.3f);
			Vloxlands.shapeRenderer.box(-0.005f, -0.005f, -0.005f, (maxX - minX) + 1.01f, (maxY - minY) + 1.01f, (maxZ - minZ) + 1.01f);
			Vloxlands.shapeRenderer.end();
		}
		
		if (Vloxlands.showPathDebug) {
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Vloxlands.shapeRenderer.begin(ShapeType.Filled);
			Vloxlands.shapeRenderer.setProjectionMatrix(camera.combined);
			for (BFSNode node : BFS.visited) {
				Vloxlands.shapeRenderer.identity();
				Vloxlands.shapeRenderer.translate(activeIsland.pos.x + node.x, activeIsland.pos.y + node.y, activeIsland.pos.z + node.z + 1.01f);
				Vloxlands.shapeRenderer.setColor(1, 1, 1, 0.3f);
				Vloxlands.shapeRenderer.box(-0.005f, -0.005f, -0.005f, 1.01f, 1.01f, 1.01f);
			}
			Vloxlands.shapeRenderer.end();
		}
	}
	
	@Override
	public void tick(int tick) {
		this.tick = tick;
		
		if (!Config.paused) {
			time -= 0.00002777f;
			if (time <= -0.99999999999f) time = 0.99999999999f;
			
			float t = time * MathUtils.PI;
			
			float x = MathUtils.sin(t) * 0.5f;
			float z = MathUtils.cos(t);
			
			float light = MathUtils.cos(t - MathUtils.PI / 2) * 0.5f + 0.3f;
			
			shadowLight.set(light - 0.1f, light, light, x, -0.5f, z);
			directionalLight.set(light, light, light, x, -0.5f, z);
			world.tick(tick);
		}
		if (cursorEntity != null) cursorEntity.tick(tick);
		
		if (activeIsland != null && startTick > 0) {
			camera.position.interpolate(target, (tick - startTick) / (float) (ticksForTravel * Config.getGameSpeed()), Interpolation.linear);
			camera.direction.interpolate(targetDirection, (tick - startTick) / (float) (ticksForTravel * Config.getGameSpeed()), Interpolation.linear);
			camera.up.interpolate(new Vector3(0, 1, 0), (tick - startTick) / (float) (ticksForTravel * Config.getGameSpeed()), Interpolation.linear);
			
			if (tick >= startTick + ticksForTravel || camera.position.dst(target) < 0.1f) {
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
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
		
		minimapCamera.viewportWidth = width;
		minimapCamera.viewportHeight = height;
		minimapCamera.update();
	}
	
	public void pickRay(boolean hover, boolean lmb, int x, int y) {
		Ray ray = camera.getPickRay(x, y);
		
		if (hover) {
			Entity hovered = null;
			float distance = 0;
			
			for (Entity e : activeIsland.getEntities()) {
				e.hovered = false;
				if (!e.isVisible()) continue;
				if (!e.inFrustum) continue;
				
				e.getWorldBoundingBox(bb);
				
				if (Intersector.intersectRayBounds(ray, bb, tmp)) {
					float dst = ray.origin.dst(tmp);
					if (hovered == null || dst < distance) {
						hovered = e;
						distance = dst;
					}
				}
			}
			
			if (hovered != null) hovered.hovered = true;
		} else {
			Entity selectedEntity = null;
			Chunk selectedChunk = null;
			Vector3 selVoxel = new Vector3();
			
			float distance = 0;
			
			for (Entity e : activeIsland.getEntities()) {
				e.wasSelected = e.selected;
				if (lmb) e.selected = false;
				float dst = ray.origin.dst(e.posCache);
				if (e.isVisible() && e.inFrustum && e.hovered && (distance == 0 || dst < distance) && dst < pickRayMaxDistance) {
					distance = dst;
					selectedEntity = e;
				}
			}
			
			for (Chunk c : activeIsland.getChunks()) {
				if (c == null) continue;
				
				if (c.inFrustum && !c.isEmpty()) {
					tmp1.set(activeIsland.pos.x + c.pos.x, activeIsland.pos.y + c.pos.y, activeIsland.pos.z + c.pos.z);
					tmp2.set(tmp1.cpy().add(Chunk.SIZE, Chunk.SIZE, Chunk.SIZE));
					
					bb.set(tmp1, tmp2);
					if (Intersector.intersectRayBounds(ray, bb, null) && c.pickVoxel(ray, tmp5, tmp6)) {
						float dst = ray.origin.dst(tmp5);
						if ((distance == 0 || dst < distance) && dst <= pickRayMaxDistance) {
							distance = dst;
							selVoxel.set(tmp6);
							selectedChunk = c;
						}
					}
				}
			}
			
			if (selectedChunk != null) {
				// -- determine selectedVoxelFace -- //
				Direction dir = null;
				float distanc = 0;
				Vector3 is2 = new Vector3();
				byte air = Voxel.get("AIR").getId();
				
				for (Direction d : Direction.values()) {
					tmp7.set(activeIsland.pos.x + selectedChunk.pos.x + selVoxel.x + d.dir.x, activeIsland.pos.y + selectedChunk.pos.y + selVoxel.y + d.dir.y, activeIsland.pos.z + selectedChunk.pos.z + selVoxel.z + d.dir.z);
					tmp8.set(tmp7.cpy().add(1, 1, 1));
					bb3.set(tmp7, tmp8);
					
					if (activeIsland.get(selectedChunk.pos.x + selVoxel.x + d.dir.x, selectedChunk.pos.y + selVoxel.y + d.dir.y, selectedChunk.pos.z + selVoxel.z + d.dir.z) != air) continue;
					
					if (Intersector.intersectRayBounds(ray, bb3, is2)) {
						float dist = ray.origin.dst(is2);
						if (dir == null || dist < distanc) {
							distanc = dist;
							dir = d;
						}
					}
				}
				
				selectedVoxel.set(selVoxel).add(selectedChunk.pos);
				
				for (SelectionListener sl : listeners)
					sl.onVoxelSelection(new VoxelSelection(activeIsland, new VoxelPos(selVoxel.cpy().add(selectedChunk.pos), selectedChunk.get((int) selVoxel.x, (int) selVoxel.y, (int) selVoxel.z)), dir), lmb);
			} else if (selectedEntity != null) {
				selVoxel.set(-1, 0, 0);
				selectedEntity.selected = true;
				if (selectedEntity instanceof Structure) {
					for (SelectionListener sl : listeners)
						sl.onStructureSelection((Structure) selectedEntity, lmb);
				} else if (selectedEntity instanceof Creature) {
					for (SelectionListener sl : listeners)
						sl.onCreatureSelection((Creature) selectedEntity, lmb);
				}
			} else {
				for (SelectionListener sl : listeners)
					sl.onNoSelection(lmb);
			}
		}
	}
	
	public Chunk pickVoxelRay(Island island, Vector3 selVoxel, boolean lmb, int x, int y) {
		Chunk selectedChunk = null;
		Ray ray = camera.getPickRay(x, y);
		
		float distance = 0;
		
		for (Chunk c : island.getChunks()) {
			if (c == null) continue;
			if (c.inFrustum && !c.isEmpty()) {
				tmp1.set(island.pos.x + c.pos.x, island.pos.y + c.pos.y, island.pos.z + c.pos.z);
				tmp2.set(tmp1.cpy().add(Chunk.SIZE, Chunk.SIZE, Chunk.SIZE));
				
				bb.set(tmp1, tmp2);
				if (Intersector.intersectRayBounds(ray, bb, null) && c.pickVoxel(ray, tmp5, tmp6)) {
					float dst = ray.origin.dst(tmp5);
					if ((distance == 0 || dst < distance) && dst <= pickRayMaxDistance) {
						distance = dst;
						selVoxel.set(tmp6).add(c.pos);
						selectedChunk = c;
					}
				}
			}
		}
		
		return selectedChunk;
	}
	
	public void selectionBox(Rectangle rectangle) {
		CustomizableFrustum frustum = new CustomizableFrustum(rectangle);
		camera.update();
		frustum.update(camera.invProjectionView);
		Vector3 origin = camera.unproject(new Vector3(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		boolean anyEntitySelected = false;
		boolean dispatched = false;
		
		for (Entity entity : activeIsland.getEntities()) {
			if (entity instanceof StaticEntity) continue;
			if (!entity.isVisible()) continue;
			entity.wasSelected = entity.selected;
			entity.selected = false;
			entity.getWorldBoundingBox(bb);
			
			float dst = origin.dst(entity.posCache);
			if (entity.inFrustum && frustum.boundsInFrustum(bb) && dst < pickRayMaxDistance) {
				entity.selected = true;
				anyEntitySelected = true;
				if (!dispatched && entity instanceof Creature) {
					for (SelectionListener sl : listeners)
						sl.onCreatureSelection((Creature) entity, true);
					dispatched = true;
				}
			}
		}
		
		if (!anyEntitySelected) {
			for (Island i : world.getIslands()) {
				if (i == null) continue;
				for (Entity e : i.getEntities()) {
					if (!(e instanceof StaticEntity)) continue;
					
					e.wasSelected = e.selected;
					e.selected = false;
					e.getWorldBoundingBox(bb);
					
					float dst = origin.dst(e.posCache);
					if (e.inFrustum && frustum.boundsInFrustum(bb) && dst < pickRayMaxDistance) e.selected = true;
				}
			}
		}
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (middleDown && Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
			float f = 0.1f;
			
			controller.target.y = controllerTarget.y + (screenY - mouseDown.y) * f;
			camera.position.y = cameraPos.y + (screenY - mouseDown.y) * f;
			camera.update();
			controller.update();
		}
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (regionSelectionMode) pickVoxelRay(activeIsland, selectedVoxel, false, screenX, screenY);
		else if (cursorEntity != null) {
			pickVoxelRay(activeIsland, hoveredVoxel, false, screenX, screenY);
			cursorEntity.getModelInstance().transform.setToTranslation(activeIsland.pos);
			cursorEntity.getModelInstance().transform.translate(hoveredVoxel).translate(cursorEntity.getBoundingBox().getDimensions().x <= 1 ? cursorEntity.blockTrn.x : 0, cursorEntity.blockTrn.y, cursorEntity.getBoundingBox().getDimensions().z <= 1 ? cursorEntity.blockTrn.z : 0);
			cursorEntity.setIsland(activeIsland);
			cursorEntity.updateVoxelPos();
			cursorEntityPlacable = cursorEntity.canBePlaced();
			
			if (defaultCursorEntityMaterials == null) {
				defaultCursorEntityMaterials = new Array<Material>();
				
				for (Material m : cursorEntity.getModelInstance().materials)
					defaultCursorEntityMaterials.add(m.copy());
			}
			
			for (int i = 0; i < cursorEntity.getModelInstance().materials.size; i++) {
				Material m = cursorEntity.getModelInstance().materials.get(i);
				
				if (!cursorEntityPlacable) {
					m.set(new BlendingAttribute(0.8f), ColorAttribute.createDiffuse(Color.RED));
				} else {
					m.remove(ColorAttribute.Diffuse);
					BlendingAttribute ba = (BlendingAttribute) defaultCursorEntityMaterials.get(i).get(BlendingAttribute.Type);
					if (ba == null) m.remove(BlendingAttribute.Type);
					else m.set(ba);
				}
			}
		} else if (activeIsland != null) pickRay(true, false, screenX, screenY);
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		mouseDown.set(screenX, screenY);
		
		if (button == Buttons.MIDDLE) {
			controllerTarget.set(controller.target);
			cameraPos.set(camera.position);
			middleDown = true;
			Gdx.input.setCursorCatched(true);
		}
		return false;
	}
	
	@Override
	public boolean tap(float x, float y, int count, int button) {
		if (!doneLoading) return false;
		
		if (button != Buttons.MIDDLE) {
			if (!regionSelectionMode) {
				if (cursorEntity != null) {
					if (button == Buttons.LEFT) {
						if (cursorEntityPlacable) {
							for (int i = 0; i < defaultCursorEntityMaterials.size; i++) {
								cursorEntity.getModelInstance().materials.set(i, defaultCursorEntityMaterials.get(i));
							}
							
							if (cursorEntity instanceof Structure) ((Structure) cursorEntity).setBuilt(false);
							cursorEntity.getModelInstance().transform.translate(-activeIsland.pos.x, -activeIsland.pos.y, -activeIsland.pos.z);
							activeIsland.addEntity(cursorEntity, true, false);
							cursorEntity.updateVoxelPos();
							
							if (!cursorEntityContinousPlacing) {
								cursorEntity = null;
								defaultCursorEntityMaterials = null;
								cursorEntityPlacable = false;
							} else {
								cursorEntity = (StaticEntity) Entity.getForId(cursorEntity.getId(), cursorEntity.posCache.x, cursorEntity.posCache.y, cursorEntity.posCache.z);
								cursorEntity.setIsland(activeIsland);
								cursorEntity.getModelInstance().transform.translate(activeIsland.pos.x, activeIsland.pos.y, activeIsland.pos.z);
								cursorEntity.updateVoxelPos();
								if (cursorEntity instanceof Structure) ((Structure) cursorEntity).setBuilt(true);
								cursorEntity.setVisible(true);
							}
						}
					} else {
						cursorEntity = null;
						defaultCursorEntityMaterials = null;
						cursorEntityPlacable = false;
					}
				} else {
					selectionStartVoxel.set(-1, 0, 0);
					pickRay(false, button == Buttons.LEFT, (int) x, (int) y);
				}
			} else {
				if (selectionStartVoxel.x == -1) {
					selectedVoxel.set(-1, 0, 0);
					pickVoxelRay(activeIsland, selectionStartVoxel, regionSelectionLMB = button == Buttons.LEFT, (int) x, (int) y);
				} else if (regionSelectionLMB == (button == Buttons.LEFT)) {
					pickVoxelRay(activeIsland, selectedVoxel, button == Buttons.LEFT, (int) x, (int) y);
					
					for (SelectionListener sl : listeners)
						sl.onVoxelRangeSelection(activeIsland, selectionStartVoxel, selectedVoxel, regionSelectionLMB);
					
					regionSelectionMode = false;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Buttons.MIDDLE) {
			middleDown = false;
			Gdx.input.setCursorCatched(false);
		}
		return false;
	}
	
	public void addListener(SelectionListener value) {
		listeners.insert(0, value);
	}
	
	public boolean removeListener(SelectionListener value) {
		return listeners.removeValue(value, true);
	}
	
	/*
	 * Only call when @param:action != null
	 */
	public void action(String action) {
		if (action.contains("|region")) {
			selectionStartVoxel.set(-1, 0, 0);
			selectedVoxel.set(-1, 0, 0);
			regionSelectionMode = true;
		}
		if (action.contains("entity")) {
			String[] a = action.split("\\|");
			String s = a[0].replace("entity:", "");
			Entity e = Entity.getForId((byte) Integer.parseInt(s), 0, 0, 0);
			if (e instanceof Structure) {
				((Structure) e).setBuilt(true);
				((Structure) e).tickRequestsEnabled = false;
			}
			e.setVisible(true);
			cursorEntity = (StaticEntity) e;
			cursorEntityContinousPlacing = a.length > 1 && a[1].equals("cont");
		}
		
		activeAction = action;
	}
}
