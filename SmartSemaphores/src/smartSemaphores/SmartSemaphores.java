/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores;

import java.awt.Point;
import java.util.ArrayList;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.StickyBorders;
import sajas.core.Agent;
import sajas.core.Runtime;
import sajas.sim.repasts.RepastSLauncher;
import sajas.wrapper.ContainerController;
import smartSemaphores.jade.ConsensualSemaphoricAgent;
import smartSemaphores.jade.RoadAgent;
import smartSemaphores.jade.SemaphoricAgent;
import smartSemaphores.jade.SinkAgent;
import smartSemaphores.jade.SmartSemaphoricAgent;
import smartSemaphores.jade.TimedSemaphoricAgent;
import smartSemaphores.repast.SimulationManager;

/**
 * The Class SmartSemaphores.
 */
public class SmartSemaphores extends RepastSLauncher implements ContextBuilder<Object> {

    /** The Constant TICKS_PER_SECOND. */
    public static final float TICKS_PER_SECOND = 1.0f;

    /** The simulation type. */
    public static SimulationType SIMULATION_TYPE = SimulationType.CONSENSUAL_AGENTS;

    /** The hours for the simulation. */
    public static int HOURS = 5;

    /** The vehicle exit rate. */
    public static int EXIT_RATE = 3;

    /** The max ticks. */
    public static int MAX_TICKS;

    /** The min starting cars. */
    public static int MIN_STARTING_CARS = 100;

    /** The max starting cars. */
    public static int MAX_STARTING_CARS = 800;

    /** The emergency probability. */
    public static double EMERGENCY_PROBABILITY = 0.001f;

    /** The pedestrian probability. */
    public static double PEDESTRIAN_PROBABILITY = 0.15f;

    // JADE containers and agents
    /** The main container. */
    @SuppressWarnings("unused")
    private ContainerController mainContainer;

    /** The cross container A. */
    private ContainerController crossContainerA;

    /** The cross container B. */
    private ContainerController crossContainerB;

    /** The cross container C. */
    private ContainerController crossContainerC;

    /** The agents. */
    private ArrayList<RoadAgent> agents;

    /** Simulation manager (repast agent) */
    private SimulationManager manager;

    /** The grid. */
    private Grid<Object> grid;

    /** The context. */
    private Context<Object> context;

    /*
     * (non-Javadoc)
     * 
     * @see sajas.sim.repasts.RepastSLauncher#getName()
     */
    @Override
    public String getName() {
	return "SmartSemaphores";
    }

    /*
     * (non-Javadoc)
     * 
     * @see sajas.sim.repasts.RepastSLauncher#launchJADE()
     */
    @Override
    protected void launchJADE() {
	Runtime rt = Runtime.instance();
	Profile p0 = new ProfileImpl();
	Profile p1 = new ProfileImpl();
	Profile p2 = new ProfileImpl();
	Profile p3 = new ProfileImpl();

	this.mainContainer = rt.createMainContainer(p0);
	this.crossContainerA = rt.createAgentContainer(p1);
	this.crossContainerB = rt.createAgentContainer(p2);
	this.crossContainerC = rt.createAgentContainer(p3);

	this.agents = new ArrayList<>();

	switch (SmartSemaphores.SIMULATION_TYPE) {
	case SMART_AGENTS:
	    launchSmartAgents(true);
	    break;
	case CONSENSUAL_AGENTS:
	    launchSmartAgents(false);
	    break;
	case TIMED_AGENTS:
	    launchTimedAgents();
	    break;
	}

	int[] sources = { 1, 8, 10, 6, 15, 17 };
	int[] middles = { 3, 4, 11, 12, 13, 14 };
	int[] sinks = { 2, 7, 9, 5, 16, 18 };
	this.manager.init(sources, middles, sinks);
    }

