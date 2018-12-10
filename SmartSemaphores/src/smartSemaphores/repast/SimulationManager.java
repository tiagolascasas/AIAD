/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.repast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import smartSemaphores.SimulationType;
import smartSemaphores.SmartSemaphores;
import smartSemaphores.jade.RoadAgent;
import smartSemaphores.jade.SemaphoreStates;
import smartSemaphores.jade.SemaphoricAgent;
import smartSemaphores.jade.SinkAgent;

/**
 * The Class SimulationManager.
 */
public class SimulationManager {

	/** The current tick. */
	public static int currentTick = 0;

	/** The current active sequence. */
	public static int currentActiveSequence = 1;

	/** The current active count. */
	private static int currentActiveCount = 0;

	/** The duration of a cycle for timed semaphores */
	private static final int TIMED_CYCLE = 30;

	/** The simulation. */
	private SmartSemaphores simulation;

	/** The generators. */
	private HashMap<String, FluxGenerator> generators;

	/** The pedestrian probs. */
	private HashMap<String, Double> pedestrianProbs;

	/** The source agents. */
	private ArrayList<SemaphoricAgent> sourceAgents;

	/** The middle agents. */
	private ArrayList<SemaphoricAgent> middleAgents;

	/** The sink agents. */
	private ArrayList<SinkAgent> sinkAgents;

	/** The semaphoric agents. */
	private ArrayList<SemaphoricAgent> semaphoricAgents;

	/** The injected vehicles. */
	private ArrayList<NormalVehicle> injectedVehicles;

	/** The injected emergency. */
	private ArrayList<EmergencyVehicle> injectedEmergency;

	/** The pedestrians. */
	private ArrayList<Pedestrian> pedestrians;

	/**
	 * Instantiates a new simulation manager.
	 *
	 * @param simulation the simulation
	 */
	public SimulationManager(SmartSemaphores simulation) {
		this.simulation = simulation;
		this.sourceAgents = new ArrayList<>();
		this.middleAgents = new ArrayList<>();
		this.sinkAgents = new ArrayList<>();
		this.generators = new HashMap<>();
		this.pedestrianProbs = new HashMap<>();
		this.injectedVehicles = new ArrayList<>();
		this.injectedEmergency = new ArrayList<>();
		this.pedestrians = new ArrayList<>();
	}

	/**
	 * Inits the simulation.
	 *
	 * @param sources the sources
	 * @param middles the middles
	 * @param sinks   the sinks
	 */
	public void init(int[] sources, int[] middles, int[] sinks) {
		for (int i : sources) {
			sourceAgents.add((SemaphoricAgent) simulation.getAgent(i));
		}

		for (int i : middles) {
			middleAgents.add((SemaphoricAgent) simulation.getAgent(i));
		}

		for (int i : sinks) {
			sinkAgents.add((SinkAgent) simulation.getAgent(i));
		}

		this.semaphoricAgents = new ArrayList<>();
		this.semaphoricAgents.addAll(this.sourceAgents);
		this.semaphoricAgents.addAll(this.middleAgents);

		for (SemaphoricAgent agent : sourceAgents) {
			FluxGenerator generator = new FluxGeneratorSinusoid(RandomHelper.nextInt());
			String name = agent.getAID().getName();
			this.generators.put(name, generator);
		}

		for (SemaphoricAgent agent : semaphoricAgents) {
			double prob = Math.random() / 4;
			this.pedestrianProbs.put(agent.getAID().getName(), prob);
		}
	}

	/**
	 * Step.
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		updateState();

		injectNormalVehicles();
		injectEmergencyVehicles();
		injectPedestrians();
		transferCars();

		if (currentTick == SmartSemaphores.MAX_TICKS) {
			if (!SmartSemaphores.DATA_REPORTING)
				generateReport();
			else
				printBasicReportToStdout();
			SimulationManager.currentTick = 0;
		}
		
		for (SemaphoricAgent agent : this.semaphoricAgents)
			agent.incrementCounters();
	}

	/**
	 * Inject normal vehicles.
	 */
	private void injectNormalVehicles() {
		for (int i = 0; i < this.sourceAgents.size(); i++) {
			SemaphoricAgent source = this.sourceAgents.get(i);
			FluxGenerator generator = this.generators.get(source.getAID().getName());
			int id = source.getID();
			int increment = generator.calculateY(SimulationManager.currentTick);

			ArrayList<NormalVehicle> newCars = new ArrayList<>();

			int availableSpace = source.getAvailabeSpace(increment);
			for (int j = 0; j < availableSpace; j++)
				newCars.add(new NormalVehicle(SimulationManager.currentTick, id));

			source.addCars(newCars);
			this.injectedVehicles.addAll(newCars);
		}
	}

