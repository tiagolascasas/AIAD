package smartSemaphores.repast;

import java.util.ArrayList;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import smartSemaphores.SmartSemaphoresRepastLauncher;
import smartSemaphores.jade.SemaphoricAgent;

public class SimulationManager
{
	private SmartSemaphoresRepastLauncher simulation;
	private int currentTick = 0;
	private ArrayList<SemaphoricAgent> agents;
	private ArrayList<FluxGenerator> generators;


	public SimulationManager(SmartSemaphoresRepastLauncher simulation)
	{
		this.simulation = simulation;

		
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step()
	{
		currentTick++;

	}
}
