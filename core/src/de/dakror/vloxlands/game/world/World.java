package de.dakror.vloxlands.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.glutils.MipMapGenerator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import de.dakror.vloxlands.Vloxlands;

/**
 * @author Dakror
 */
public class World implements RenderableProvider
{
	public int visibleChunks;
	
	float[] opaqueMeshData;
	// float[] transpMeshData;
	
	public Vector3 size;
	public Chunk[] chunks;
	
	Material opaque;// , transp;
	
	public World(int width, int height, int depth)
	{
		size = new Vector3(width, height, depth);
		chunks = new Chunk[width * height * depth];
		int l = 0;
		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				for (int k = 0; k < depth; k++)
				{
					chunks[l++] = new Chunk(i, j, k);
				}
			}
		}
		
		Texture tex = new Texture(Gdx.files.internal("voxelTextures.png"), true);
		MipMapGenerator.setUseHardwareMipMap(false);
		tex.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		opaque = new Material(TextureAttribute.createDiffuse(tex));
		// transp = new Material(TextureAttribute.createDiffuse(tex), new BlendingAttribute());
		
		opaqueMeshData = new float[Chunk.SIZE * Chunk.SIZE * Chunk.SIZE * Chunk.VERTEX_SIZE * 6];
		// transpMeshData = new float[Chunk.SIZE * Chunk.SIZE * Chunk.SIZE * Chunk.VERTEX_SIZE * 6];
	}
	
	public byte get(float x, float y, float z)
	{
		int chunkX = (int) (x / Chunk.SIZE);
		if (chunkX < 0 || chunkX >= size.x) return 0;
		int chunkY = (int) (y / Chunk.SIZE);
		if (chunkY < 0 || chunkY >= size.y) return 0;
		int chunkZ = (int) (z / Chunk.SIZE);
		if (chunkZ < 0 || chunkZ >= size.z) return 0;
		return chunks[(int) (chunkX + chunkZ * size.x + chunkY * size.x * size.z)].get((int) x % Chunk.SIZE, (int) y % Chunk.SIZE, (int) z % Chunk.SIZE);
	}
	
	public void set(float x, float y, float z, byte id)
	{
		int chunkX = (int) (x / Chunk.SIZE);
		if (chunkX < 0 || chunkX >= size.x) return;
		int chunkY = (int) (y / Chunk.SIZE);
		if (chunkY < 0 || chunkY >= size.y) return;
		int chunkZ = (int) (z / Chunk.SIZE);
		if (chunkZ < 0 || chunkZ >= size.z) return;
		chunks[(int) (chunkX + chunkZ * size.x + chunkY * size.x * size.z)].set((int) x % Chunk.SIZE, (int) y % Chunk.SIZE, (int) z % Chunk.SIZE, id);
	}
	
	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool)
	{
		visibleChunks = 0;
		for (int i = 0; i < chunks.length; i++)
		{
			Chunk chunk = chunks[i];
			if (Vloxlands.currentGame.camera.frustum.boundsInFrustum(chunk.boundingBox))
			{
				chunk.updateMeshes(opaqueMeshData);// , transpMeshData);
				if (chunk.isEmpty()) continue;
				
				Renderable opaque = pool.obtain();
				opaque.material = this.opaque;
				opaque.mesh = chunk.getOpaqueMesh();
				opaque.meshPartOffset = 0;
				opaque.meshPartSize = chunk.opaqueVerts;
				opaque.primitiveType = GL20.GL_TRIANGLES;
				
				// Renderable transp = pool.obtain();
				// transp.material = this.transp;
				// transp.mesh = chunk.getTransparentMesh();
				// transp.meshPartOffset = 0;
				// transp.meshPartSize = chunk.transpVerts;
				// transp.primitiveType = GL20.GL_TRIANGLES;
				
				
				renderables.add(opaque);
				// renderables.add(transp);
				visibleChunks++;
			}
		}
	}
}
