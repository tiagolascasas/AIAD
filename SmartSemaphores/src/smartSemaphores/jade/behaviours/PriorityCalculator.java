package smartSemaphores.jade.behaviours;

import smartSemaphores.jade.SemaphoreStates;
import smartSemaphores.jade.SemaphoricAgent;

public final class PriorityCalculator
{
	public static final double EMERGENCY_PRIORITY = 12.0;

	private static final double ROAD_OVERFLOW_PRIORITY = 11.0;
	private static final double GREEN_MAX_TIME_PRIORITY = 0.0;
	private static final double GREEN_MIN_TIME_PRIORITY = 10.0;
	private static final double RED_MAX_TIME_PRIORITY = 10.0;

	private static final int RED_MAX_TIME = 180;
	private static final int GREEN_MAX_TIME = 120;
	private static final int GREEN_MIN_TIME = 20;

	public static double calculatePriority(SemaphoricAgent thisAgent)
	{
		if (thisAgent.hasEmergencyVehicles())
			return EMERGENCY_PRIORITY;

		if (thisAgent.carRoadRatio() >= 1.0)
			return ROAD_OVERFLOW_PRIORITY;

		if (thisAgent.getCurrentState() == SemaphoreStates.GREEN)
			return calculatePriorityGreen(thisAgent);
		else if (thisAgent.getCurrentState() == SemaphoreStates.RED)
			return calculatePriorityRed(thisAgent);
		System.out.println("eu NÃO CHEGO AQUI");
		return -1.0; // TODO SE ISTO ACONTECER SOMETHING IS REALLY WRONG.... HOWEVER IT SHOULD
						// PROCEED AS NORMAL?????
	}

	private static double calculatePriorityGreen(SemaphoricAgent thisAgent)
	{
		System.out.println("Entro aqui verde");
		if (thisAgent.getSecondsPassedOnState() >= GREEN_MAX_TIME)
			return GREEN_MAX_TIME_PRIORITY;
		else if (thisAgent.getSecondsPassedOnState() >= GREEN_MIN_TIME)
			return GREEN_MIN_TIME_PRIORITY;
		else
			return ((-0.0002 * Math.pow(thisAgent.getSecondsPassedOnState(), 2))
					- (0.0243 * thisAgent.getSecondsPassedOnState()) + 5.7327) + (thisAgent.carRoadRatio() * 5);

	}

	private static double calculatePriorityRed(SemaphoricAgent thisAgent)
	{
		System.out.println("Entro aqui red");
		if (thisAgent.getSecondsPassedOnState() >= RED_MAX_TIME)
			return RED_MAX_TIME_PRIORITY;

		return ((0.0002 * Math.pow(thisAgent.getSecondsPassedOnState(), 2))
				+ (0.0005 * thisAgent.getSecondsPassedOnState()) - 0.0578) + (thisAgent.carRoadRatio() * 5);
	}
}
