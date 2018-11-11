package smartSemaphores.jade;

import smartSemaphores.jade.behaviours.HandleRequestsBehaviour;
import smartSemaphores.jade.behaviours.RequestPerformerBehaviour;

public class SmartSemaphoricAgent extends SemaphoricAgent
{
	public SmartSemaphoricAgent(int id, int[] semaphoricIDs, int[] connectableIDs, int capacity)
	{
		super(id, semaphoricIDs, connectableIDs, capacity);
	}
	
	@Override
	public void setup()
	{
		super.setup();
		
		addBehaviour(new HandleRequestsBehaviour());
		addBehaviour(new RequestPerformerBehaviour());
	}
}
