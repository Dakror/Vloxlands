package de.dakror.vloxlands.render;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;

import de.dakror.vloxlands.game.voxel.Voxel;
import de.dakror.vloxlands.game.world.Chunk;
import de.dakror.vloxlands.util.Direction;

public class Face
{
	public static class FaceKey implements Comparable<FaceKey>
	{
		public int x, y, z, d;
		
		public FaceKey(int x, int y, int z, int d)
		{
			set(x, y, z, d);
		}

		public FaceKey set(FaceKey k)
		{
			x = k.x;
			y = k.y;
			z = k.z;
			d = k.d;
			
			return this;
		}
		
		public FaceKey set(int x, int y, int z, int d)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.d = d;
			
			return this;
		}
		
		@Override
		public int hashCode()
		{
			return ((x * Chunk.SIZE + y) * Chunk.SIZE + z) * Chunk.SIZE + d;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (!(o instanceof FaceKey)) return false;
			
			return hashCode() == o.hashCode();
		}
		
		@Override
		public String toString()
		{
			return "[" + x + ", " + y + ", " + z + ", " + Direction.values()[d] + "]";
		}
		
		@Override
		public int compareTo(FaceKey o)
		{
			if (x != o.x) return x - o.x;
			else if (y != o.x) return y - o.y;
			else if (z != o.z) return z - o.z;
			
			return d - o.d;
		}
	}
	
	public Direction dir;
	public Vector3 pos, tl, tr, bl, br, n;
	
	public Vector2 tex;
	public float texWidth = Voxel.TEXSIZE;
	public float texHeight = Voxel.TEXSIZE;

	public byte light;
	
	public float sizeX, sizeY, sizeZ;
	
	public Face(Direction dir, Vector3 pos, Vector2 tex, byte light)
	{
		this(dir, pos, tex, 1, 1, 1, light);
	}
	
	public Face(Direction dir, Vector3 pos, Vector2 tex, int sizeX, int sizeY, int sizeZ, byte light)
	{
		super();
		this.dir = dir;
		this.pos = pos;
		this.tex = tex;
		this.light = light;
		setSize(sizeX, sizeY, sizeZ);
	}
	
	public void setSize(float sizeX, float sizeY, float sizeZ)
	{
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		
		updateVertices();
	}
	
	public void updateVertices()
	{
		tl = new Vector3(0, sizeY, 0);
		tr = new Vector3(sizeX, sizeY, 0);
		bl = new Vector3(0, 0, 0);
		br = new Vector3(sizeX, 0, 0);
		switch (dir)
		{
			case NORTH:
			{
				tl.x = sizeX;
				bl.x = sizeX;
				
				tr.z = sizeZ;
				br.z = sizeZ;
				
				break;
			}
			case SOUTH:
			{
				tl.z = sizeZ;
				bl.z = sizeZ;
				
				tr.x = 0;
				br.x = 0;
				
				break;
			}
			case WEST:
			{
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
			case UP:
			{
				tl.z = sizeZ;
				tr.z = sizeZ;
				
				bl.y = sizeY;
				br.y = sizeY;
				break;
			}
			case DOWN:
			{
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
	
	public void getVertexData(FloatArray vert)
	{
		boolean zDir = dir == Direction.WEST || dir == Direction.EAST;
		boolean yDir = dir == Direction.UP || dir == Direction.DOWN;
		
		float tx = (float) Math.ceil(zDir ? sizeX : yDir ? sizeX : sizeZ);
		float ty = (float) Math.ceil(yDir ? sizeZ : sizeY);
		
		vert.add(tl.x + pos.x);
		vert.add(tl.y + pos.y);
		vert.add(tl.z + pos.z);
		vert.add(n.x);
		vert.add(n.y);
		vert.add(n.z);
		vert.add(tex.x);
		vert.add(tex.y);
		vert.add(tx);
		vert.add(ty);
		vert.add(light);
		
		vert.add(tr.x + pos.x);
		vert.add(tr.y + pos.y);
		vert.add(tr.z + pos.z);
		vert.add(n.x);
		vert.add(n.y);
		vert.add(n.z);
		vert.add(tex.x + texWidth);
		vert.add(tex.y);
		vert.add(tx);
		vert.add(ty);
		vert.add(light);
		
		vert.add(br.x + pos.x);
		vert.add(br.y + pos.y);
		vert.add(br.z + pos.z);
		vert.add(n.x);
		vert.add(n.y);
		vert.add(n.z);
		vert.add(tex.x + texWidth);
		vert.add(tex.y + texHeight);
		vert.add(tx);
		vert.add(ty);
		vert.add(light);
		
		vert.add(bl.x + pos.x);
		vert.add(bl.y + pos.y);
		vert.add(bl.z + pos.z);
		vert.add(n.x);
		vert.add(n.y);
		vert.add(n.z);
		vert.add(tex.x);
		vert.add(tex.y + texHeight);
		vert.add(tx);
		vert.add(ty);
		vert.add(light);
	}
	
	public void increaseSize(Vector3 direction)
	{
		setSize(sizeX + direction.x, sizeY + direction.y, sizeZ + direction.z);
	}
	
	@Override
	public String toString()
	{
		return "VoxelFace[pos=" + pos.toString() + ", DIR=" + dir + ", sizeX=" + sizeX + ", sizeY=" + sizeY + ", sizeZ=" + sizeZ + ", tl=" + tl + ", tr=" + tr + ", bl=" + bl + ", br=" + br + "]";
	}
	
	public boolean isSameSize(Face o, Vector3 direction)
	{
		if (direction.x == 1) return sizeY == o.sizeY && sizeZ == o.sizeZ;
		else if (direction.y == 1) return sizeX == o.sizeX && sizeZ == o.sizeZ;
		else return sizeY == o.sizeY && sizeX == o.sizeX;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return toString().equals(obj.toString());
	}
}
