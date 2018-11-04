package smartSemaphores.jade.behaviours;

import sajas.core.behaviours.Behaviour;
import smartSemaphores.jade.SemaphoreStates;
import smartSemaphores.jade.TimedSemaphoricAgent;
import smartSemaphores.repast.SimulationManager;

public class TimedBehaviour extends Behaviour
{
	private boolean done = false;
	
	@Override
	public void action()
	{
		TimedSemaphoricAgent agent = (TimedSemaphoricAgent)this.myAgent;
		int sequence = agent.getSequence();
		int currentSequence = SimulationManager.currentActiveSequence;
		
		if (sequence == currentSequence)
			agent.switchState(SemaphoreStates.GREEN);
		else
			agent.switchState(SemaphoreStates.RED);
	}

	@Override
	public boolean done()
	{
		return done;
	}
}
