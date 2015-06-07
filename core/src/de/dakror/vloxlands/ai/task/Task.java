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


package de.dakror.vloxlands.ai.task;

import com.badlogic.gdx.math.Vector2;

import de.dakror.vloxlands.game.entity.structure.Structure;
import de.dakror.vloxlands.game.item.inv.ResourceList;
import de.dakror.vloxlands.util.interf.provider.ResourceListProvider;

/**
 * Tasks to be queued and done by Structures
 * 
 * @author Dakror
 */
public abstract class Task implements ResourceListProvider {
	ResourceList costs, result;
	int duration;
	String name, title, description;
	Vector2 icon;
	Structure origin;
	
	public boolean started;
	
	public Task() {
		costs = new ResourceList();
		result = new ResourceList();
		duration = 0;
		title = "";
	}
	
	public Task(String name, String title, String description, Vector2 icon, int duration, ResourceList costs, ResourceList result) {
		this.name = name;
		this.title = title;
		this.description = description != null ? description : "";
		this.icon = icon;
		this.duration = duration;
		this.costs = costs != null ? costs : new ResourceList();
		this.result = result != null ? result : new ResourceList();
	}
	
	public void setOrigin(Structure origin) {
		this.origin = origin;
	}
	
	@Override
	public ResourceList getCosts() {
		return costs;
	}
	
	@Override
	public ResourceList getResult() {
		return result;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Vector2 getIcon() {
		return icon;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public void enter() {
		started = true;
	}
	
	public abstract void exit();
}
