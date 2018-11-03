package smartSemaphores.repast;

import java.util.ArrayList;
import java.util.HashMap;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.util.ContextUtils;
import smartSemaphores.SmartSemaphoresRepastLauncher;
import smartSemaphores.jade.SemaphoricAgent;
import smartSemaphores.jade.SinkAgent;

public class SimulationManager
{
	private SmartSemaphoresRepastLauncher simulation;
	private int currentTick = 0;
	private HashMap<String, FluxGenerator> generators;
	private ArrayList<SemaphoricAgent> sourceAgents;
	private ArrayList<SemaphoricAgent> middleAgents;
	private ArrayList<SinkAgent> sinkAgents;


	public SimulationManager(SmartSemaphoresRepastLauncher simulation)
	{
		this.simulation = simulation;
		this.sourceAgents = new ArrayList<>();
		this.middleAgents = new ArrayList<>();
		this.sinkAgents = new ArrayList<>();
		this.generators = new HashMap<>();	
	}
	
	public void init(int[] sources, int[] middles, int[] sinks)
	{
		//Context<?> context = ContextUtils.getContext(this);
		
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
		
		System.out.println(sourceAgents.size());
		
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
		if (currentTick == 0)
			RunEnvironment.getInstance().setScheduleTickDelay(40);
		
		currentTick++;
	}
}
