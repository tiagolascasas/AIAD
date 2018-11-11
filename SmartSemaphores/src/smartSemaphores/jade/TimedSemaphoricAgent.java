/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.jade;

import smartSemaphores.jade.behaviours.TimedBehaviour;

/**
 * The Class TimedSemaphoricAgent.
 */
public class TimedSemaphoricAgent extends SemaphoricAgent {

    /** The sequence. */
    private int sequence;

    /**
     * Instantiates a new timed semaphoric agent.
     *
     * @param id
     *            the id
     * @param semaphoricIDs
     *            the semaphoric I ds
     * @param connectableIDs
     *            the connectable I ds
     * @param capacity
     *            the capacity
     * @param sequence
     *            the sequence
     */
    public TimedSemaphoricAgent(int id, int[] semaphoricIDs, int[] connectableIDs, int capacity, int sequence) {
	super(id, semaphoricIDs, connectableIDs, capacity);

	this.sequence = sequence;
    }

    /*
     * (non-Javadoc)
     * 
     * @see smartSemaphores.jade.SemaphoricAgent#setup()
     */
    @Override
    protected void setup() {
	super.setup();

	this.addBehaviour(new TimedBehaviour());
    }

    /**
     * Gets the sequence.
     *
     * @return the sequence
     */
    public int getSequence() {
	return sequence;
    }

    /**
     * Sets the sequence.
     *
     * @param sequence
     *            the new sequence
     */
    public void setSequence(int sequence) {
	this.sequence = sequence;
    }

}
