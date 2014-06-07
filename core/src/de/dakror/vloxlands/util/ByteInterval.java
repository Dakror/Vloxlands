/*
Copyright 2013 John Thomas McDole

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package de.dakror.vloxlands.util;

import java.util.Comparator;

import com.binarydreamers.trees.Interval;

public class ByteInterval implements Interval<Byte>
{
	byte low;
	byte high;
	
	public ByteInterval(byte lower, byte upper)
	{
		low = lower;
		high = upper;
	}
	
	@Override
	public Byte getLower()
	{
		return low;
	}
	
	@Override
	public Byte getUpper()
	{
		return high;
	}
	
	@Override
	public String toString()
	{
		return "long(" + low + ", " + high + ")";
	}
	
	static final Comparator<Interval<Byte>> comparator = new Comparator<Interval<Byte>>()
	{
		@Override
		public int compare(Interval<Byte> o1, Interval<Byte> o2)
		{
			int value = o1.getLower() - o2.getLower();
			if (value == 0)
			{
				value = o1.getUpper() - o2.getUpper();
			}
			return value;
		}
	};
}
