package de.dakror.vloxlands.util.math;

import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * @author Dakror
 */
public class CustomizableFrustum extends Frustum {
	float[] points = new float[8 * 3];
	
	/**
	 * @param rectangle bounds in range of <code>[0,1]</code>
	 */
	public CustomizableFrustum(Rectangle r) {
		r.x *= 2;
		r.y *= 2;
		r.width *= 2;
		r.height *= 2;
		
		int j = 0;
		
		for (int i = 0; i < 8; i++) {
			points[j++] = r.x - 1 + (i == 1 || i == 2 || i == 5 || i == 6 ? r.width : 0);
			points[j++] = r.y - 1 + (i == 2 || i == 3 || i == 6 || i == 7 ? r.height : 0);
			points[j++] = i > 3 ? 1 : -1;
		}
	}
	
	@Override
	public void update(Matrix4 inverseProjectionView) {
		System.arraycopy(points, 0, planePointsArray, 0, points.length);
		Matrix4.prj(inverseProjectionView.val, planePointsArray, 0, 8, 3);
		for (int i = 0, j = 0; i < 8; i++) {
			Vector3 v = planePoints[i];
			v.x = planePointsArray[j++];
			v.y = planePointsArray[j++];
			v.z = planePointsArray[j++];
		}
		
		planes[0].set(planePoints[1], planePoints[0], planePoints[2]);
		planes[1].set(planePoints[4], planePoints[5], planePoints[7]);
		planes[2].set(planePoints[0], planePoints[4], planePoints[3]);
		planes[3].set(planePoints[5], planePoints[1], planePoints[6]);
		planes[4].set(planePoints[2], planePoints[3], planePoints[6]);
		planes[5].set(planePoints[4], planePoints[0], planePoints[1]);
	}
}
