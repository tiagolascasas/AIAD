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
	private ArrayList<Vehicle> injectedVehicles;
	private int exitedCars = 0;
	
	public SimulationManager(SmartSemaphoresRepastLauncher simulation)
	{
		this.simulation = simulation;
		this.sourceAgents = new ArrayList<>();
		this.middleAgents = new ArrayList<>();
		this.sinkAgents = new ArrayList<>();
		this.generators = new HashMap<>();	
		this.injectedVehicles = new ArrayList<>();
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
		
		injectCars();
		
		transferCars();
		
		if (currentTick == SmartSemaphoresRepastLauncher.MAX_TICKS)
			printReportToStandardOutput();
	}

	private void injectCars()
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
					RoadAgent targetAgent = neighbours.get(i);
					int availableSpace = targetAgent.getAvailabeSpace(1);
					int availableCars = agent.getCurrentNormalCars();
					
					if (availableSpace == 1 && availableCars > 0)
					{
						ArrayList<NormalVehicle> cars = new ArrayList<>();
						NormalVehicle car = agent.removeCar();
						cars.add(car);
						targetAgent.addCars(cars);
					}
				}
			}
		}
	}

	private void updateState()
	{
		if (currentTick == 0)
			RunEnvironment.getInstance().setScheduleTickDelay(40);
		
		currentTick++;
	}
	
	private void printReportToStandardOutput()
	{
		for (SemaphoricAgent agent : this.semaphoricAgents)
		{
			int currentCars = agent.getCurrentCars();
			String id = agent.getAID().getName();
			System.out.println(id + ": " + currentCars + " currently waiting here");
		}
		for (SinkAgent sink : this.sinkAgents)
		{
			int currentCars = sink.getCurrentCars();
			String id = sink.getAID().getName();
			System.out.println(id + ": " + currentCars + " exited this way");
			this.exitedCars += currentCars;
		}
		System.out.println("\n" + this.injectedVehicles.size() + " entered the simulation");
		System.out.println("\n" + this.exitedCars + " exited the simulation");
	}
}
