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

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * @author Dakror
 */
public class Path {
	public Vector3 removedFirstNode;
	Array<Vector3> nodes;
	/**
	 * If the target is not walkable (e.g. a embeded resource in the ground) this
	 * is the actual target.
	 */
	Vector3 ghostTarget;
	/**
	 * If the target is already targeted by others this is the actual target
	 */
	Vector3 realTarget;
	
	int index;
	
	public Path(Array<Vector3> nodes) {
		this.nodes = nodes;
		index = 0;
	}
	
	public void pop() {
		nodes.pop();
	}
	
	public boolean isLast() {
		return index == nodes.size - 1;
	}
	
	public void next() {
		index++;
	}
	
	public Vector3 getLast() {
		if (nodes.size > 0) return nodes.peek();
		return null;
	}
	
	public Vector3 get() {
		return nodes.get(index);
	}
	
	public Vector3 get(int index) {
		return nodes.get(index);
	}
	
	public int getIndex() {
		return index;
	}
	
	public boolean isDone() {
		return index == nodes.size - 1;
	}
	
	public int size() {
		return nodes.size;
	}
	
	public float length() {
		float length = 0;
		if (removedFirstNode != null && nodes.size > 0) length = nodes.get(0).dst(removedFirstNode);
		
		for (int i = 1; i < nodes.size; i++) {
			length += nodes.get(i).dst(nodes.get(i - 1));
		}
		
		return length;
	}
	
	public Vector3 getGhostTarget() {
		return ghostTarget;
	}
	
	public void setGhostTarget(Vector3 ghostTarget) {
		this.ghostTarget = ghostTarget;
	}
	
	public Vector3 getRealTarget() {
		return realTarget;
	}
	
	public void setRealTarget(Vector3 realTarget) {
		this.realTarget = realTarget;
	}
}
