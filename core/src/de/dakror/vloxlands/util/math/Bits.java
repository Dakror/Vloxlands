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
 

package de.dakror.vloxlands.util.math;

import java.io.ByteArrayOutputStream;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Copied from {@link java.io.Bits} and slightly modified to work with {@link java.io.ByteArrayOutputStream}
 * 
 * @author Dakror
 */
public class Bits {
	public static void putBoolean(ByteArrayOutputStream baos, boolean val) {
		baos.write((byte) (val ? 1 : 0));
	}
	
	public static void putChar(ByteArrayOutputStream baos, char val) {
		baos.write((byte) (val >>> 8));
		baos.write((byte) (val));
	}
	
	public static void putShort(ByteArrayOutputStream baos, short val) {
		baos.write((byte) (val >>> 8));
		baos.write((byte) (val));
	}
	
	public static void putInt(ByteArrayOutputStream baos, int val) {
		baos.write((byte) (val >>> 24));
		baos.write((byte) (val >>> 16));
		baos.write((byte) (val >>> 8));
		baos.write((byte) (val));
	}
	
	public static void putFloat(ByteArrayOutputStream baos, float val) {
		putInt(baos, Float.floatToIntBits(val));
	}
	
	public static void putLong(ByteArrayOutputStream baos, long val) {
		baos.write((byte) (val >>> 56));
		baos.write((byte) (val >>> 48));
		baos.write((byte) (val >>> 40));
		baos.write((byte) (val >>> 32));
		baos.write((byte) (val >>> 24));
		baos.write((byte) (val >>> 16));
		baos.write((byte) (val >>> 8));
		baos.write((byte) (val));
	}
	
	public static void putDouble(ByteArrayOutputStream baos, double val) {
		putLong(baos, Double.doubleToLongBits(val));
	}
	
	public static void putMatrix4(ByteArrayOutputStream baos, Matrix4 mtx) {
		for (float f : mtx.getValues())
			putFloat(baos, f);
	}
	
	public static void putVector3(ByteArrayOutputStream baos, Vector3 vec) {
		putFloat(baos, vec.x);
		putFloat(baos, vec.y);
		putFloat(baos, vec.z);
	}
}
