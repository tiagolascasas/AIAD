package smartSemaphores.repast;

import java.util.ArrayList;
import java.util.HashMap;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.util.ContextUtils;
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
			FluxGenerator generator = new FluxGenerator(RandomHelper.nextInt());
			String name = agent.getAID().getName();
			this.generators.put(name, generator);
		}
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step()
	{
		updateState();
		
		injectNormalVehicles();
		injectEmergencyVehicles();
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
			int increment = generator.calculateY(this.currentTick);
			
			ArrayList<NormalVehicle> newCars = new ArrayList<>();
			
			int availableSpace = source.getAvailabeSpace(increment);
			for (int j = 0; j < availableSpace; j++)
				newCars.add(new NormalVehicle(this.currentTick, id));
			
			source.addCars(newCars);
			this.injectedVehicles.addAll(newCars);
		}
	}

	private void injectEmergencyVehicles()
	{
		double prob = simulation.EMERGENCY_PROBABILITY;
		
		for (SemaphoricAgent agent : this.sourceAgents)
		{
			double random = Math.random();
			if (random < prob)
			{
				EmergencyVehicle vehicle = new EmergencyVehicle(this.currentTick, agent.getID());
				agent.addEmergencyVehicle(vehicle);
				this.injectedEmergency.add(vehicle);
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
		
		if (this.simulation.TIMED_AGENTS)
		{
			this.currentActiveCount++;
			if (this.currentActiveCount == 60)
			{
				this.currentActiveCount = 0;
				this.currentActiveSequence++;
				if (this.currentActiveSequence == 5)
					this.currentActiveSequence = 1;
			}
		}
		
		currentTick++;
	}
	
	private void printReportToStandardOutput()
	{
		for (SemaphoricAgent agent : this.semaphoricAgents)
		{
			int currentCars = agent.getCurrentNormalVehicles();
			String id = agent.getAID().getName();
			System.out.println(id + ": " + currentCars + " currently waiting here");
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
	}
}
