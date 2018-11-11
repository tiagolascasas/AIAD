/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.jade.behaviours;

import smartSemaphores.jade.SemaphoreStates;
import smartSemaphores.jade.SemaphoricAgent;

/**
 * The Class PriorityCalculator.
 */
public final class PriorityCalculator {

    /** The Constant EMERGENCY_PRIORITY. */
    public static final double EMERGENCY_PRIORITY = 12.0;

    /** The Constant ROAD_OVERFLOW_PRIORITY. */
    private static final double ROAD_OVERFLOW_PRIORITY = 11.0;

    /** The Constant GREEN_MAX_TIME_PRIORITY. */
    private static final double GREEN_MAX_TIME_PRIORITY = 0.0;

    /** The Constant GREEN_MIN_TIME_PRIORITY. */
    private static final double GREEN_MIN_TIME_PRIORITY = 10.0;

    /** The Constant RED_MAX_TIME_PRIORITY. */
    private static final double RED_MAX_TIME_PRIORITY = 10.0;

    /** The Constant RED_MAX_TIME. */
    private static final int RED_MAX_TIME = 180;

    /** The Constant GREEN_MAX_TIME. */
    private static final int GREEN_MAX_TIME = 120;

    /** The Constant GREEN_MIN_TIME. */
    private static final int GREEN_MIN_TIME = 15;

    /**
     * Calculate priority.
     *
     * @param thisAgent
     *            the this agent
     * @return the double
     */
    public static double calculatePriority(SemaphoricAgent thisAgent) {
	if (thisAgent.hasEmergencyVehicles())
	    return EMERGENCY_PRIORITY;

	if (thisAgent.carRoadRatio() >= 1.0)
	    return ROAD_OVERFLOW_PRIORITY;

	if (thisAgent.getCurrentState() == SemaphoreStates.GREEN)
	    return calculatePriorityGreen(thisAgent);
	else if (thisAgent.getCurrentState() == SemaphoreStates.RED)
	    return calculatePriorityRed(thisAgent);

	return -1.0;
    }

    /**
     * Calculate priority green.
     *
     * @param thisAgent
     *            the this agent
     * @return the double
     */
    private static double calculatePriorityGreen(SemaphoricAgent thisAgent) {
	if (thisAgent.getSecondsPassedOnState() >= GREEN_MAX_TIME)
	    return GREEN_MAX_TIME_PRIORITY;
	else if (thisAgent.getSecondsPassedOnState() >= GREEN_MIN_TIME)
	    return GREEN_MIN_TIME_PRIORITY;
	else
	    return ((-0.0001 * Math.pow(thisAgent.getSecondsPassedOnState(), 2))
		    - (0.0389 * thisAgent.getSecondsPassedOnState()) + 6.2782)
		    + calculateCarAndPedestrianPriority(thisAgent);
    }

    /**
     * Calculate priority red.
     *
     * @param thisAgent
     *            the this agent
     * @return the double
     */
    private static double calculatePriorityRed(SemaphoricAgent thisAgent) {
	if (thisAgent.getSecondsPassedOnState() >= RED_MAX_TIME)
	    return RED_MAX_TIME_PRIORITY;

	return ((0.0002 * Math.pow(thisAgent.getSecondsPassedOnState(), 2))
		+ (0.0059 * thisAgent.getSecondsPassedOnState()) - 0.1051)
		+ calculateCarAndPedestrianPriority(thisAgent);
    }

    /**
     * Calculate car and pedestrian priority.
     *
     * @param thisAgent
     *            the this agent
     * @return the double
     */
    private static double calculateCarAndPedestrianPriority(SemaphoricAgent thisAgent) {
	int pedestrians = thisAgent.getPedestriansNumber();
	return (thisAgent.carRoadRatio() * 5.5)
		+ ((-0.0012 * Math.pow(pedestrians, 2)) - (0.0266 * pedestrians) + 0.0126);
    }
}