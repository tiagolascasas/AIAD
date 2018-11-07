package smartSemaphores.jade;

import java.util.ArrayList;
import java.util.LinkedList;
import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;
import sajas.core.Agent;
import sajas.core.behaviours.TickerBehaviour;
import smartSemaphores.SmartSemaphoresRepastLauncher;
import smartSemaphores.jade.behaviours.StateCommunicationBehaviour;
import smartSemaphores.repast.EmergencyVehicle;
import smartSemaphores.repast.NormalVehicle;

public class SemaphoricAgent extends RoadAgent
{
	private final static int SECONDS_TO_MS = 1000;

	public final int capacity;
	private ArrayList<String> connectableAgents;
	private ArrayList<String> semaphoricAgents;
	private SemaphoreStates state = SemaphoreStates.RED;
	private int pedestrianCount = 0;
	private int pedestrianTotalCount = 0;
	private int secondsPassedOnState = 0;

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
		// Printout a welcome message
		System.out.println("HELLO");

		addBehaviour(new TickerBehaviour(this, SECONDS_TO_MS)
		{
			private static final long serialVersionUID = 1054989232567187192L;

			protected void onTick()
			{
				secondsPassedOnState++;
			}
		});

		// TODO verificações
		addBehaviour(new TickerBehaviour(this, 5 * SECONDS_TO_MS)
		{
			private static final long serialVersionUID = -5946442955577436810L;

			protected void onTick()
			{
				myAgent.addBehaviour(new StateCommunicationBehaviour());
			}
		});
	}

	@Override
	protected void takeDown()
	{
		// TODO Terminate code
		System.out.println("TERMINATE CODE");
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
}
