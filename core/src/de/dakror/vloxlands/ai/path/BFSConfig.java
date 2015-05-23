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
 

package de.dakror.vloxlands.ai.path;

import de.dakror.vloxlands.game.entity.creature.Creature;

/**
 * @author Dakror
 */
public class BFSConfig {
	byte voxel;
	byte meta;
	byte neighborMeta;
	
	float maxRange;
	
	int neighborRangeX, neighborRangeY, neighborRangeZ;
	
	boolean notMeta;
	boolean notNeighbor;
	boolean closest;
	
	Creature creature;
	
	public BFSConfig(Creature creature) {
		this.creature = creature;
		neighborRangeX = neighborRangeY = neighborRangeZ = 1;
	}
	
	public BFSConfig voxel(byte voxel) {
		this.voxel = voxel;
		return this;
	}
	
	public BFSConfig range(float maxRange) {
		this.maxRange = maxRange;
		return this;
	}
	
	public BFSConfig closest(boolean closest) {
		this.closest = closest;
		return this;
	}
	
	public BFSConfig meta(byte meta) {
		this.meta = meta;
		return this;
	}
	
	public BFSConfig notmeta(byte meta) {
		this.meta = meta;
		notMeta = true;
		return this;
	}
	
	public BFSConfig neighbor(byte neighborMeta) {
		this.neighborMeta = neighborMeta;
		return this;
	}
	
	public BFSConfig notneighbor(byte neighborMeta) {
		this.neighborMeta = neighborMeta;
		notNeighbor = true;
		return this;
	}
	
	public BFSConfig neighborrangeX(int neighborRangeX) {
		this.neighborRangeX = neighborRangeX;
		return this;
	}
	
	public BFSConfig neighborrangeY(int neighborRangeY) {
		this.neighborRangeY = neighborRangeY;
		return this;
	}
	
	public BFSConfig neighborrangeZ(int neighborRangeZ) {
		this.neighborRangeZ = neighborRangeZ;
		return this;
	}
	
	public BFSConfig neighborrange(int neighborRangeX, int neighborRangeY, int neighborRangeZ) {
		this.neighborRangeX = neighborRangeX;
		this.neighborRangeY = neighborRangeY;
		this.neighborRangeZ = neighborRangeZ;
		return this;
	}
}
