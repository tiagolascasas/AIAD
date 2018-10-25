package smartSemaphores;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;
import sajas.core.Agent;
import sajas.core.behaviours.WakerBehaviour;

public class SemaphoricAgent extends Agent
{
	private ArrayList<String> neighbours;
	private boolean on = false;
	
	public SemaphoricAgent(int id, int[] neighboursIDs)
	{
		this.neighbours = new ArrayList<>();
		for (int nID : neighboursIDs)
		{
			if (nID == id)
				continue;
			neighbours.add(makeFullName(nID));
		}
		
		this.addBehaviour(new TestBehaviour());
	}
	
	private String makeFullName(int nID) 
	{
		return "Semaphoric agent " + nID + "@SmartSemaphores";
	}

	public void switchState(boolean turnOn)
	{
		if (turnOn == this.on)
			return;
		
		for (String agentName : this.neighbours)
		{
			Context<?> context = ContextUtils.getContext(this);
			Agent targetAgent = SmartSemaphoresRepastLauncher.getAgent(context, agentName);
			Network<Object> net = (Network<Object>) ContextUtils.getContext(this).getProjection("SmartSemaphores Road Network");
			if (turnOn)
			{
				net.addEdge(this, targetAgent);
			}
			else
			{
				RepastEdge<Object> edge = net.getEdge(this, targetAgent);
				net.removeEdge(edge);
			}
		}
		this.on = turnOn;
	}

	public boolean getCurrentState() 
	{
		return this.on;
	}
}
