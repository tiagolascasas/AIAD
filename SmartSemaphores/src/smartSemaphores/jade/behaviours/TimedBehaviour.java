package smartSemaphores.jade.behaviours;

import sajas.core.behaviours.CyclicBehaviour;
import smartSemaphores.jade.SemaphoreStates;
import smartSemaphores.jade.TimedSemaphoricAgent;
import smartSemaphores.repast.SimulationManager;

public class TimedBehaviour extends CyclicBehaviour
{
	private static final long serialVersionUID = -7448453660507939119L;
	private int time = 0;
	
	@Override
	public void action()
	{
		time++;
		System.out.println(time);
		TimedSemaphoricAgent agent = (TimedSemaphoricAgent)this.myAgent;
		int sequence = agent.getSequence();
		int currentSequence = SimulationManager.currentActiveSequence;
		
		if (sequence == currentSequence)
			agent.switchState(SemaphoreStates.GREEN);
		else
			agent.switchState(SemaphoreStates.RED);
	}
}
