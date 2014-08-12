package de.dakror.vloxlands.game.entity.structure;


public enum NodeType
{
	target(false),
	entry(true),
	exit(true),
	deposit(true),
	build(true),
	pickup(true),
	spawn(false),
	
	;
	
	public boolean useGhostTarget;
	
	private NodeType(boolean useGhostTarget)
	{
		this.useGhostTarget = useGhostTarget;
	}
}
