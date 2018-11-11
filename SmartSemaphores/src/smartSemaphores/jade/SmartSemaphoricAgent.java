/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.jade;

import smartSemaphores.jade.behaviours.HandleRequestsBehaviour;
import smartSemaphores.jade.behaviours.RequestPerformerBehaviour;

/**
 * The Class SmartSemaphoricAgent.
 */
public class SmartSemaphoricAgent extends SemaphoricAgent {

    /**
     * Instantiates a new smart semaphoric agent.
     *
     * @param id
     *            the id
     * @param semaphoricIDs
     *            the semaphoric Id's
     * @param connectableIDs
     *            the connectable Id's
     * @param capacity
     *            the capacity
     */
    public SmartSemaphoricAgent(int id, int[] semaphoricIDs, int[] connectableIDs, int capacity) {
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
	addBehaviour(new RequestPerformerBehaviour());
	addBehaviour(new HandleRequestsBehaviour());
    }
}
