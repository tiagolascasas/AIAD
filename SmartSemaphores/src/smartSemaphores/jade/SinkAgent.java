package smartSemaphores.jade;

public class SinkAgent extends RoadAgent
{
	public int addCars(int increment)
	{
		currentCars += increment;
		return -1;
	}
	
	public static String makeFullName(int nID)
	{
		return "Sink agent " + nID + "@SmartSemaphores";
	}
}