	/**
	 * Inject emergency vehicles.
	 */
	private void injectEmergencyVehicles() {
		double prob = SmartSemaphores.EMERGENCY_PROBABILITY;

		for (SemaphoricAgent agent : this.sourceAgents) {
			double random = Math.random();
			if (random < prob) {
				EmergencyVehicle vehicle = new EmergencyVehicle(SimulationManager.currentTick, agent.getID());
				agent.addEmergencyVehicle(vehicle);
				this.injectedEmergency.add(vehicle);
			}
		}

	}

	/**
	 * Inject pedestrians.
	 */
	private void injectPedestrians() {
		for (SemaphoricAgent agent : this.semaphoricAgents) {
			if (agent.getCurrentState() == SemaphoreStates.RED) {
				double prob = this.pedestrianProbs.get(agent.getAID().getName());
				double random = Math.random();
				if (random < prob) {
					int agentID = agent.getID();
					int tick = SimulationManager.currentTick;
					Pedestrian pedestrian = new Pedestrian(agentID, tick);

					agent.addPedestrian(pedestrian);
					this.pedestrians.add(pedestrian);
				}
			}
		}

	}

	/**
	 * Transfer cars.
	 */
	private void transferCars() {
		for (SemaphoricAgent agent : this.semaphoricAgents) {
			if (agent.getCurrentState() == SemaphoreStates.GREEN) {
				ArrayList<RoadAgent> neighbours = agent.getConnectableAgents();
				int possibilites = neighbours.size();

				for (int i = 0; i < SmartSemaphores.EXIT_RATE; i++) {
					int road = RandomHelper.nextIntFromTo(0, possibilites - 1);
					RoadAgent targetAgent = neighbours.get(road);
					int availableSpace = targetAgent.getAvailabeSpace(1);
					int availableCars = agent.getCurrentNormalCars();

					if (availableSpace == 1 && availableCars > 0) {
						ArrayList<NormalVehicle> cars = new ArrayList<>();
						NormalVehicle car = agent.removeCar();
						cars.add(car);
						targetAgent.addCars(cars);
					}

					if (agent.getEmergencyVehicles().size() > 0) {
						EmergencyVehicle vehicle = agent.removeEmergencyVehicle();
						targetAgent.addEmergencyVehicle(vehicle);
					}
				}
			}
		}
	}

	/**
	 * Update state.
	 */
	private void updateState() {
		if (currentTick == 0) {
			if (SmartSemaphores.DATA_REPORTING)
				RunEnvironment.getInstance().setScheduleTickDelay(0);
			else
				RunEnvironment.getInstance().setScheduleTickDelay(40);
			injectStartingCars();

			if (SmartSemaphores.SIMULATION_TYPE != SimulationType.TIMED_AGENTS)
				initGreenAgents();
		}

		if (SmartSemaphores.SIMULATION_TYPE == SimulationType.TIMED_AGENTS) {
			SimulationManager.currentActiveCount++;
			if (SimulationManager.currentActiveCount == SimulationManager.TIMED_CYCLE) {
				SimulationManager.currentActiveCount = 0;
				SimulationManager.currentActiveSequence++;
				if (SimulationManager.currentActiveSequence == 5)
					SimulationManager.currentActiveSequence = 1;
			}
		}

		for (SemaphoricAgent agent : this.semaphoricAgents)
			agent.incrementSecondsOnState();

		currentTick++;
	}

	/**
	 * Initializes the green agents.
	 */
	private void initGreenAgents() {
		Integer[] startingGreen = { 1, 6, 15 };
		for (SemaphoricAgent agent : this.sourceAgents) {
			int id = agent.getID();
			if (Arrays.asList(startingGreen).contains(id))
				agent.switchState(SemaphoreStates.GREEN);
		}
	}

	/**
	 * Inject starting cars.
	 */
	private void injectStartingCars() {
		for (SemaphoricAgent agent : this.sourceAgents) {
			int min = SmartSemaphores.MIN_STARTING_CARS;
			int max = SmartSemaphores.MAX_STARTING_CARS;
			int numberOfCars = RandomHelper.nextIntFromTo(min, max);
			ArrayList<NormalVehicle> vehicles = new ArrayList<>();

			for (int i = 0; i < numberOfCars; i++) {
				NormalVehicle vehicle = new NormalVehicle(0, agent.getID());
				vehicles.add(vehicle);
			}
			agent.addCars(vehicles);
		}
	}

