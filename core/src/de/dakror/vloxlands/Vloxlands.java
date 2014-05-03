package de.dakror.vloxlands;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.dakror.vloxlands.game.entity.Human;
import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Chunk;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.game.world.World;
import de.dakror.vloxlands.render.MeshingThread;
import de.dakror.vloxlands.screen.LoadingScreen;
import de.dakror.vloxlands.util.Direction;

public class Vloxlands extends Game implements InputProcessor, GestureListener
{
	public static final long seed = (long) (Math.random() * Long.MAX_VALUE);
	public static final float velocity = 10;
	public static final float rotateSpeed = 0.2f;
	public static final float pickRayMaxDistance = 30f;
	
	public static Vloxlands currentGame;
	public static World world;
	public static AssetManager assets;
	public static PerspectiveCamera camera;
	
	public Environment lights;
	
	ModelBatch modelBatch;
	FirstPersonCameraController controller;
	SpriteBatch spriteBatch;
	BitmapFont font;
	
	Vector3 selectedVoxel;
	Voxel selectedVoxelType;
	Direction selectedVoxelFace;
	int selectedIsland;
	Voxel placeVoxelType;
	int placeVoxelTypeScroll;
	
	// -- on screen controls -- //
	Stage stage;
	OrthographicCamera camera2;
	Touchpad moveTouchpad;
	TouchpadStyle touchpadStyle;
	Skin touchpadSkin;
	Drawable touchpadBack;
	Drawable touchpadFront;
	
	long last;
	int tick;
	
	public static boolean debug;
	public static boolean showChunkBorders;
	boolean middleDown;
	boolean leftDown;
	
	Vector3 worldMiddle;
	
	public Vector3 intersection = new Vector3();
	public Vector3 intersection2 = new Vector3();
	
	Vector2 lastMouseTap = new Vector2();
	
	// -- temp -- //
	public Vector3 tmp1 = new Vector3();
	Vector3 tmp2 = new Vector3();
	public Vector3 tmp3 = new Vector3();
	public Vector3 tmp4 = new Vector3();
	Vector3 tmp5 = new Vector3();
	Vector3 tmp6 = new Vector3();
	public Vector3 tmp7 = new Vector3();
	public Vector3 tmp8 = new Vector3();
	BoundingBox bb = new BoundingBox();
	public BoundingBox bb2 = new BoundingBox();
	public BoundingBox bb3 = new BoundingBox();
	public Matrix4 m4 = new Matrix4();
	
