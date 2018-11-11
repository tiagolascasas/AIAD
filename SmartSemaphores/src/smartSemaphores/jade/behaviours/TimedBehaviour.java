/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.jade.behaviours;

import sajas.core.behaviours.CyclicBehaviour;
import smartSemaphores.jade.SemaphoreStates;
import smartSemaphores.jade.TimedSemaphoricAgent;
import smartSemaphores.repast.SimulationManager;

/**
 * The Class TimedBehaviour.
 */
public class TimedBehaviour extends CyclicBehaviour {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7448453660507939119L;

    /*
     * (non-Javadoc)
     * 
     * @see sajas.core.behaviours.Behaviour#action()
     */
    @Override
    public void action() {
	TimedSemaphoricAgent agent = (TimedSemaphoricAgent) this.myAgent;
	int sequence = agent.getSequence();
	int currentSequence = SimulationManager.currentActiveSequence;

	if (sequence == currentSequence)
	    agent.switchState(SemaphoreStates.GREEN);
	else
	    agent.switchState(SemaphoreStates.RED);
    }
}
