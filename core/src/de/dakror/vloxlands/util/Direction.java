package de.dakror.vloxlands.util;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public enum Direction
{
	NORTH(1, 0, 0, 0, -90, 0),
	
	UP(0, 1, 0, 90, 0, 0),
	
	EAST(0, 0, -1, 0, 0, 0),
	
	SOUTH(-1, 0, 0, 0, 90, 0),
	
	DOWN(0, -1, 0, -90, 0, 0),
	
	WEST(0, 0, 1, 0, 180, 0);
	
	public Vector3 dir;
	Vector3 rot;
	
	Direction(int x, int y, int z, int rotX, int rotY, int rotZ)
	{
		dir = new Vector3(x, y, z);
		rot = new Vector3(rotX, rotY, rotZ);
	}
	
	public static Vector3 getNeededRotation(Direction a, Direction b)
	{
		return new Vector3(b.rot.x - a.rot.x, b.rot.y - a.rot.y, b.rot.z - a.rot.z);
	}
	
	public static Array<Direction> get90DegreeDirections(Direction d)
	{
		Array<Direction> l = new Array<Direction>();
		for (Direction e : Direction.values())
			if (e.ordinal() != d.ordinal() && e.ordinal() != (d.ordinal() + 3) % 6) l.add(e);
		System.out.println(l);
		return l;
	}
}
