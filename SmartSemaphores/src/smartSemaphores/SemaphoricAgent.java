package smartSemaphores;

import jade.lang.acl.ACLMessage;
import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;
import sajas.core.Agent;
import sajas.core.behaviours.WakerBehaviour;
import smartSemaphores.SemaphoricAgent.StartCNets;

public class SemaphoricAgent extends Agent
{
	private Context<?> context;
	private Network<Object> net;
	private RepastEdge<Object> edge = null;
	
	private static final long serialVersionUID = -3443153228626928466L;

	public SemaphoricAgent(int i, int[] cross1agents)
	{

	}

	public class StartCNets extends WakerBehaviour
	{

		private static final long serialVersionUID = 1L;

		public StartCNets(Agent a, long timeout)
		{
			super(a, timeout);
		}

		@Override
		public void onWake()
		{
			// context and network (RepastS)
			context = ContextUtils.getContext(myAgent);
			net = (Network<Object>) context.getProjection("SmartSemaphores Road Network");
		}

	}
}