	@Override
	public void create()
	{
		Bullet.init();
		
		currentGame = this;
		Gdx.app.log("Vloxlands.create", "Seed: " + seed + "");
		MathUtils.random.setSeed(seed);
		
		Voxel.loadVoxels();
		
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		assets = new AssetManager();
		modelBatch = new ModelBatch(Gdx.files.internal("shader/shader.vs"), Gdx.files.internal("shader/shader.fs"));
		camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.001f;
		camera.far = 100;
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
		
		new MeshingThread();
		
		InputMultiplexer multiplexer = new InputMultiplexer();
		
		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f), new ColorAttribute(ColorAttribute.Fog, 0.5f, 0.8f, 0.85f, 1.f));
		lights.add(new DirectionalLight().set(255, 255, 255, 0, -1, 1));
		
		int w = MathUtils.random(1, 1);
		int d = MathUtils.random(1, 1);
		
		world = new World(w, d);
		Gdx.app.log("Vloxlands.create", "World size: " + w + "x" + d);
		
		// -- stage -- //
		if (Gdx.app.getType() == ApplicationType.Android)
		{
			camera2 = new OrthographicCamera();
			
			touchpadSkin = new Skin();
			touchpadSkin.add("touchpadBack", new Texture("img/gui/touchpadBack.png"));
			touchpadSkin.add("touchpadFront", new Texture("img/gui/touchpadFront.png"));
			
			touchpadStyle = new TouchpadStyle();
			touchpadBack = touchpadSkin.getDrawable("touchpadBack");
			touchpadFront = touchpadSkin.getDrawable("touchpadFront");
			
			touchpadStyle.background = touchpadBack;
			touchpadStyle.knob = touchpadFront;
			
			int size = (int) (160 * (Gdx.graphics.getHeight() / 720f));
			int size2 = (int) (100 * (Gdx.graphics.getHeight() / 720f));
			
			touchpadStyle.knob.setMinWidth(size2);
			touchpadStyle.knob.setMinHeight(size2);
			
			int delta = 30;
			
			moveTouchpad = new Touchpad(10, touchpadStyle);
			moveTouchpad.setBounds(delta, delta, size, size);
			
			stage = new Stage(new ScreenViewport(camera2));
			stage.addActor(moveTouchpad);
			
			multiplexer.addProcessor(stage);
		}
		
		multiplexer.addProcessor(new GestureDetector(this));
		multiplexer.addProcessor(this);
		multiplexer.addProcessor(controller);
		Gdx.input.setInputProcessor(multiplexer);
		
		setScreen(new LoadingScreen());
	}
	
	public void doneLoading()
	{
		Vector3 p = world.getIslands()[0].pos;
		world.addEntity(new Human(Island.SIZE / 2 - 2, Island.SIZE / 4 * 3 + 10 + p.y, Island.SIZE / 2 - 2));
		worldMiddle = new Vector3(p.x * Island.SIZE + Island.SIZE / 2, p.y + Island.SIZE, p.z * Island.SIZE + Island.SIZE / 2);
		
		camera.position.set(worldMiddle);
		camera.position.y -= Island.SIZE / 4;
		camera.position.z += Island.SIZE / 2;
	}
	
	@Override
	public void render()
	{
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		if (getScreen() != null)
		{
			super.render();
		}
		else
		{
			Gdx.gl.glClearColor(0.5f, 0.8f, 0.85f, 1);
			controller.update();
			
			world.update();
			modelBatch.begin(camera);
			world.render(modelBatch, lights);
			modelBatch.end();
			
			if (Gdx.app.getType() == ApplicationType.Android)
			{
				camera2.update();
				
				stage.act(Gdx.graphics.getDeltaTime());
				stage.draw();
			}
			
			if (last == 0) last = System.currentTimeMillis();
			
			if (System.currentTimeMillis() - last >= 16) // ~60 a sec
			{
				if (Gdx.app.getType() == ApplicationType.Android)
				{
					float delta = Gdx.graphics.getDeltaTime();
					camera.position.add(camera.direction.cpy().nor().scl(delta * moveTouchpad.getKnobPercentY() * velocity));
					camera.position.add(camera.direction.cpy().crs(camera.up).nor().scl(delta * moveTouchpad.getKnobPercentX() * velocity));
				}
				
				world.tick(tick++);
				last = System.currentTimeMillis();
			}
		}
		
		if (debug)
		{
			spriteBatch.begin();
			
			font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 0, Gdx.graphics.getHeight());
			font.draw(spriteBatch, "C: " + world.visibleChunks + " / " + world.chunks, 0, Gdx.graphics.getHeight() - 20);
			font.draw(spriteBatch, "X: " + camera.position.x, 0, Gdx.graphics.getHeight() - 40);
			font.draw(spriteBatch, "Y: " + camera.position.y, 0, Gdx.graphics.getHeight() - 60);
			font.draw(spriteBatch, "Z: " + camera.position.z, 0, Gdx.graphics.getHeight() - 80);
			font.draw(spriteBatch, "Seed: " + seed, 0, Gdx.graphics.getHeight() - 100);
			font.draw(spriteBatch, "Sel. Voxel: " + (selectedVoxelType != null ? selectedVoxelType.getName() : " N/A"), 0, Gdx.graphics.getHeight() - 120);
			font.draw(spriteBatch, "Place: " + (placeVoxelType != null ? placeVoxelType.getName() : " N/A"), 0, Gdx.graphics.getHeight() - 140);
			spriteBatch.end();
		}
	}
	
	@Override
	public void resize(int width, int height)
	{
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
		if (Gdx.app.getType() == ApplicationType.Android) camera2.update();
	}
	
	@Override
	public void pause()
	{}
	
	@Override
	public void resume()
	{}
	
	@Override
	public void dispose()
	{}
	
	public void pickRay()
	{
		Ray ray = camera.getPickRay(lastMouseTap.x, lastMouseTap.y);
		selectedVoxelType = null;
		selectedVoxel = null;
		selectedIsland = -1;
		
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
					c.selected = false;
					c.selectedVoxel = null;
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
			
			if (chunk != null)
			{
				chunk.selected = true;
				chunk.selectedVoxel = voxel;
				
				selectedVoxelType = Voxel.getForId(chunk.get((int) voxel.x, (int) voxel.y, (int) voxel.z));
				selectedVoxel = voxel.cpy().add(chunk.pos);
				
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
				
				selectedVoxelFace = dir;
				selectedIsland = i;
			}
		}
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		lastMouseTap.set(screenX, screenY);
		pickRay();
		
		return false;
	}
	
	@Override
	public boolean tap(float x, float y, int count, int button)
	{
		if (Gdx.app.getType() == ApplicationType.Android)
		{
			lastMouseTap.set(x, y);
			pickRay();
		}
		
		return false;
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button)
	{
		return false;
	}
	
	@Override
	public boolean longPress(float x, float y)
	{
		return false;
	}
	
	@Override
	public boolean fling(float velocityX, float velocityY, int button)
	{
		return false;
	}
	
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY)
	{
		return false;
	}
	
	@Override
	public boolean panStop(float x, float y, int pointer, int button)
	{
		return false;
	}
	
	@Override
	public boolean zoom(float initialDistance, float distance)
	{
		return false;
	}
	
	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2)
	{
		return false;
	}
	
	@Override
	public boolean keyDown(int keycode)
	{
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode)
	{
		if (keycode == Keys.F1) debug = !debug;
		if (keycode == Keys.F2) showChunkBorders = !showChunkBorders;
		if (keycode == Keys.F11)
		{
			if (Gdx.graphics.isFullscreen()) Gdx.graphics.setDisplayMode(1280, 720, false);
			else Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
		}
		return false;
	}
	
	@Override
	public boolean keyTyped(char character)
	{
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
		if (button == Buttons.RIGHT && selectedVoxel != null && selectedIsland > -1)
		{
			world.getIslands()[selectedIsland].set(selectedVoxel.x, selectedVoxel.y, selectedVoxel.z, Voxel.get("AIR").getId());
			world.getIslands()[selectedIsland].recalculate();
			
			selectedVoxel = null;
			selectedVoxelType = null;
			pickRay();
		}
		if (button == Buttons.LEFT && selectedVoxelFace != null && placeVoxelType != null && selectedIsland > -1)
		{
			leftDown = true;
			world.getIslands()[selectedIsland].set(selectedVoxel.x + selectedVoxelFace.dir.x, selectedVoxel.y + selectedVoxelFace.dir.y, selectedVoxel.z + selectedVoxelFace.dir.z, placeVoxelType.getId());
			world.getIslands()[selectedIsland].recalculate();
			
			pickRay();
		}
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
		if (button == Buttons.LEFT) leftDown = false;
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		return false;
	}
	
	@Override
	public boolean scrolled(int amount)
	{
		Array<?> v = Voxel.getAll();
		
		placeVoxelTypeScroll += amount;
		placeVoxelTypeScroll %= v.size;
		placeVoxelTypeScroll = placeVoxelTypeScroll < 0 ? v.size - 1 : placeVoxelTypeScroll;
		
		placeVoxelType = (Voxel) v.get(placeVoxelTypeScroll);
		
		return false;
	}
}
