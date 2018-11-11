/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.jade;

import smartSemaphores.jade.behaviours.StateCommunicationBehaviour;

/**
 * The Class ConsensualSemaphoricAgent.
 */
public class ConsensualSemaphoricAgent extends SemaphoricAgent {

    /**
     * Instantiates a new consensual semaphoric agent.
     *
     * @param id
     *            the id
     * @param semaphoricIDs
     *            the semaphoric ID'sS of the semaphores used in the communication
     * @param connectableIDs
     *            the connectable Id's
     * @param capacity
     *            the capacity of the road
     */
    public ConsensualSemaphoricAgent(int id, int[] semaphoricIDs, int[] connectableIDs, int capacity) {
	super(id, semaphoricIDs, connectableIDs, capacity);
    }

    /*
     * (non-Javadoc)
     * 
     * @see smartSemaphores.jade.SemaphoricAgent#setup()
     */
    @Override
    public void setup() {
	super.setup();

	addBehaviour(new StateCommunicationBehaviour());
    }
}
