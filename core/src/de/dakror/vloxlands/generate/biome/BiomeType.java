package de.dakror.vloxlands.generate.biome;

public enum BiomeType
{
	moderate,
	// forest,
	// desert,
	// frost,
	
	;
	
	public String getName()
	{
		String s = name().replace("_", " ");
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
}
