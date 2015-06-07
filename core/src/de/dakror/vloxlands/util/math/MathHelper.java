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

import java.math.RoundingMode;
import java.text.DecimalFormat;

import com.badlogic.gdx.math.MathUtils;

/**
 * @author Dakror
 */
public class MathHelper {
	public static String formatBinarySize(long size, int digits) {
		return formatNumber(size, digits, 1024).toUpperCase() + "B";
	}
	
	public static String formatNumber(long size, int digits, int base) {
		if (size == 0) return "0";
		final String[] levels = { "", "k", "m", "g", "t" };
		for (int i = levels.length - 1; i > -1; i--)
			if (size >= (long) Math.pow(base, i)) {
				DecimalFormat df = new DecimalFormat();
				df.setMaximumFractionDigits(digits);
				df.setMinimumFractionDigits(digits);
				df.setRoundingMode(RoundingMode.FLOOR);
				return df.format(size / Math.pow(base, i)) + levels[i];
			}
		return null;
	}
	
	public static int[] indexShuffle(int size) {
		int[] arr = new int[size];
		for (int i = 0; i < size; i++)
			arr[i] = i;
		
		for (int i = 0; i < size; i++) {
			int ran = MathUtils.random(size - 1);
			int temp = arr[i];
			arr[i] = arr[ran];
			arr[ran] = temp;
		}
		
		return arr;
	}
}
