package de.dakror.vloxlands;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.game.world.World;
import de.dakror.vloxlands.render.MeshingThread;

public class Vloxlands extends ApplicationAdapter
{
	public static final long seed = (long) (Math.random() * Long.MAX_VALUE);
	public static final float velocity = 10;
	public static final float rotate = 0.2f;
	
	public static Vloxlands currentGame;
	
	
	public PerspectiveCamera camera;
	
	World world;
	ModelBatch modelBatch;
	Environment lights;
	FirstPersonCameraController controller;
	SpriteBatch spriteBatch;
	BitmapFont font;
	
	// -- on screen controls -- //
	Stage stage;
	OrthographicCamera camera2;
	Touchpad touchpad;
	TouchpadStyle touchpadStyle;
	Skin touchpadSkin;
	Drawable touchpadBack;
	Drawable touchpadFront;
	
	long last;
	int tick;
	
	boolean debug = true;
	
	Vector3 worldMiddle;
	
	@Override
	public void create()
	{
		currentGame = this;
		Gdx.app.log("Seed", seed + "");
		MathUtils.random.setSeed(seed);
		
		Voxel.loadVoxels();
		
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		modelBatch = new ModelBatch(Gdx.files.internal("shader/shader.vs"), Gdx.files.internal("shader/shader.fs"));
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.5f;
		camera.far = 1000;
		controller = new FirstPersonCameraController(camera)
		{
			@Override
			public boolean keyUp(int keycode)
			{
				if (keycode == Keys.F1) debug = !debug;
				return super.keyUp(keycode);
			}
			
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button)
			{
				Gdx.input.setCursorCatched(true);
				return super.touchDown(screenX, screenY, pointer, button);
			}
			
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button)
			{
				Gdx.input.setCursorCatched(false);
				return super.touchUp(screenX, screenY, pointer, button);
			}
		};
		controller.setDegreesPerPixel(rotate);
		controller.setVelocity(velocity);
		
		new MeshingThread();
		
		InputMultiplexer multiplexer = new InputMultiplexer();
		
		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		lights.add(new DirectionalLight().set(255, 255, 255, 0, -1, 1));
		
		world = new World(1, 1);
		world.addIsland(0, 0);
		Vector3 p = world.getIslands()[0].pos;
		worldMiddle = new Vector3(p.x * Island.SIZE + Island.SIZE / 2, p.y + Island.SIZE, p.z * Island.SIZE + Island.SIZE / 2);
		
		// controller.target = worldMiddle;
		// controller.translateTarget = false;
		// controller.forwardTarget = false;
		// controller.rotateLeftKey = 0;
		// controller.rotateRightKey = 0;
		// controller.rotateButton = Buttons.RIGHT;
		// controller.translateButton = Buttons.LEFT;
		
		camera.position.set(worldMiddle);
		camera.position.y -= Island.SIZE / 4;
		camera.position.z += Island.SIZE / 2;
		
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
			
			touchpad = new Touchpad(10, touchpadStyle);
			touchpad.setBounds(15, 15, size, size);
			
			stage = new Stage(new ScreenViewport(camera2));
			stage.addActor(touchpad);
			
			multiplexer.addProcessor(stage);
		}
		
		multiplexer.addProcessor(controller);
		Gdx.input.setInputProcessor(multiplexer);
	}
	
	@Override
	public void render()
	{
		Gdx.gl.glClearColor(0.5f, 0.8f, 0.85f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		modelBatch.begin(camera);
		modelBatch.render(world, lights);
		modelBatch.end();
		
		controller.update();
		if (Gdx.app.getType() == ApplicationType.Android)
		{
			camera2.update();
			
			stage.act(Gdx.graphics.getDeltaTime());
			stage.draw();
		}
		
		if (last == 0) last = System.currentTimeMillis();
		
		if (debug)
		{
			spriteBatch.begin();
			font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 0, Gdx.graphics.getHeight());
			font.draw(spriteBatch, "C: " + world.visibleChunks + " / " + world.chunks, 0, Gdx.graphics.getHeight() - 20);
			font.draw(spriteBatch, "X: " + camera.position.x, 0, Gdx.graphics.getHeight() - 40);
			font.draw(spriteBatch, "Y: " + camera.position.y, 0, Gdx.graphics.getHeight() - 60);
			font.draw(spriteBatch, "Z: " + camera.position.z, 0, Gdx.graphics.getHeight() - 80);
			font.draw(spriteBatch, "Seed: " + seed, 0, Gdx.graphics.getHeight() - 100);
			spriteBatch.end();
		}
		
		if (System.currentTimeMillis() - last >= 16) // ~60 a sec
		{
			if (Gdx.app.getType() == ApplicationType.Android)
			{
				float delta = Gdx.graphics.getDeltaTime();
				camera.position.add(camera.direction.cpy().nor().scl(delta * touchpad.getKnobPercentY() * velocity));
				camera.position.add(camera.direction.cpy().crs(camera.up).nor().scl(delta * touchpad.getKnobPercentX() * velocity));
			}
			
			world.tick(tick++);
			last = System.currentTimeMillis();
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
}
