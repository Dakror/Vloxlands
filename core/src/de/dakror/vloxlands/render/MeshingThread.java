package de.dakror.vloxlands.render;

import com.badlogic.gdx.utils.Array;

import de.dakror.vloxlands.util.Meshable;

/**
 * @author Dakror
 */
public class MeshingThread extends Thread
{
	public static MeshingThread currentMeshingThread;
	
	Array<Meshable> meshables = new Array<Meshable>();
	
	public static void register(Meshable m)
	{
		currentMeshingThread.meshables.add(m);
	}
	
	public static void dispose(Meshable m)
	{
		currentMeshingThread.meshables.removeValue(m, true);
	}
	
	public MeshingThread()
	{
		setPriority(7);
		setName("Meshing Thread");
		currentMeshingThread = this;
		start();
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			for (Meshable m : meshables)
				if (m != null) m.mesh();
			try
			{
				Thread.sleep(16);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
