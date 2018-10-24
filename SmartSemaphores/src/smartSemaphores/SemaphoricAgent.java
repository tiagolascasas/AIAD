package smartSemaphores;

import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;
import sajas.core.Agent;
import sajas.core.behaviours.WakerBehaviour;

public class SemaphoricAgent extends Agent
{
	private Context<?> context;
	private Network<Object> net;
	private RepastEdge<Object> edge = null;
	private int id;
	private int[] neighbours;
	private boolean on = false;
	
	private static final long serialVersionUID = -3443153228626928466L;

	public SemaphoricAgent(int id, int[] neighbours)
	{
		this.id = id;
		this.neighbours = neighbours;
		this.addBehaviour(new TestBehaviour());
	}
}