	/**
	 * Generate report.
	 */
	private void generateReport() {
		long unixTime = System.currentTimeMillis() / 1000L;
		String uniqueID = "" + unixTime;

		ArrayList<Vehicle> exitedNormal = getExitedVehicles(this.injectedVehicles);
		ArrayList<Vehicle> exitedEmer = getExitedVehicles(this.injectedEmergency);

		StatisticReportsCreator.generateVariablesReport(uniqueID);
		StatisticReportsCreator.generateAverageTimesDatasets(uniqueID, exitedNormal, exitedEmer);
		StatisticReportsCreator.generateAllTimesDatasets(uniqueID, exitedNormal, exitedEmer);
		StatisticReportsCreator.generateSemaphoreDataset(uniqueID, this.semaphoricAgents);
		StatisticReportsCreator.generatePedestrianReport(uniqueID, pedestrians);

		this.printBasicReportToStdout();
	}

	/**
	 * Prints the basic report to stdout.
	 */
	private void printBasicReportToStdout() {

		int exitedVehicles = 0;
		int exitedEmergency = 0;

		if (!SmartSemaphores.DATA_REPORTING) {
			for (SemaphoricAgent agent : this.semaphoricAgents) {
				int currentCars = agent.getCurrentNormalVehicles();
				int pedCount = agent.getPedestrianTotalCount();
				String id = agent.getAID().getName();
				System.out.println(
						id + ": " + currentCars + " currently waiting here. " + pedCount + " pedestrians crossed");
			}

			for (SinkAgent sink : this.sinkAgents) {
				int currentCars = sink.getCurrentNormalVehicles();
				int currentEmergency = sink.getCurrentEmergencyVehicles();
				String id = sink.getAID().getName();
				System.out.println(id + ": " + currentCars + " exited this way");
				exitedVehicles += currentCars;
				exitedEmergency += currentEmergency;
			}
		}

		ArrayList<Vehicle> exitedNormal = getExitedVehicles(this.injectedVehicles);
		ArrayList<Vehicle> exitedEmer = getExitedVehicles(this.injectedEmergency);

		TimesTable t1 = new TimesTable(exitedNormal);
		TimesTable t2 = new TimesTable(exitedEmer);
		
		HashMap<Integer, Integer> greenTimes = new HashMap<>();
		HashMap<Integer, Double> variances = new HashMap<>();
		
		for (SemaphoricAgent agent : this.semaphoricAgents)
		{
			ArrayList<Integer> times = agent.getGreenTimes();
			int avg = TimesTable.getAverage(times);
			double variance = TimesTable.variance(times);
			greenTimes.put(agent.getID(), avg);
			variances.put(agent.getID(), variance);
		}

		if (SmartSemaphores.DATA_REPORTING)
		{
			System.out.println("Finished simulation no. " + SmartSemaphores.SIM_COUNT);
			SmartSemaphores.SIM_COUNT++;
			StatisticReportsCreator.saveToDataset(t1);
			StatisticReportsCreator.saveToSemaphoricDataset(greenTimes, variances);
		}

		if (!SmartSemaphores.DATA_REPORTING) {
			System.out.println("\n" + this.injectedVehicles.size() + " normal vehicles entered the simulation");
			System.out.println(this.injectedEmergency.size() + " emergency vehicles entered the simulation");
			System.out.println("\n" + exitedVehicles + " normal vehicles exited the simulation");
			System.out.println(exitedEmergency + " emergency vehicles exited the simulation");

			System.out.println("------------------------------");
			System.out.println("Normal vehicles:");
			t1.printTableToStdout();
			System.out.println("------------------------------");
			System.out.println("Emergency vehicles:");
			t2.printTableToStdout();
			System.out.println("------------------------------");
		}
	}

	/**
	 * Gets the exited vehicles.
	 *
	 * @param injected the injected
	 * @return the exited vehicles
	 */
	private ArrayList<Vehicle> getExitedVehicles(ArrayList<? extends Vehicle> injected) {
		ArrayList<Vehicle> vehicles = new ArrayList<>();
		for (Vehicle v : injected) {
			if (v.hasExited())
				vehicles.add(v);
		}
		return vehicles;
	}
}
