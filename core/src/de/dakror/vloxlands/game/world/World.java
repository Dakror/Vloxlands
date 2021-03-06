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


package de.dakror.vloxlands.game.world;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import de.dakror.vloxlands.Config;
import de.dakror.vloxlands.Vloxlands;
import de.dakror.vloxlands.ai.path.AStar;
import de.dakror.vloxlands.ai.path.Path;
import de.dakror.vloxlands.game.entity.Entity;
import de.dakror.vloxlands.game.entity.creature.Creature;
import de.dakror.vloxlands.game.entity.creature.Human;
import de.dakror.vloxlands.game.entity.structure.NodeType;
import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.query.PathBundle;
import de.dakror.vloxlands.game.query.Query;
import de.dakror.vloxlands.game.query.Query.Queryable;
import de.dakror.vloxlands.util.interf.Savable;
import de.dakror.vloxlands.util.interf.Tickable;

/**
 * @author Dakror
 */
public class World implements RenderableProvider, Tickable, Queryable, Savable {
	public static final Color SELECTION = Color.WHITE;
	
	public static final int MAX_HEIGHT = 512;
	
	public static Material[][] dataMaps;
	int dataMap;
	
	public static Material highlight;
	
	Island[] islands;
	
	int width, depth;
	
	public int visibleChunks, loadedChunks, chunks, visibleEntities, totalEntities;
	
	public static final float gap = 0.01f;
	
	public World(int width, int depth) {
		this.width = width;
		this.depth = depth;
		
		islands = new Island[width * depth];
		
		highlight = new Material(TextureAttribute.createDiffuse(Vloxlands.assets.get("img/transparent.png", Texture.class)), ColorAttribute.createDiffuse(SELECTION));
		
		dataMaps = new Material[Config.dataMaps.length][2];
		for (int i = 0; i < dataMaps.length; i++) {
			Material trp = new Material(TextureAttribute.createDiffuse(Vloxlands.assets.get("img/datamaps/" + Config.dataMaps[i].toLowerCase() + ".png", Texture.class)), new BlendingAttribute());
			Material opq = Config.dataMapFullBlending[i] ? trp : new Material(TextureAttribute.createDiffuse(Vloxlands.assets.get("img/datamaps/" + Config.dataMaps[i].toLowerCase() + ".png", Texture.class)));
			dataMaps[i] = new Material[] { opq, trp };
		}
	}
	
	/**
	 * @param x in index space
	 * @param y in pos space
	 * @param z in index space
	 */
	public void addIsland(int x, int z, Island island) {
		islands[z * width + x] = island;
		island.index.set(x, 0, z);
		chunks += Island.CHUNKS * Island.CHUNKS * Island.CHUNKS;
	}
	
	@Override
	public void tick(int tick) {
		for (Island island : islands)
			if (island != null) island.tick(tick);
	}
	
	public void update(float delta) {
		for (Island island : islands)
			if (island != null) island.update(delta);
	}
	
	public Island[] getIslands() {
		return islands;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getDepth() {
		return depth;
	}
	
	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		visibleChunks = 0;
		loadedChunks = 0;
		for (Island island : islands) {
			if (island != null && island.inFrustum) {
				island.getRenderables(renderables, pool);
				visibleChunks += island.visibleChunks;
				loadedChunks += island.loadedChunks;
			}
		}
	}
	
	public void render(ModelBatch batch, Environment environment) {
		batch.render(this, environment);
		visibleEntities = 0;
		totalEntities = 0;
		
		for (Island island : islands) {
			island.render(batch, environment);
			totalEntities += island.getStructureCount();
		}
	}
	
