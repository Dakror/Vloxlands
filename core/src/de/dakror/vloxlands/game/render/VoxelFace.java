package de.dakror.vloxlands.game.render;

import java.util.Arrays;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.dakror.vloxlands.game.world.Chunk;
import de.dakror.vloxlands.game.world.Island;
import de.dakror.vloxlands.util.Direction;

public class VoxelFace
{
	public Direction dir;
	public Vector3 pos, tl, tr, bl, br, n;
	
	public Vector2 tex;
	public int sizeX, sizeY, sizeZ;
	
	public VoxelFace(Direction dir, Vector3 pos, Vector2 tex)
	{
		this(dir, pos, tex, 1, 1, 1);
	}
	
	public VoxelFace(VoxelFace o)
	{
		sizeX = o.sizeX;
		sizeY = o.sizeY;
		sizeZ = o.sizeZ;
		dir = o.dir;
		tex = o.tex.cpy();
		pos = o.pos.cpy();
		
		updateVertices();
	}
	
	public VoxelFace(Direction dir, Vector3 pos, Vector2 tex, int sizeX, int sizeY, int sizeZ)
	{
		super();
		this.dir = dir;
		this.pos = pos;
		this.tex = tex;
		setSize(sizeX, sizeY, sizeZ);
	}
	
	public void setSize(int sizeX, int sizeY, int sizeZ)
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
	
	public float[] getVertexData()
	{
		float[] vert = new float[Chunk.VERTEX_SIZE * 4];
		
		int[] ints = new int[] { sizeX, sizeY, sizeZ };
		Arrays.sort(ints);
		boolean dirVertical = dir == Direction.UP || dir == Direction.DOWN;
		int vertical = (dirVertical) ? 0 : 1 + ((sizeY > sizeX && sizeY > sizeZ) ? -1 : 0);
		
		int i = 0;
		vert[i++] = tl.x + pos.x;
		vert[i++] = tl.y + pos.y;
		vert[i++] = tl.z + pos.z;
		vert[i++] = n.x;
		vert[i++] = n.y;
		vert[i++] = n.z;
		vert[i++] = 0;
		vert[i++] = 0;
		
		vert[i++] = tr.x + pos.x;
		vert[i++] = tr.y + pos.y;
		vert[i++] = tr.z + pos.z;
		vert[i++] = n.x;
		vert[i++] = n.y;
		vert[i++] = n.z;
		vert[i++] = dirVertical && sizeX > sizeZ ? ints[2] : ints[1 + vertical];
		vert[i++] = 0;
		
		vert[i++] = br.x + pos.x;
		vert[i++] = br.y + pos.y;
		vert[i++] = br.z + pos.z;
		vert[i++] = n.x;
		vert[i++] = n.y;
		vert[i++] = n.z;
		vert[i++] = dirVertical && sizeX > sizeZ ? ints[2] : ints[1 + vertical];
		vert[i++] = dirVertical && sizeX > sizeZ ? ints[1] : ints[2 - vertical];
		
		vert[i++] = bl.x + pos.x;
		vert[i++] = bl.y + pos.y;
		vert[i++] = bl.z + pos.z;
		vert[i++] = n.x;
		vert[i++] = n.y;
		vert[i++] = n.z;
		vert[i++] = 0;
		vert[i++] = dirVertical && sizeX > sizeZ ? ints[1] : ints[2 - vertical];
		
		return vert;
	}
	
	public void increaseSize(int sizeX, int sizeY, int sizeZ)
	{
		setSize(this.sizeX + sizeX, this.sizeY + sizeY, this.sizeZ + sizeZ);
	}
	
	@Override
	public String toString()
	{
		return "VoxelFace[pos=" + pos.toString() + ", DIR=" + dir + ", sizeX=" + sizeX + ", sizeY=" + sizeY + ", sizeZ=" + sizeZ + ", tl=" + tl + ", tr=" + tr + ", bl=" + bl + ", br=" + br + "]";
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return toString().equals(obj.toString());
	}
	
	public static class VoxelFaceKey implements Comparable<VoxelFaceKey>
	{
		public int x, y, z, d;
		
		public VoxelFaceKey(int x, int y, int z, int d)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.d = d;
		}
		
		public VoxelFaceKey(VoxelFace vf)
		{
			x = (int) vf.pos.x;
			y = (int) vf.pos.y;
			z = (int) vf.pos.z;
			d = vf.dir.ordinal();
		}
		
		@Override
		public int hashCode()
		{
			return ((x * Island.SIZE + y) * Island.SIZE + z) * Island.SIZE + d;// Integer.parseInt(x + "" + y + "" + z + "" + d);
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (!(o instanceof VoxelFaceKey)) return false;
			
			return hashCode() == o.hashCode();
		}
		
		@Override
		public String toString()
		{
			return "[" + x + ", " + y + ", " + z + ", " + Direction.values()[d] + "]";
		}
		
		@Override
		public int compareTo(VoxelFaceKey o)
		{
			if (x != o.x) return x - o.x;
			else if (y != o.x) return y - o.y;
			else if (z != o.z) return z - o.z;
			
			return d - o.d;
		}
	}
}
