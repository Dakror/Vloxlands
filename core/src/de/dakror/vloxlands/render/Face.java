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


package de.dakror.vloxlands.render;

import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.util.Direction;

/**
 * @author Dakror
 */
public abstract class Face<T extends Face<T>> {
	public Direction dir;
	public Vector3 pos, tl, tr, bl, br, n;
	public float sizeX, sizeY, sizeZ;
	
	int hash;
	boolean hashDirty = true;
	
	public Face(Direction dir, Vector3 pos) {
		this(dir, pos, 1, 1, 1);
	}
	
	public Face(Direction dir, Vector3 pos, float sizeX, float sizeY, float sizeZ) {
		this.dir = dir;
		this.pos = pos;
		setSize(sizeX, sizeY, sizeZ);
	}
	
	public void setSize(float sizeX, float sizeY, float sizeZ) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		
		updateVertices();
	}
	
	public void updateVertices() {
		tl = new Vector3(0, sizeY, 0);
		tr = new Vector3(sizeX, sizeY, 0);
		bl = new Vector3(0, 0, 0);
		br = new Vector3(sizeX, 0, 0);
		switch (dir) {
			case NORTH: {
				tl.x = sizeX;
				bl.x = sizeX;
				
				tr.z = sizeZ;
				br.z = sizeZ;
				
				break;
			}
			case SOUTH: {
				tl.z = sizeZ;
				bl.z = sizeZ;
				
				tr.x = 0;
				br.x = 0;
				
				break;
			}
			case WEST: {
				tl.z = sizeZ;
				bl.z = sizeZ;
				tr.z = sizeZ;
				br.z = sizeZ;
				
				tl.x = sizeX;
				bl.x = sizeX;
				tr.x = 0;
				br.x = 0;
				
				break;
			}
			case UP: {
				tl.z = sizeZ;
				tr.z = sizeZ;
				
				bl.y = sizeY;
				br.y = sizeY;
				break;
			}
			case DOWN: {
				tl.y = 0;
				tr.y = 0;
				
				bl.z = sizeZ;
				br.z = sizeZ;
				break;
			}
			default:
				break;
		}
		
		n = bl.cpy().sub(br).crs(tr.cpy().sub(br)).nor();
	}
	
	public void increaseSize(Vector3 direction) {
		setSize(sizeX + direction.x, sizeY + direction.y, sizeZ + direction.z);
	}
	
	public void increaseSize(float x, float y, float z) {
		setSize(sizeX + x, sizeY + y, sizeZ + z);
	}
	
	public boolean isSameSize(T o, Vector3 direction) {
		if (direction.x == 1) return sizeY == o.sizeY && sizeZ == o.sizeZ;
		else if (direction.y == 1) return sizeX == o.sizeX && sizeZ == o.sizeZ;
		else return sizeY == o.sizeY && sizeX == o.sizeX;
	}
	
	@Override
	public String toString() {
		return "VoxelFace[pos=" + pos.toString() + ", DIR=" + dir + ", sizeX=" + sizeX + ", sizeY=" + sizeY + ", sizeZ=" + sizeZ + ", tl=" + tl + ", tr=" + tr + ", bl=" + bl + ", br=" + br + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Face)) return false;
		return hashCode() == obj.hashCode() && sizeX == ((TextureFace) obj).sizeX && sizeY == ((TextureFace) obj).sizeY && sizeZ == ((TextureFace) obj).sizeZ;
	}
	
	@Override
	public int hashCode() {
		if (hashDirty) {
			hash = Face.getHashCode((int) pos.x, (int) pos.y, (int) pos.z, dir.ordinal());
			hashDirty = false;
		}
		return hash;
	}
	
	public abstract boolean canCombine(T o);
	
	public static int getHashCode(int x, int y, int z, int d) {
		int hash = 0;
		hash += x << 24;
		hash += y << 16;
		hash += z << 8;
		hash += d;
		
		return hash;
	}
}
