package de.dakror.vloxlands;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.game.world.World;
import de.dakror.vloxlands.render.MeshingThread;

public class Vloxlands extends ApplicationAdapter
{
	public static final long seed = (long) (Math.random() * Long.MAX_VALUE);
	
	public static Vloxlands currentGame;
	
	public PerspectiveCamera camera;
	
	World world;
	ModelBatch modelBatch;
	Environment lights;
	CameraInputController controller;
	SpriteBatch spriteBatch;
	BitmapFont font;
	
	long last;
	int tick;
	
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
		controller = new CameraInputController(camera)
		{
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
		// controller.setVelocity(20);
		Gdx.input.setInputProcessor(controller);
		
		new MeshingThread();
		
		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		lights.add(new DirectionalLight().set(255, 255, 255, 0, -1, 1));
		
		world = new World(1, 1);
		world.addIsland(0, 0);
		Vector3 p = world.getIslands()[0].pos;
		worldMiddle = new Vector3(p.x * Island.SIZE + Island.SIZE / 2, p.y + Island.SIZE, p.z * Island.SIZE + Island.SIZE / 2);
		
		controller.target = worldMiddle;
		controller.translateTarget = false;
		controller.forwardTarget = false;
		controller.rotateLeftKey = 0;
		controller.rotateRightKey = 0;
		controller.rotateButton = Buttons.RIGHT;
		controller.translateButton = Buttons.LEFT;
		
		camera.position.set(worldMiddle);
		camera.position.y -= Island.SIZE / 4;
		camera.position.z += Island.SIZE / 3 * 2;
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
		
		if (last == 0) last = System.currentTimeMillis();
		
		spriteBatch.begin();
		font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 0, Gdx.graphics.getHeight());
		font.draw(spriteBatch, "C: " + world.visibleChunks + " / " + world.chunks, 0, Gdx.graphics.getHeight() - 20);
		font.draw(spriteBatch, "X: " + camera.position.x, 0, Gdx.graphics.getHeight() - 40);
		font.draw(spriteBatch, "Y: " + camera.position.y, 0, Gdx.graphics.getHeight() - 60);
		font.draw(spriteBatch, "Z: " + camera.position.z, 0, Gdx.graphics.getHeight() - 80);
		spriteBatch.end();
		
		if (System.currentTimeMillis() - last >= 16) // ~60 a sec
		{
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
	}
}
