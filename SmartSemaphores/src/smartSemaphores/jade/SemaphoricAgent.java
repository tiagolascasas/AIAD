package smartSemaphores.jade;

import java.util.ArrayList;
import java.util.LinkedList;
import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;
import sajas.core.Agent;
import smartSemaphores.SmartSemaphoresRepastLauncher;
import smartSemaphores.jade.behaviours.TestBehaviour;
import smartSemaphores.repast.EmergencyVehicle;
import smartSemaphores.repast.NormalVehicle;

public class SemaphoricAgent extends RoadAgent
{
	public final int capacity;
	private ArrayList<String> connectableAgents;
	private ArrayList<String> semaphoricAgents;
	private SemaphoreStates state = SemaphoreStates.RED;
	private int id;

	public SemaphoricAgent(int id, int[] semaphoricIDs, int[] connectableIDs, int capacity)
	{
		super(id);
		
		this.semaphoricAgents = new ArrayList<>();
		this.connectableAgents = new ArrayList<>();
		
		for (int nID : semaphoricIDs)
		{
			if (nID == id)
				continue;
			semaphoricAgents.add(makeFullName(nID));
		}
		
		for (int nID : connectableIDs)
		{
			connectableAgents.add(makeFullName(nID));
		}
		
		this.capacity = capacity;
		this.vehicles = new LinkedList<>();
		this.emergency = new LinkedList<>();
	}
	
	@Override
	protected void setup()
	{
		this.addBehaviour(new TestBehaviour());
	}

	public void switchState(SemaphoreStates wantedState)
	{
		if (wantedState == this.state)
			return;

		for (String agentName : this.connectableAgents)
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

	@Override
	public int getAvailabeSpace(int increment)
	{
		if (this.vehicles.size() + increment <= this.capacity)
			return increment;
		else
			return this.capacity - this.vehicles.size();
	}
	
	@Override
	public void addCars(ArrayList<NormalVehicle> newCars)
	{
		this.vehicles.addAll(newCars);
	}
	
	public ArrayList<NormalVehicle> removeCars(int decrement)
	{
		ArrayList<NormalVehicle> removedCars = new ArrayList<>();
		
		while (decrement > 0 && this.vehicles.size() > 0)
		{
			NormalVehicle car = this.vehicles.element();
			this.vehicles.remove();
		}
		
		return removedCars;
	}
	
	public NormalVehicle removeCar()
	{
		NormalVehicle car = null;
		
		if (this.vehicles.size() > 0)
		{
			car = this.vehicles.element();
			this.vehicles.remove();
		}
		return car;
	}

	public ArrayList<RoadAgent> getConnectableAgents()
	{
		ArrayList<RoadAgent> neighbours = new ArrayList<>();
		
		for (String id : this.connectableAgents)
		{
			Context<?> context = ContextUtils.getContext(this);
			Agent agent = SmartSemaphoresRepastLauncher.getAgent(context, id);
			neighbours.add((RoadAgent) agent);
		}
		return neighbours;
	}

	@Override
	public void addEmergencyVehicle(EmergencyVehicle vehicle)
	{
		this.emergency.add(vehicle);
	}

	public int getCurrentNormalCars()
	{
		return this.vehicles.size();
	}
}
