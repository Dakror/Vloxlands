package de.dakror.vloxlands.render;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap;

import de.dakror.vloxlands.game.world.Chunk;
import de.dakror.vloxlands.render.VoxelFace.VoxelFaceKey;
import de.dakror.vloxlands.util.Direction;

/**
 * @author Dakror
 */
public class Mesher
{
	public static ObjectMap<VoxelFaceKey, VoxelFace> generateGreedyMesh(int cx, int cy, int cz, ObjectMap<VoxelFaceKey, VoxelFace> originalMap)
	{
		ObjectMap<VoxelFaceKey, VoxelFace> strips0 = new ObjectMap<VoxelFaceKey, VoxelFace>();
		
		if (originalMap.size == 0) return originalMap;
		
		// greedy-mode along Z - axis
		for (int x = 0; x < Chunk.SIZE; x++)
		{
			for (int y = 0; y < Chunk.SIZE; y++)
			{
				VoxelFace[] activeStrips = new VoxelFace[Direction.values().length];
				for (int z = 0; z < Chunk.SIZE; z++)
				{
					for (int i = 0; i < activeStrips.length; i++)
					{
						
						int posX = cx * Chunk.SIZE + x;
						int posY = cy * Chunk.SIZE + y;
						int posZ = cz * Chunk.SIZE + z;
						
						VoxelFaceKey key = new VoxelFaceKey(posX, posY, posZ, i);
						VoxelFace val = originalMap.get(key);
						
						if (activeStrips[i] != null)
						{
							if (val == null)
							{
								strips0.put(new VoxelFaceKey(activeStrips[i]), activeStrips[i]);
								activeStrips[i] = null;
							}
							else if (val.tex.equals(activeStrips[i].tex))
							{
								activeStrips[i].increaseSize(0, 0, 1);
							}
							else
							{
								strips0.put(new VoxelFaceKey(activeStrips[i]), activeStrips[i]);
								activeStrips[i] = new VoxelFace(Direction.values()[i], new Vector3(posX, posY, posZ), val.tex.cpy());
							}
						}
						else if (val != null)
						{
							activeStrips[i] = new VoxelFace(Direction.values()[i], new Vector3(posX, posY, posZ), val.tex.cpy());
						}
					}
				}
				for (int i = 0; i < activeStrips.length; i++)
					if (activeStrips[i] != null) strips0.put(new VoxelFaceKey(activeStrips[i]), activeStrips[i]);
			}
		}
		
		ObjectMap<VoxelFaceKey, VoxelFace> strips1 = new ObjectMap<VoxelFaceKey, VoxelFace>();
		
		// greedy-mode along X - axis
		for (int y = 0; y < Chunk.SIZE; y++)
		{
			VoxelFace[] activeStrips = new VoxelFace[Direction.values().length];
			for (int z = 0; z < Chunk.SIZE; z++)
			{
				for (int x = 0; x < Chunk.SIZE; x++)
				{
					for (int i = 0; i < activeStrips.length; i++)
					{
						int posX = cx * Chunk.SIZE + x;
						int posY = cy * Chunk.SIZE + y;
						int posZ = cz * Chunk.SIZE + z;
						
						VoxelFaceKey key = new VoxelFaceKey(posX, posY, posZ, i);
						VoxelFace val = strips0.get(key);
						
						if (val != null)
						{
							if (activeStrips[i] == null)
							{
								activeStrips[i] = new VoxelFace(val);
							}
							else
							{
								if (val.tex.equals(activeStrips[i].tex) && val.sizeZ == activeStrips[i].sizeZ && val.pos.z == activeStrips[i].pos.z)
								{
									activeStrips[i].increaseSize(1, 0, 0);
								}
								else
								{
									strips1.put(new VoxelFaceKey(activeStrips[i]), activeStrips[i]);
									
									activeStrips[i] = new VoxelFace(val);
								}
							}
						}
						else if (activeStrips[i] != null)
						{
							strips1.put(new VoxelFaceKey(activeStrips[i]), activeStrips[i]);
							activeStrips[i] = null;
						}
					}
				}
			}
			for (int i = 0; i < activeStrips.length; i++)
				if (activeStrips[i] != null) strips1.put(new VoxelFaceKey(activeStrips[i]), activeStrips[i]);
		}
		
		ObjectMap<VoxelFaceKey, VoxelFace> strips2 = new ObjectMap<VoxelFaceKey, VoxelFace>();
		
		// greedy-mode along Y - axis
		for (int x = 0; x < Chunk.SIZE; x++)
		{
			VoxelFace[] activeStrips = new VoxelFace[Direction.values().length];
			for (int z = 0; z < Chunk.SIZE; z++)
			{
				for (int y = 0; y < Chunk.SIZE; y++)
				{
					for (int i = 0; i < activeStrips.length; i++)
					{
						int posX = cx * Chunk.SIZE + x;
						int posY = cy * Chunk.SIZE + y;
						int posZ = cz * Chunk.SIZE + z;
						
						VoxelFaceKey key = new VoxelFaceKey(posX, posY, posZ, i);
						VoxelFace val = strips1.get(key);
						
						if (val != null)
						{
							if (activeStrips[i] == null)
							{
								activeStrips[i] = new VoxelFace(val);
							}
							else
							{
								if (val.tex.equals(activeStrips[i].tex) && val.sizeZ == activeStrips[i].sizeZ && val.sizeX == activeStrips[i].sizeX && val.pos.x == activeStrips[i].pos.x && val.pos.z == activeStrips[i].pos.z)
								{
									activeStrips[i].increaseSize(0, 1, 0);
								}
								else
								{
									strips2.put(new VoxelFaceKey(activeStrips[i]), activeStrips[i]);
									
									activeStrips[i] = new VoxelFace(val);
								}
							}
						}
						else if (activeStrips[i] != null)
						{
							strips2.put(new VoxelFaceKey(activeStrips[i]), activeStrips[i]);
							activeStrips[i] = null;
						}
					}
				}
			}
			for (int i = 0; i < activeStrips.length; i++)
				if (activeStrips[i] != null) strips2.put(new VoxelFaceKey(activeStrips[i]), activeStrips[i]);
		}
		
		return strips2;
	}
}
