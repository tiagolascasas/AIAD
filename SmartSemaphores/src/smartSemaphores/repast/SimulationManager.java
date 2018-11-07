package smartSemaphores.repast;

import java.util.ArrayList;
import java.util.HashMap;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import smartSemaphores.SimulationType;
import smartSemaphores.SmartSemaphoresRepastLauncher;
import smartSemaphores.jade.RoadAgent;
import smartSemaphores.jade.SemaphoreStates;
import smartSemaphores.jade.SemaphoricAgent;
import smartSemaphores.jade.SinkAgent;

public class SimulationManager
{
	public static int currentTick = 0;
	
	private SmartSemaphoresRepastLauncher simulation;
	private HashMap<String, FluxGenerator> generators;
	private HashMap<String, Double> pedestrianProbs;
	private ArrayList<SemaphoricAgent> sourceAgents;
	private ArrayList<SemaphoricAgent> middleAgents;
	private ArrayList<SinkAgent> sinkAgents;
	private ArrayList<SemaphoricAgent> semaphoricAgents;
	private ArrayList<NormalVehicle> injectedVehicles;
	private ArrayList<EmergencyVehicle> injectedEmergency;
	
	//variables for timed semaphores
	public static int currentActiveSequence = 1;
	private static int currentActiveCount = 0;
	
	public SimulationManager(SmartSemaphoresRepastLauncher simulation)
	{
		this.simulation = simulation;
		this.sourceAgents = new ArrayList<>();
		this.middleAgents = new ArrayList<>();
		this.sinkAgents = new ArrayList<>();
		this.generators = new HashMap<>();
		this.pedestrianProbs = new HashMap<>();
		this.injectedVehicles = new ArrayList<>();
		this.injectedEmergency = new ArrayList<>();
	}
	
