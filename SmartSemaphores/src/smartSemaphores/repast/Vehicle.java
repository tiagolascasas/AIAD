/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author		Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.repast;

/**
 * The Class Vehicle.
 */
public abstract class Vehicle {

    /** The start tick. */
    protected int startTick;

    /** The end tick. */
    protected int endTick;

    /** The origin point. */
    protected int originPoint;

    /** The end point. */
    protected int endPoint;

    /** Boolean exited. */
    protected boolean exited = false;

    /**
     * Instantiates a new vehicle.
     *
     * @param startTick
     *            the start tick
     * @param originPoint
     *            the origin point
     */
    protected Vehicle(int startTick, int originPoint) {
	this.startTick = startTick;
	this.originPoint = originPoint;
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
	this.exited = true;
    }

    /**
     * Gets the elapsed time.
     *
     * @return the elapsed time
     */
    public int getElapsedTime() {
	return this.endTick - this.startTick;
    }

    /**
     * Gets the origin point.
     *
     * @return the origin point
     */
    public int getOriginPoint() {
	return originPoint;
    }

    /**
     * Sets the origin point.
     *
     * @param originPoint
     *            the new origin point
     */
    public void setOriginPoint(int originPoint) {
	this.originPoint = originPoint;
    }

    /**
     * Gets the end point.
     *
     * @return the end point
     */
    public int getEndPoint() {
	return endPoint;
    }

    /**
     * Sets the end point.
     *
     * @param endPoint
     *            the new end point
     */
    public void setEndPoint(int endPoint) {
	this.endPoint = endPoint;
    }

    /**
     * Checks for exited.
     *
     * @return true, if successful
     */
    public boolean hasExited() {
	return this.exited;
    }
}
