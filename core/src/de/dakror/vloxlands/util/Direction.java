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
 

package de.dakror.vloxlands.util;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * @author Dakror
 */
public enum Direction {
	NORTH(1, 0, 0, 0, -90, 0),
	UP(0, 1, 0, 90, 0, 0),
	EAST(0, 0, -1, 0, 0, 0),
	SOUTH(-1, 0, 0, 0, 90, 0),
	DOWN(0, -1, 0, -90, 0, 0),
	WEST(0, 0, 1, 0, 180, 0);
	
	public Vector3 dir;
	Vector3 rot;
	
	Direction(int x, int y, int z, int rotX, int rotY, int rotZ) {
		dir = new Vector3(x, y, z);
		rot = new Vector3(rotX, rotY, rotZ);
	}
	
	public static Vector3 getNeededRotation(Direction a, Direction b) {
		return new Vector3(b.rot.x - a.rot.x, b.rot.y - a.rot.y, b.rot.z - a.rot.z);
	}
	
	public static Array<Direction> get90DegreeDirections(Direction d) {
		Array<Direction> l = new Array<Direction>();
		for (Direction e : Direction.values())
			if (e.ordinal() != d.ordinal() && e.ordinal() != (d.ordinal() + 3) % 6) l.add(e);
		System.out.println(l);
		return l;
	}
}
