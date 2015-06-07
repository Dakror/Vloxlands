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


package de.dakror.vloxlands.game.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.game.Game;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.game.world.World;
import de.dakror.vloxlands.ui.PinnableWindow;
import de.dakror.vloxlands.ui.RevolverSlot;
import de.dakror.vloxlands.util.CSVReader;
import de.dakror.vloxlands.util.base.EntityBase;
import de.dakror.vloxlands.util.interf.Savable;
import de.dakror.vloxlands.util.math.Bits;

/**
 * @author Dakror
 */
public class Entity extends EntityBase implements Telegraph, Savable {
	public static final int LINES[][] = { { 0, 1 }, { 0, 3 }, { 0, 4 }, { 6, 7 }, { 6, 5 }, { 6, 2 }, { 1, 5 }, { 2, 3 }, { 4, 5 }, { 3, 7 }, { 1, 2 }, { 7, 4 } };
	
	static HashMap<Byte, Class<?>> idToClassMap = new HashMap<Byte, Class<?>>();
	static HashMap<Class<?>, Byte> classToIdMap = new HashMap<Class<?>, Byte>();
	
	protected ModelInstance modelInstance;
	protected Array<ModelInstance> subs;
	
	protected byte id;
	protected int level;
	protected String name;
	
	protected float weight;
	protected float uplift;
	protected boolean modelVisible;
	protected boolean additionalVisible;
	protected boolean visible;
	protected boolean spawned;
	
	public boolean inFrustum;
	public boolean hovered;
	public boolean wasSelected;
	public boolean selected;
	
	protected boolean markedForRemoval;
	protected BoundingBox boundingBox;
	protected final Vector3 dimensions = new Vector3();
	public final Vector3 blockTrn = new Vector3();
	protected Island island;
	
	protected AnimationController animationController;
	
	public final Vector3 posCache = new Vector3();
	public final Quaternion rotCache = new Quaternion();
	public final Vector3 tmpV = new Vector3();
	public final Quaternion tmpQ = new Quaternion();
	final Matrix4 tmp = new Matrix4();
	
	public Entity(float x, float y, float z, String model) {
		id = classToIdMap.get(getClass());
		
		modelInstance = new ModelInstance(Vloxlands.assets.get("models/" + model, Model.class));
		modelInstance.calculateBoundingBox(boundingBox = new BoundingBox());
		
		if (boundingBox.getDimensions().x % 1 != 0 || boundingBox.getDimensions().y % 1 != 0 || boundingBox.getDimensions().z % 1 != 0) {
			blockTrn.set(((float) Math.ceil(boundingBox.getDimensions().x) - boundingBox.getDimensions().x) / 2, 1 - boundingBox.getCenter().y, ((float) Math.ceil(boundingBox.getDimensions().z) - boundingBox.getDimensions().z) / 2);
		}
		blockTrn.add(boundingBox.getDimensions().cpy().scl(0.5f));
		
		modelInstance.transform.translate(x, y, z).translate(blockTrn);
		
		animationController = new AnimationController(modelInstance);
		markedForRemoval = false;
		
		subs = new Array<ModelInstance>();
		for (Node n : modelInstance.nodes.get(0).children) {
			if (n.id.startsWith("model:")) {
				subs.add(new ModelInstance(Vloxlands.assets.get("models/" + model.replace(model.substring(model.lastIndexOf("/") + 1), n.id.replace("model:", "")) + ".vxi", Model.class), n.translation));
			}
		}
		
		modelInstance.transform.getTranslation(posCache);
		
		level = 0;
		modelVisible = true;
		additionalVisible = true;
		visible = true;
		
		dimensions.set(Math.max(Math.round(boundingBox.getDimensions().x), 1), Math.max(Math.round(boundingBox.getDimensions().y), 1), Math.max(Math.round(boundingBox.getDimensions().z), 1));
		Game.instance.addListener(this);
	}
	
	public void setIsland(Island island) {
		this.island = island;
	}
	
	public Island getIsland() {
		return island;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public float getWeight() {
		return weight;
	}
	
	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	public float getUplift() {
		return uplift;
	}
	
	public void setUplift(float uplift) {
		this.uplift = uplift;
	}
	
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}
	
	public AnimationController getAnimationController() {
		return animationController;
	}
	
	public ModelInstance getModelInstance() {
		return modelInstance;
	}
	
