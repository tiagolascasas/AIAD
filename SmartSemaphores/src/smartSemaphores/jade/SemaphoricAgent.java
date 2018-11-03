package smartSemaphores.jade;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;
import sajas.core.Agent;
import sajas.core.behaviours.WakerBehaviour;
import smartSemaphores.SmartSemaphoresRepastLauncher;

public class SemaphoricAgent extends RoadAgent
{
	public final int capacity;
	private ArrayList<String> neighbours;
	private SemaphoreStates state = SemaphoreStates.RED;

	public SemaphoricAgent(int id, int[] neighboursIDs, int[] sinkIDs, int capacity)
	{
		this.neighbours = new ArrayList<>();
		for (int nID : neighboursIDs)
		{
			if (nID == id)
				continue;
			neighbours.add(makeFullName(nID));
		}
		for (int nID : sinkIDs)
		{
			neighbours.add(SinkAgent.makeFullName(nID));
		}
		this.capacity = capacity;
		this.addBehaviour(new TestBehaviour());
	}

	public static String makeFullName(int nID)
	{
		return "Semaphoric agent " + nID + "@SmartSemaphores";
	}

	public void switchState(SemaphoreStates wantedState)
	{
		if (wantedState == this.state)
			return;

		for (String agentName : this.neighbours)
		{
			Context<?> context = ContextUtils.getContext(this);
			Agent targetAgent = SmartSemaphoresRepastLauncher.getAgent(context, agentName);
			Network<Object> net = (Network<Object>) ContextUtils.getContext(this)
					.getProjection("SmartSemaphores Road Network");
			if (wantedState == SemaphoreStates.GREEN)
			{
				net.addEdge(this, targetAgent);
			}
			else
			{
				RepastEdge<Object> edge = net.getEdge(this, targetAgent);
				net.removeEdge(edge);
			}
		}
		this.state = wantedState;
	}

	public SemaphoreStates getCurrentState()
	{
		return this.state;
	}

	public int addCars(int increment)
	{
		if (this.currentCars + increment > this.capacity)
		{
			this.currentCars = this.capacity;
			return this.capacity - increment;
		} else
		{
			this.currentCars += increment;
			return increment;
		}
	}

	public int removeCars(int decrement)
	{
		if (this.currentCars - decrement < 0)
		{
			int diff = decrement - this.currentCars;
			this.currentCars = 0;
			return diff;
		} else
		{
			this.currentCars -= decrement;
			return decrement;
		}
	}

	public ArrayList<RoadAgent> getNeighbours()
	{
		ArrayList<RoadAgent> neighbours = new ArrayList<>();
		
		for (String id : this.neighbours)
		{
			Context<?> context = ContextUtils.getContext(this);
			Agent agent = SmartSemaphoresRepastLauncher.getAgent(context, id);
			neighbours.add((RoadAgent) agent);
		}
		return neighbours;
	}
}