	@Override
	public PathBundle query(Query query) {
		if (query.island == null) {
			Gdx.app.error("World.query", "You must specify an island index because they can't be connected yet! Return null.");
			return null;
		}
		
		Structure structure = query.sourceStructure;
		Creature creature = query.sourceCreature;
		Path path = null;
		float distance = 0;
		
		if (structure == null && creature == null) {
			Gdx.app.error("World.query", "You have to specify either a source Creature or Structure when querying! Return null.");
			return null;
		}
		
		if (query.searchingStructure) {
			if (query.sourceCreature == null) {
				Gdx.app.error("World.query", "You should specify a source Creature when querying a Structure! Return null.");
				return null;
			}
			Vector3 v = query.pathStart != null ? query.pathStart : query.sourceCreature.getVoxelBelow();
			
			for (Entity s : query.island.entities) {
				if (!(s instanceof Structure)) continue;
				if (s == query.sourceStructure) continue;
				if (!query.searchedClass.isAssignableFrom(s.getClass())) continue;
				if (query.mustWork && !((Structure) s).isWorking()) continue;
				if (query.mustBeEmpty && ((Structure) s).getInventory().getCount() > 0) continue;
				if (query.mustBeFull && !((Structure) s).getInventory().isFull()) continue;
				if (query.mustHaveCapacity && ((Structure) s).getInventory().isFull()) continue;
				if (query.mustHaveCapacityForTransportedItemStack && query.transportedItemStack != null && !query.transportedItemStack.isNull() && ((Structure) s).getInventory().getCount() + query.transportedItemStack.getAmount() > ((Structure) s).getInventory().getCapacity()) continue;
				if (query.searchedNodeType != null && !((Structure) s).hasStructureNode(query.searchedNodeType)) continue;
				if (query.searchedNodeName != null && !((Structure) s).hasStructureNode(query.searchedNodeName)) continue;
				if (query.searchedItemStack != null && !((Structure) s).getInventory().contains(query.searchedItemStack)) continue;
				if (query.searchedToolType != null && !((Structure) s).getInventory().contains(query.searchedToolType)) continue;
				
				NodeType type = query.searchedNodeType != null ? query.searchedNodeType : NodeType.target;
				Path p = AStar.findPath(v, ((Structure) s).getStructureNode(v, type, query.searchedNodeName).pos.cpy().add(((Structure) s).getVoxelPos()), query.sourceCreature, type.useGhostTarget);
				if (p == null) continue;
				
				float dist = p.length();
				if (path == null || (query.takeClosest && dist < distance) || (!query.takeClosest && dist > distance)) {
					distance = dist;
					path = p;
					structure = (Structure) s;
				}
			}
		} else {
			NodeType type = query.searchedNodeType != null ? query.searchedNodeType : NodeType.target;
			
			for (Entity e : query.island.entities) {
				if (!(e instanceof Creature)) continue;
				if (e == query.sourceCreature) continue;
				if (!e.getClass().equals(query.searchedClass)) continue;
				if (query.searchedClass.equals(Human.class)) {
					if (query.mustIdle && !((Human) e).isIdle()) continue;
					if (query.mustBeFull && !((Human) e).getCarryingItemStack().isFull()) continue;
					if (query.mustBeEmpty && !((Human) e).getCarryingItemStack().isNull()) continue;
					if (query.mustHaveCapacity && ((Human) e).getCarryingItemStack().isFull()) continue;
					if (query.mustHaveCapacityForTransportedItemStack && !((Human) e).getCarryingItemStack().canAdd(query.transportedItemStack)) continue;
				}
				
				Vector3 v = ((Creature) e).getVoxelBelow();
				Vector3 to = query.sourceStructure.getStructureNode(v, type, query.searchedNodeName).pos.cpy().add(query.sourceStructure.getVoxelPos());
				Path p = AStar.findPath(v, query.sourceCreature != null ? query.sourceCreature.getVoxelBelow() : to, query.sourceCreature != null ? query.sourceCreature : (Creature) e, query.sourceCreature != null ? true : type.useGhostTarget);
				if (p == null) continue;
				
				float dist = p.length();
				if (path == null || (query.takeClosest && dist < distance) || (!query.takeClosest && dist > distance)) {
					distance = dist;
					path = p;
					creature = (Creature) e;
				}
			}
		}
		
		if (path == null) return null;
		return new PathBundle(path, structure, creature, query);
	}
	
	@Override
	public void save(ByteArrayOutputStream baos) throws IOException {
		baos.write(width);
		baos.write(depth);
		
		for (Island i : islands)
			i.save(baos);
	}
	
	public void setDataMap(int dataMap) {
		this.dataMap = Math.abs(dataMap) % dataMaps.length;
	}
	
	public int getDataMap() {
		return dataMap;
	}
	
	public Material getOpaque() {
		return dataMaps[dataMap][0];
	}
	
	public Material getTransp() {
		return dataMaps[dataMap][1];
	}
	
	public Material getDefOpaque() {
		return dataMaps[0][0];
	}
	
	public Material getDefTransp() {
		return dataMaps[0][1];
	}
	
	public static float calculateRelativeUplift(float y) {
		return (1 - y / MAX_HEIGHT) * 8 + 0.1f;
	}
}
