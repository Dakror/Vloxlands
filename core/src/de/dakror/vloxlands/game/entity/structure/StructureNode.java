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
 

package de.dakror.vloxlands.game.entity.structure;

import com.badlogic.gdx.math.Vector3;

/**
 * @author Dakror
 */
public class StructureNode {
	/**
	 * An unique name, should be name of a item
	 */
	public String name;
	public Vector3 pos;
	public NodeType type;
	
	public StructureNode(NodeType type, int x, int y, int z) {
		this(type, "", x, y, z);
	}
	
	public StructureNode(NodeType type, String name, int x, int y, int z) {
		this.type = type;
		this.name = name;
		pos = new Vector3(x, y, z);
	}
}
