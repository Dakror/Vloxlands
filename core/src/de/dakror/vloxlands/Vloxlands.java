package de.dakror.vloxlands;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Chunk;
import de.dakror.vloxlands.game.world.World;

public class Vloxlands extends ApplicationAdapter
{
	public static Vloxlands currentGame;
	
	public PerspectiveCamera camera;
	
	World world;
	ModelBatch modelBatch;
	Environment lights;
	CameraInputController controller;
	FPSLogger logger;
	long last;
	
	Vector3 worldMiddle;
	
	@Override
	public void create()
	{
		currentGame = this; // hi
		
		Voxel.loadVoxels();
		modelBatch = new ModelBatch();
		DefaultShader.defaultCullFace = GL20.GL_FRONT;
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.5f;
		camera.far = 1000;
		
		controller = new CameraInputController(camera);
		Gdx.input.setInputProcessor(controller);
		
		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		lights.add(new DirectionalLight().set(255, 255, 255, 0, -1, 1));
		
		world = new World(8, 8, 8);
		worldMiddle = world.size.cpy().scl(0.5f * Chunk.SIZE);
		camera.position.set(worldMiddle.cpy());
		camera.position.y += world.size.y;
		camera.position.z += 10;
		camera.rotate(new Vector3(1, 0, 0), -45);
		logger = new FPSLogger();
		controller.target = worldMiddle;
		controller.translateTarget = false;
		controller.forwardTarget = false;
		controller.rotateLeftKey = 0;
		controller.rotateRightKey = 0;
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
		
		logger.log();
		if (System.currentTimeMillis() - last >= 1000)
		{
			Gdx.app.log("Chunks", world.visibleChunks + " / " + world.chunks.length);
			last = System.currentTimeMillis();
		}
	}
}
