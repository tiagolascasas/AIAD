package smartSemaphores;

import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;
import sajas.core.AID;
import sajas.core.Agent;
import sajas.core.behaviours.Behaviour;

public class TestBehaviour extends Behaviour
{
	private boolean done = false;
	
	@Override
	public void action()
	{
		if (!myAgent.getName().startsWith("Semaphoric agent 1@"))
			return;
		
		Context<?> context = ContextUtils.getContext(myAgent);
		Agent targetAgent = SmartSemaphoresRepastLauncher.getAgent(context, "Semaphoric agent 3@SmartSemaphores");
		Network<Object> net = (Network<Object>) ContextUtils.getContext(myAgent).getProjection("SmartSemaphores Road Network");
		net.addEdge(myAgent, targetAgent);
	}

	@Override
	public boolean done()
	{
		return done;
	}

}
