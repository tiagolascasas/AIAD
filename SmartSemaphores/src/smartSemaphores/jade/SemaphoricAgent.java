package smartSemaphores.jade;

import java.util.ArrayList;
import java.util.LinkedList;
import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;
import sajas.core.Agent;
import sajas.core.behaviours.CyclicBehaviour;
import sajas.core.behaviours.TickerBehaviour;
import smartSemaphores.SmartSemaphoresRepastLauncher;
import smartSemaphores.jade.behaviours.HandleRequestsBehaviour;
import smartSemaphores.jade.behaviours.RequestPerformerBehaviour;
import smartSemaphores.repast.EmergencyVehicle;
import smartSemaphores.repast.NormalVehicle;

public class SemaphoricAgent extends RoadAgent
{
	public final int capacity;
	private ArrayList<String> connectableAgents;
	private ArrayList<String> semaphoricAgents;
	private SemaphoreStates state = SemaphoreStates.RED;
	private int pedestrianCount = 0;
	private int pedestrianTotalCount = 0;
	private int secondsPassedOnState = 0;
	private ArrayList<SemaphoreStates> stateTracker;

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
		this.stateTracker = new ArrayList<>();
		this.stateTracker.add(this.state);
	}

	@Override
	protected void setup()
	{
		System.out.println("Agent " + this.id + " is online");

		addBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				addBehaviour(new HandleRequestsBehaviour());
				
			}
			
		});
		addBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				addBehaviour(new RequestPerformerBehaviour());
				
			}
			
		});
	}

	@Override
	protected void takeDown()
	{
		System.out.println("Agent " + this.id + " has been terminated");
	}

	public void switchState(SemaphoreStates wantedState)
	{
		if (wantedState == this.state)
			return;

		for (String agentName : this.connectableAgents)
		{
			Context<?> context = ContextUtils.getContext(this);
			Agent targetAgent = SmartSemaphoresRepastLauncher.getAgent(context, agentName);
			@SuppressWarnings("unchecked")
			Network<Object> net = (Network<Object>) ContextUtils.getContext(this)
					.getProjection("SmartSemaphores Road Network");
			if (wantedState == SemaphoreStates.GREEN)
			{
				net.addEdge(this, targetAgent);
			} else
			{
				RepastEdge<Object> edge = net.getEdge(this, targetAgent);
				net.removeEdge(edge);
				this.pedestrianTotalCount += this.pedestrianCount;
				this.pedestrianCount = 0;
			}
		}
		this.state = wantedState;
		this.secondsPassedOnState = 0;
	}

	public SemaphoreStates getCurrentState()
	{
		return this.state;
	}

	public boolean hasEmergencyVehicles()
	{
		return this.emergency.size() > 0;
	}

	public float carRoadRatio()
	{
		return ((float) this.vehicles.size()) / ((float) this.capacity);
	}

	public int getSecondsPassedOnState()
	{
		return secondsPassedOnState;
	}
	
	public void incrementSecondsOnState()
	{
		this.secondsPassedOnState++;
		this.stateTracker.add(this.state);
	}

	/**
	 * Gets the semaphoric agents that also share the same cross as this agent
	 * 
	 * @return the other semaphoric agents
	 */
	public ArrayList<String> getNeighbours()
	{
		return this.semaphoricAgents;
	}

	/**
	 * Gets the road agents this agent can connect to
	 * 
	 * @return the road agents this agent can connect to
	 */
	public ArrayList<String> getSinkAgentNeighbours()
	{
		return this.connectableAgents;
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
			removedCars.add(car);
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
	
	public Agent getNeighbourByID(String id)
	{
		Context<?> context = ContextUtils.getContext(this);
		Agent agent = SmartSemaphoresRepastLauncher.getAgentByID(context, id);
		return agent;
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

	public EmergencyVehicle removeEmergencyVehicle()
	{
		EmergencyVehicle v = null;

		if (this.emergency.size() > 0)
		{
			v = this.emergency.element();
			this.emergency.remove();
		}
		return v;
	}

	public void addPedestrian()
	{
		this.pedestrianCount++;
	}

	public int getPedestrianTotalCount()
	{
		return pedestrianTotalCount;
	}
	
	public ArrayList<SemaphoreStates> getStateTracker()
	{
		return this.stateTracker;
	}
}
