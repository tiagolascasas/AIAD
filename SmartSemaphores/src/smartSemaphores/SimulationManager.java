package smartSemaphores;

import repast.simphony.engine.schedule.ScheduledMethod;

public class SimulationManager
{
	private SmartSemaphoresRepastLauncher simulation;

	SimulationManager(SmartSemaphoresRepastLauncher simulation)
	{
		this.simulation = simulation;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step()
	{
		System.out.println("asdasd");
	}
}