	public void init(int[] sources, int[] middles, int[] sinks)
	{
		for (int i : sources)
		{
			sourceAgents.add((SemaphoricAgent) simulation.getAgent(i));
		}
		
		for (int i : middles)
		{
			middleAgents.add((SemaphoricAgent) simulation.getAgent(i));
		}
		
		for (int i : sinks)
		{
			sinkAgents.add((SinkAgent) simulation.getAgent(i));
		}
		
		this.semaphoricAgents = new ArrayList<>();
		this.semaphoricAgents.addAll(this.sourceAgents);
		this.semaphoricAgents.addAll(this.middleAgents);
		
		for (SemaphoricAgent agent : sourceAgents)
		{
			//FluxGeneratorPolynomial generator = new FluxGeneratorPolynomial(RandomHelper.nextInt());
			FluxGenerator generator = new FluxGeneratorSinusoid(RandomHelper.nextInt());
			String name = agent.getAID().getName();
			this.generators.put(name, generator);
		}
		
		for (SemaphoricAgent agent : semaphoricAgents)
		{
			double prob = Math.random() / 4;
			this.pedestrianProbs.put(agent.getAID().getName(), prob);
		}
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step()
	{
		updateState();
		
		injectNormalVehicles();
		injectEmergencyVehicles();
		injectPedestrians();
		transferCars();
		
		if (currentTick == SmartSemaphoresRepastLauncher.MAX_TICKS)
			printReportToStandardOutput();
	}

	private void injectNormalVehicles()
	{
		for (int i = 0; i < this.sourceAgents.size(); i++)
		{
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

	private void injectEmergencyVehicles()
	{
		double prob = SmartSemaphoresRepastLauncher.EMERGENCY_PROBABILITY;
		
		for (SemaphoricAgent agent : this.sourceAgents)
		{
			double random = Math.random();
			if (random < prob)
			{
				EmergencyVehicle vehicle = new EmergencyVehicle(SimulationManager.currentTick, agent.getID());
				agent.addEmergencyVehicle(vehicle);
				this.injectedEmergency.add(vehicle);
			}
		}
		
	}

	private void injectPedestrians()
	{
		for (SemaphoricAgent agent : this.semaphoricAgents)
		{
			if (agent.getCurrentState() == SemaphoreStates.RED)
			{
				double prob = this.pedestrianProbs.get(agent.getAID().getName());
				double random = Math.random();
				if (random < prob)
					agent.addPedestrian();
			}
		}
		
	}

	private void transferCars()
	{
		for (SemaphoricAgent agent : this.semaphoricAgents)
		{
			if (agent.getCurrentState() == SemaphoreStates.GREEN)
			{
				ArrayList<RoadAgent> neighbours = agent.getConnectableAgents();
				int possibilites = neighbours.size();
				
				for (int i = 0; i < SmartSemaphoresRepastLauncher.EXIT_RATE; i++)
				{
					int road = RandomHelper.nextIntFromTo(0, possibilites - 1);
					RoadAgent targetAgent = neighbours.get(road);
					int availableSpace = targetAgent.getAvailabeSpace(1);
					int availableCars = agent.getCurrentNormalCars();
					
					if (availableSpace == 1 && availableCars > 0)
					{
						ArrayList<NormalVehicle> cars = new ArrayList<>();
						NormalVehicle car = agent.removeCar();
						cars.add(car);
						targetAgent.addCars(cars);
					}
					
					if (agent.getEmergencyVehicles().size() > 0)
					{
						EmergencyVehicle vehicle = agent.removeEmergencyVehicle();
						targetAgent.addEmergencyVehicle(vehicle);
					}
				}
			}
		}
	}

	private void updateState()
	{
		if (currentTick == 0)
			RunEnvironment.getInstance().setScheduleTickDelay(40);
		
		if (SmartSemaphoresRepastLauncher.simulationType == SimulationType.TIMED_AGENTS)
		{
			SimulationManager.currentActiveCount++;
			if (SimulationManager.currentActiveCount == 60)
			{
				SimulationManager.currentActiveCount = 0;
				SimulationManager.currentActiveSequence++;
				if (SimulationManager.currentActiveSequence == 5)
					SimulationManager.currentActiveSequence = 1;
			}
		}
		
		currentTick++;
	}
	
	private void printReportToStandardOutput()
	{
		for (SemaphoricAgent agent : this.semaphoricAgents)
		{
			int currentCars = agent.getCurrentNormalVehicles();
			int pedCount = agent.getPedestrianTotalCount();
			String id = agent.getAID().getName();
			System.out.println(id + ": " + currentCars + " currently waiting here. " + pedCount + " pedestrians crossed");
		}
		
		int exitedVehicles = 0;
		int exitedEmergency = 0;
		
		for (SinkAgent sink : this.sinkAgents)
		{
			int currentCars = sink.getCurrentNormalVehicles();
			int currentEmergency = sink.getCurrentEmergencyVehicles();
			String id = sink.getAID().getName();
			System.out.println(id + ": " + currentCars + " exited this way");
			exitedVehicles += currentCars;
			exitedEmergency += currentEmergency;
		}
		
		System.out.println("\n" + this.injectedVehicles.size() + " normal vehicles entered the simulation");
		System.out.println(this.injectedEmergency.size() + " emergency vehicles entered the simulation");
		System.out.println("\n" + exitedVehicles + " normal vehicles exited the simulation");
		System.out.println(exitedEmergency + " emergency vehicles exited the simulation");
		
		ArrayList<Vehicle> exitedNormal = getExitedVehicles(this.injectedVehicles);
		ArrayList<Vehicle> exitedEmer = getExitedVehicles(this.injectedEmergency);
		
		TimesTable t1 = new TimesTable(exitedNormal);
		TimesTable t2 = new TimesTable(exitedEmer);
		
		System.out.println("------------------------------");
		System.out.println("Normal vehicles:");
		t1.printTableToStdout();
		System.out.println("------------------------------");
		System.out.println("Emergency vehicles:");
		t2.printTableToStdout();
		System.out.println("------------------------------");
	}

	private ArrayList<Vehicle> getExitedVehicles(ArrayList<? extends Vehicle> injected)
	{
		ArrayList<Vehicle> vehicles = new ArrayList<>();
		for (Vehicle v : injected)
		{
			if (v.hasExited())
				vehicles.add(v);
		}
		return vehicles;
	}
}