    /**
     * Launch smart agents.
     *
     * @param smart
     *            Launches smart agents if true, consensus agents if false
     * 
     */
    private void launchSmartAgents(boolean smart) {
	try {
	    // Semaphoric agents on road cross A
	    int[] crossAagents = { 1, 11, 4, 8 };
	    int[] crossAconnectables = { 2, 7, 12, 3 };
	    for (int i : crossAagents) {
		SemaphoricAgent agent;
		if (smart)
		    agent = new SmartSemaphoricAgent(i, crossAagents, crossAconnectables, 1000);
		else
		    agent = new ConsensualSemaphoricAgent(i, crossAagents, crossAconnectables, 1000);
		this.crossContainerA.acceptNewAgent("Agent " + i, agent).start();
		this.agents.add(agent);

		putOnGrid(i, agent);

	    }

	    // Semaphoric agents on road cross B
	    int[] crossBagents = { 3, 13, 6, 10 };
	    int[] crossBconnectables = { 5, 9, 4, 14 };
	    for (int i : crossBagents) {
		SemaphoricAgent agent;
		if (smart)
		    agent = new SmartSemaphoricAgent(i, crossBagents, crossBconnectables, 1000);
		else
		    agent = new ConsensualSemaphoricAgent(i, crossBagents, crossBconnectables, 1000);
		this.crossContainerB.acceptNewAgent("Agent " + i, agent).start();
		this.agents.add(agent);

		putOnGrid(i, agent);
	    }

	    // Semaphoric agents on road cross C
	    int[] crossCagents = { 14, 17, 15, 12 };
	    int[] crossCconnectables = { 18, 16, 11, 13 };
	    for (int i : crossCagents) {
		SemaphoricAgent agent;
		if (smart)
		    agent = new SmartSemaphoricAgent(i, crossCagents, crossCconnectables, 1000);
		else
		    agent = new ConsensualSemaphoricAgent(i, crossCagents, crossCconnectables, 1000);
		this.crossContainerC.acceptNewAgent("Agent " + i, agent).start();
		this.agents.add(agent);

		putOnGrid(i, agent);
	    }

	    initSinkAgents();

	} catch (StaleProxyException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Launch timed agents.
     */
    private void launchTimedAgents() {

	try {
	    // Semaphoric agents on road cross A
	    int[] crossAagents = { 1, 11, 4, 8 };
	    int[] crossAconnectables = { 2, 7, 12, 3 };
	    int seq = 1;
	    for (int i : crossAagents) {
		TimedSemaphoricAgent agent = new TimedSemaphoricAgent(i, crossAagents, crossAconnectables, 1000, seq);
		this.crossContainerA.acceptNewAgent("Agent " + i, agent).start();
		this.agents.add(agent);
		seq++;

		putOnGrid(i, agent);
	    }

	    // Semaphoric agents on road cross B
	    int[] crossBagents = { 3, 13, 6, 10 };
	    int[] crossBconnectables = { 5, 9, 4, 14 };
	    seq = 1;
	    for (int i : crossBagents) {
		TimedSemaphoricAgent agent = new TimedSemaphoricAgent(i, crossBagents, crossBconnectables, 1000, seq);
		this.crossContainerB.acceptNewAgent("Agent " + i, agent).start();
		this.agents.add(agent);
		seq++;

		putOnGrid(i, agent);
	    }

	    // Semaphoric agents on road cross C
	    int[] crossCagents = { 14, 17, 15, 12 };
	    int[] crossCconnectables = { 18, 16, 11, 13 };
	    seq = 1;
	    for (int i : crossCagents) {
		TimedSemaphoricAgent agent = new TimedSemaphoricAgent(i, crossCagents, crossCconnectables, 1000, seq);
		this.crossContainerC.acceptNewAgent("Agent " + i, agent).start();
		this.agents.add(agent);
		seq++;

		putOnGrid(i, agent);
	    }

	    initSinkAgents();

	} catch (StaleProxyException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Inits the sink agents.
     *
     * @throws StaleProxyException
     *             the stale proxy exception
     */
    private void initSinkAgents() throws StaleProxyException {
	// Sink Agents (two sinks per cross)
	SinkAgent sinkAgent;

	sinkAgent = new SinkAgent(2);
	this.crossContainerA.acceptNewAgent("Agent 2", sinkAgent).start();
	this.agents.add(sinkAgent);
	putOnGrid(2, sinkAgent);

	sinkAgent = new SinkAgent(7);
	this.crossContainerA.acceptNewAgent("Agent 7", sinkAgent).start();
	this.agents.add(sinkAgent);
	putOnGrid(7, sinkAgent);

	sinkAgent = new SinkAgent(5);
	this.crossContainerB.acceptNewAgent("Agent 5", sinkAgent).start();
	this.agents.add(sinkAgent);
	putOnGrid(5, sinkAgent);

	sinkAgent = new SinkAgent(9);
	this.crossContainerB.acceptNewAgent("Agent 9", sinkAgent).start();
	this.agents.add(sinkAgent);
	putOnGrid(9, sinkAgent);

	sinkAgent = new SinkAgent(16);
	this.crossContainerC.acceptNewAgent("Agent 16", sinkAgent).start();
	this.agents.add(sinkAgent);
	putOnGrid(16, sinkAgent);

	sinkAgent = new SinkAgent(18);
	this.crossContainerC.acceptNewAgent("Agent 18", sinkAgent).start();
	this.agents.add(sinkAgent);
	putOnGrid(18, sinkAgent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sajas.sim.repasts.RepastSLauncher#build(repast.simphony.context.Context)
     */
    @Override
    public Context<?> build(Context<Object> context) {
	this.context = context;

	initSimulation();

	GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);

	// implement strict borders so there is no wrapping
	// multiple occupancy of grid cells is still set to false
	this.grid = gridFactory.createGrid("SmartSemaphores Road Grid", context,
		new GridBuilderParameters<Object>(new StickyBorders(), new SimpleGridAdder<Object>(), false, 15, 19));

	NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("SmartSemaphores Road Network", context, true);
	netBuilder.buildNetwork();

	this.manager = new SimulationManager(this);
	context.add(this.manager);

	return super.build(context);
    }

    /**
     * Initializes the simulation.
     */
    private void initSimulation() {
	Parameters params = RunEnvironment.getInstance().getParameters();

	switch ((String) params.getValue("simulationType")) {

	case "TIMED_AGENTS":
	    SIMULATION_TYPE = SimulationType.TIMED_AGENTS;
	    break;
	case "CONSENSUAL_AGENTS":
	    SIMULATION_TYPE = SimulationType.CONSENSUAL_AGENTS;
	    break;
	case "SMART_AGENTS":
	default:
	    SIMULATION_TYPE = SimulationType.SMART_AGENTS;
	    break;
	}
	;

	HOURS = (int) params.getValue("hours");
	EXIT_RATE = (int) params.getValue("exitRate");
	MIN_STARTING_CARS = (int) params.getValue("minStartCars");
	MAX_STARTING_CARS = (int) params.getValue("maxStartCars");
	EMERGENCY_PROBABILITY = (double) params.getValue("emergencyProbability");
	PEDESTRIAN_PROBABILITY = (double) params.getValue("pedestrianProbability");

	MAX_TICKS = (int) (TICKS_PER_SECOND * 3600 * HOURS);
	RunEnvironment.getInstance().endAt(MAX_TICKS);
	RunEnvironment.getInstance().setScheduleTickDelay(70);
    }

    /**
     * Gets the specific agent given a name.
     *
     * @param context
     *            the context
     * @param name
     *            the name
     * @return the agent
     */
    public static Agent getAgent(Context<?> context, String name) {
	for (Object obj : context.getObjects(Agent.class)) {
	    if (((Agent) obj).getAID().getName().equals(name)) {
		return (Agent) obj;
	    }
	}
	return null;
    }

    /**
     * Gets the agent by ID correspondent to a given name.
     *
     * @param context
     *            the context
     * @param name
     *            the name
     * @return the agent by ID
     */
    public static Agent getAgentByID(Context<?> context, String name) {
	for (Object obj : context.getObjects(Agent.class)) {
	    if (obj instanceof RoadAgent && ((RoadAgent) obj).getID() == Integer.parseInt(name))
		;
	    return (Agent) obj;
	}
	return null;
    }

    /**
     * Gets the agent.
     *
     * @param i
     *            the i
     * @return the agent
     */
    public RoadAgent getAgent(int i) {
	String id = RoadAgent.makeFullName(i);

	for (RoadAgent agent : this.agents) {
	    if (agent.getAID().getName().equals(id))
		return agent;
	}
	return null;
    }

    /**
     * Semaphore location.
     *
     * @param numAgent
     *            the num agent
     * @return the point
     */
    public Point semaphoreLocation(int numAgent) {
	int x, y;

	switch (numAgent) {
	case 1:
	    x = 3;
	    y = 15;
	    break;
	case 2:
	    x = 4;
	    y = 17;
	    break;
	case 3:
	    x = 3;
	    y = 5;
	    break;
	case 4:
	    x = 5;
	    y = 13;
	    break;
	case 5:
	    x = 4;
	    y = 1;
	    break;
	case 6:
	    x = 5;
	    y = 3;
	    break;
	case 7:
	    x = 1;
	    y = 14;
	    break;
	case 8:
	    x = 3;
	    y = 13;
	    break;
	case 9:
	    x = 1;
	    y = 4;
	    break;
	case 10:
	    x = 3;
	    y = 3;
	    break;
	case 11:
	    x = 5;
	    y = 15;
	    break;
	case 12:
	    x = 10;
	    y = 10;
	    break;
	case 13:
	    x = 5;
	    y = 5;
	    break;
	case 14:
	    x = 10;
	    y = 8;
	    break;
	case 15:
	    x = 12;
	    y = 10;
	    break;
	case 16:
	    x = 13;
	    y = 12;
	    break;
	case 17:
	    x = 12;
	    y = 8;
	    break;
	case 18:
	    x = 13;
	    y = 6;
	    break;
	default:
	    return null;
	}

	return new Point(x, y);
    }

    /**
     * Add agent to a grid.
     *
     * @param i
     *            the i
     * @param agent
     *            the agent
     */
    private void putOnGrid(int i, Agent agent) {
	context.add(agent);
	Point location = semaphoreLocation(i);
	if (location != null) {
	    this.grid.moveTo(agent, (int) location.getX(), (int) location.getY());
	}
    }
}
