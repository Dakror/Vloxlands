package de.dakror.vloxlands.game.query;

public class VoxelStats {
	public float weight, uplift;
	
	public VoxelStats() {
		this(0, 0);
	}
	
	public VoxelStats(float weight, float uplift) {
		this.weight = weight;
		this.uplift = uplift;
	}
}