	public byte getId() {
		return id;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public boolean isMarkedForRemoval() {
		return markedForRemoval;
	}
	
	@Override
	public void tick(int tick) {
		modelInstance.transform.getTranslation(posCache);
		modelInstance.transform.getRotation(rotCache);
		inFrustum = Game.camera.frustum.boundsInFrustum(boundingBox.getCenter().x + posCache.x, boundingBox.getCenter().y + posCache.y, boundingBox.getCenter().z + posCache.z, boundingBox.getDimensions().x / 2, boundingBox.getDimensions().y / 2, boundingBox.getDimensions().z / 2);
	}
	
	public void getWorldBoundingBox(BoundingBox bb) {
		bb.min.set(boundingBox.min).add(posCache);
		bb.max.set(boundingBox.max).add(posCache);
		
		bb.set(bb.min, bb.max);
	}
	
	public void render(ModelBatch batch, Environment environment, boolean minimapMode) {
		if (!visible) return;
		
		if (modelVisible) {
			batch.render(modelInstance, environment);
			for (ModelInstance mi : subs) {
				tmp.set(mi.transform);
				modelInstance.transform.getTranslation(posCache);
				modelInstance.transform.getRotation(rotCache);
				mi.transform.getTranslation(tmpV);
				mi.transform.getRotation(tmpQ);
				mi.transform.idt();
				
				mi.transform.translate(posCache).rotate(rotCache).translate(tmpV);
				
				mi.transform.rotate(tmpQ);
				
				batch.render(mi, environment);
				
				mi.transform.set(tmp);
			}
		}
		if (additionalVisible) renderAdditional(batch, environment);
		
		if ((hovered || selected) && !minimapMode) {
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glLineWidth(selected ? 3 : 2);
			Vloxlands.shapeRenderer.setProjectionMatrix(Game.camera.combined);
			Vloxlands.shapeRenderer.identity();
			Vloxlands.shapeRenderer.translate(posCache.x, posCache.y - boundingBox.getDimensions().y / 2 + boundingBox.getCenter().y + World.gap, posCache.z);
			Vloxlands.shapeRenderer.rotate(1, 0, 0, 90);
			Vloxlands.shapeRenderer.begin(ShapeType.Line);
			Vloxlands.shapeRenderer.setColor(World.SELECTION);
			Vloxlands.shapeRenderer.rect(-(float) Math.ceil(boundingBox.getDimensions().x) / 2, -(float) Math.ceil(boundingBox.getDimensions().z) / 2, (float) Math.ceil(boundingBox.getDimensions().x), (float) Math.ceil(boundingBox.getDimensions().z));
			Vloxlands.shapeRenderer.end();
			Gdx.gl.glLineWidth(1);
		}
		
		if (Vloxlands.wireframe && !minimapMode) {
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Vloxlands.shapeRenderer.setProjectionMatrix(Game.camera.combined);
			Vloxlands.shapeRenderer.identity();
			Vloxlands.shapeRenderer.translate(posCache.x, posCache.y - boundingBox.getDimensions().y / 2 + boundingBox.getCenter().y, posCache.z);
			Vloxlands.shapeRenderer.begin(ShapeType.Line);
			Vloxlands.shapeRenderer.setColor(Color.RED);
			Vloxlands.shapeRenderer.box(-boundingBox.getDimensions().x / 2, 0, boundingBox.getDimensions().z / 2, boundingBox.getDimensions().x, boundingBox.getDimensions().y, boundingBox.getDimensions().z);
			Vloxlands.shapeRenderer.end();
		}
	}
	
	public void renderAdditional(ModelBatch batch, Environment environment) {}
	
	public void update(float delta) {
		animationController.update(delta);
	}
	
	@Override
	public void dispose() {
		Game.instance.removeListener(this);
	}
	
	public void kill() {
		markedForRemoval = true;
	}
	
	public boolean isSpawned() {
		return spawned;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}
	
	@Override
	public void save(ByteArrayOutputStream baos) throws IOException {
		baos.write(id);
		
		Bits.putMatrix4(baos, modelInstance.transform);
		
		Bits.putInt(baos, level);
		
		Bits.putBoolean(baos, visible);
		Bits.putBoolean(baos, spawned);
		Bits.putBoolean(baos, markedForRemoval);
	}
	
	public void setUI(PinnableWindow window, Object... params) {}
	
	public void setActions(RevolverSlot parent) {}
	
	public boolean intersects(Entity o) {
		float lx = Math.abs(posCache.x - o.posCache.x);
		float sumx = (dimensions.x / 2.0f) + (o.dimensions.x / 2.0f);
		
		float ly = Math.abs(posCache.y - o.posCache.y);
		float sumy = (dimensions.y / 2.0f) + (o.dimensions.y / 2.0f);
		
		float lz = Math.abs(posCache.z - o.posCache.z);
		float sumz = (dimensions.z / 2.0f) + (o.dimensions.z / 2.0f);
		
		return (lx < sumx && ly < sumy && lz < sumz);
	}
	
	// -- events -- //
	
	public void onSpawn() {
		spawned = true;
	}
	
	// -- statics -- //
	
	public static void loadEntities() {
		CSVReader csv = new CSVReader(Gdx.files.internal("data/entities.csv"));
		csv.readRow(); // headers
		
		String cell;
		Class<?> c = null;
		boolean hasCell0 = false;
		while ((cell = csv.readNext()) != null) {
			if (cell.trim().length() == 0) continue;
			try {
				if (csv.getIndex() == 0) {
					hasCell0 = true;
					c = Class.forName("de.dakror.vloxlands.game.entity." + cell);
				} else {
					if (!hasCell0) continue;
					byte b = (byte) Integer.parseInt(cell.trim());
					
					idToClassMap.put(b, c);
					classToIdMap.put(c, b);
					
					hasCell0 = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Gdx.app.log("Entity.loadEntities", idToClassMap.size() + " entities loaded.");
	}
	
	public static Entity getForId(byte id, float x, float y, float z) {
		Class<?> c = idToClassMap.get(id);
		if (c == null) {
			Gdx.app.error("Entity.getForId", "No Entity found for id=" + id + "!");
			return null;
		}
		try {
			return (Entity) c.getConstructor(float.class, float.class, float.class).newInstance(x, y, z);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
