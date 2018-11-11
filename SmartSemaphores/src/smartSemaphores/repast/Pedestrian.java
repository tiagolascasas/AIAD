/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.repast;

/**
 * The Class Pedestrian.
 */
public class Pedestrian {

    /** The start tick. */
    private int startTick;

    /** The semaphore. */
    private int semaphore;

    /** The end tick. */
    private int endTick;

    /**
     * Instantiates a new pedestrian.
     *
     * @param semaphore
     *            the semaphore
     * @param startTick
     *            the start tick
     */
    public Pedestrian(int semaphore, int startTick) {
	this.semaphore = semaphore;
	this.startTick = startTick;
    }

    /**
     * Gets the end tick.
     *
     * @return the end tick
     */
    public int getEndTick() {
	return endTick;
    }

    /**
     * Sets the end tick.
     *
     * @param endTick
     *            the new end tick
     */
    public void setEndTick(int endTick) {
	this.endTick = endTick;
    }

    /**
     * Gets the semaphore.
     *
     * @return the semaphore
     */
    public int getSemaphore() {
	return semaphore;
    }

    /**
     * Sets the semaphore.
     *
     * @param semaphore
     *            the new semaphore
     */
    public void setSemaphore(int semaphore) {
	this.semaphore = semaphore;
    }

    /**
     * Gets the start tick.
     *
     * @return the start tick
     */
    public int getStartTick() {
	return startTick;
    }

    /**
     * Sets the start tick.
     *
     * @param startTick
     *            the new start tick
     */
    public void setStartTick(int startTick) {
	this.startTick = startTick;
    }

    /**
     * Gets the elapsed time.
     *
     * @return the elapsed time
     */
    public int getElapsedTime() {
	return this.endTick - this.startTick;
    }
}
