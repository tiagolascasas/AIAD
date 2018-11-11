/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.jade;

import java.util.ArrayList;
import java.util.LinkedList;
import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;
import sajas.core.Agent;
import smartSemaphores.SmartSemaphores;
import smartSemaphores.repast.EmergencyVehicle;
import smartSemaphores.repast.NormalVehicle;
import smartSemaphores.repast.Pedestrian;
import smartSemaphores.repast.SimulationManager;

/**
 * The Class SemaphoricAgent.
 */
public abstract class SemaphoricAgent extends RoadAgent {

    /** The capacity. */
    public final int capacity;

    /** The connectable agents. */
    private ArrayList<String> connectableAgents;

    /** The semaphoric agents neighbors. */
    private ArrayList<String> semaphoricAgents;

    /** The state. */
    private SemaphoreStates state = SemaphoreStates.RED;

    /** The state tracker. */
    private ArrayList<SemaphoreStates> stateTracker;

    /** The pedestrians. */
    private ArrayList<Pedestrian> pedestrians;

    /** The pedestrian count. */
    private int pedestrianCount = 0;

    /** The pedestrian total count. */
    private int pedestrianTotalCount = 0;

    /** The seconds passed on state. */
    private int secondsPassedOnState = 0;

    /**
     * Instantiates a new semaphoric agent.
     *
     * @param id
     *            the id
     * @param semaphoricIDs
     *            the semaphoric Id's
     * @param connectableIDs
     *            the connectable Id's
     * @param capacity
     *            the capacity of the road
     */
    public SemaphoricAgent(int id, int[] semaphoricIDs, int[] connectableIDs, int capacity) {
	super(id);

	this.semaphoricAgents = new ArrayList<>();
	this.connectableAgents = new ArrayList<>();
	this.pedestrians = new ArrayList<>();

	for (int nID : semaphoricIDs) {
	    if (nID == id)
		continue;
	    semaphoricAgents.add(makeSemaphoreName(nID));
	}

	for (int nID : connectableIDs) {
	    connectableAgents.add(makeFullName(nID));
	}

	this.capacity = capacity;
	this.vehicles = new LinkedList<>();
	this.emergency = new LinkedList<>();
	this.stateTracker = new ArrayList<>();
	this.stateTracker.add(this.state);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sajas.core.Agent#setup()
     */
    @Override
    protected void setup() {
	System.out.println("Agent " + this.id + " is online");
    }

    /*
     * (non-Javadoc)
     * 
     * @see sajas.core.Agent#takeDown()
     */
    @Override
    protected void takeDown() {
	System.out.println("Agent " + this.id + " has been terminated");
    }

    /**
     * Switch state.
     *
     * @param wantedState
     *            the wanted state
     */
    public void switchState(SemaphoreStates wantedState) {
	if (wantedState == this.state)
	    return;

	System.out.println("Agent " + Integer.toString(this.id) + " changing state to " + this.state.toString());

	for (String agentName : this.connectableAgents) {
	    Context<?> context = ContextUtils.getContext(this);
	    Agent targetAgent = SmartSemaphores.getAgent(context, agentName);
	    @SuppressWarnings("unchecked")
	    Network<Object> net = (Network<Object>) ContextUtils.getContext(this)
		    .getProjection("SmartSemaphores Road Network");
	    if (wantedState == SemaphoreStates.GREEN) {
		net.addEdge(this, targetAgent);
	    } else {
		RepastEdge<Object> edge = net.getEdge(this, targetAgent);
		net.removeEdge(edge);

		this.pedestrianTotalCount += this.pedestrianCount;
		this.pedestrianCount = 0;
		flushPedestrians();
	    }
	}
	this.state = wantedState;
	this.secondsPassedOnState = 0;
    }

    /**
     * Gets the current state.
     *
     * @return the current state
     */
    public SemaphoreStates getCurrentState() {
	return this.state;
    }

    /**
     * Checks for emergency vehicles.
     *
     * @return true, if successful
     */
    public boolean hasEmergencyVehicles() {
	return this.emergency.size() > 0;
    }

    /**
     * Car road ratio.
     *
     * @return float
     */
    public float carRoadRatio() {
	return ((float) this.vehicles.size()) / ((float) this.capacity);
    }

    /**
     * Gets the seconds passed on state.
     *
     * @return the seconds passed on state
     */
    public int getSecondsPassedOnState() {
	return secondsPassedOnState;
    }

    /**
     * Increment seconds on state.
     */
    public void incrementSecondsOnState() {
	this.secondsPassedOnState++;
	this.stateTracker.add(this.state);
    }

    /**
     * Gets the neighbours.
     *
     * @return the neighbours
     */
    public ArrayList<String> getNeighbours() {
	return this.semaphoricAgents;
    }

    /**
     * Gets the sink agent neighbours.
     *
     * @return the sink agent neighbours
     */
    public ArrayList<String> getSinkAgentNeighbours() {
	return this.connectableAgents;
    }

    /*
     * (non-Javadoc)
     * 
     * @see smartSemaphores.jade.RoadAgent#getAvailabeSpace(int)
     */
    @Override
    public int getAvailabeSpace(int increment) {
	if (this.vehicles.size() + increment <= this.capacity)
	    return increment;
	else
	    return this.capacity - this.vehicles.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see smartSemaphores.jade.RoadAgent#addCars(java.util.ArrayList)
     */
    @Override
    public void addCars(ArrayList<NormalVehicle> newCars) {
	this.vehicles.addAll(newCars);
    }

    /**
     * Removes the cars.
     *
     * @param decrement
     *            the decrement
     * @return the array list
     */
    public ArrayList<NormalVehicle> removeCars(int decrement) {
	ArrayList<NormalVehicle> removedCars = new ArrayList<>();

	while (decrement > 0 && this.vehicles.size() > 0) {
	    NormalVehicle car = this.vehicles.element();
	    this.vehicles.remove();
	    removedCars.add(car);
	}

	return removedCars;
    }

    /**
     * Removes the car.
     *
     * @return the normal vehicle
     */
    public NormalVehicle removeCar() {
	NormalVehicle car = null;

	if (this.vehicles.size() > 0) {
	    car = this.vehicles.element();
	    this.vehicles.remove(car);
	}
	return car;
    }

    /**
     * Gets the connectable agents.
     *
     * @return the connectable agents
     */
    public ArrayList<RoadAgent> getConnectableAgents() {
	ArrayList<RoadAgent> neighbours = new ArrayList<>();

	for (String id : this.connectableAgents) {
	    Context<?> context = ContextUtils.getContext(this);
	    Agent agent = SmartSemaphores.getAgent(context, id);
	    neighbours.add((RoadAgent) agent);
	}
	return neighbours;
    }

    /**
     * Gets the neighbour by ID.
     *
     * @param id
     *            the id
     * @return the neighbour by ID
     */
    public Agent getNeighbourByID(String id) {
	Context<?> context = ContextUtils.getContext(this);
	Agent agent = SmartSemaphores.getAgentByID(context, id);
	return agent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * smartSemaphores.jade.RoadAgent#addEmergencyVehicle(smartSemaphores.repast.
     * EmergencyVehicle)
     */
    @Override
    public void addEmergencyVehicle(EmergencyVehicle vehicle) {
	this.emergency.add(vehicle);
    }

    /**
     * Gets the current normal cars.
     *
     * @return the current normal cars
     */
    public int getCurrentNormalCars() {
	return this.vehicles.size();
    }

    /**
     * Removes the emergency vehicle.
     *
     * @return the emergency vehicle
     */
    public EmergencyVehicle removeEmergencyVehicle() {
	EmergencyVehicle v = null;

	if (this.emergency.size() > 0) {
	    v = this.emergency.element();
	    this.emergency.remove();
	}
	return v;
    }

    /**
     * Adds the pedestrian.
     *
     * @param p
     *            the p
     */
    public void addPedestrian(Pedestrian p) {
	this.pedestrianCount++;
	this.pedestrians.add(p);
    }

    /**
     * Gets the pedestrian total count.
     *
     * @return the pedestrian total count
     */
    public int getPedestrianTotalCount() {
	return pedestrianTotalCount;
    }

    /**
     * Flush pedestrians.
     */
    private void flushPedestrians() {
	int tick = SimulationManager.currentTick;
	for (Pedestrian p : this.pedestrians)
	    p.setEndTick(tick);
	this.pedestrians = new ArrayList<>();
    }

    /**
     * Gets the state tracker.
     *
     * @return the state tracker
     */
    public ArrayList<SemaphoreStates> getStateTracker() {
	return this.stateTracker;
    }
}
