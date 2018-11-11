package smartSemaphores.jade;

import smartSemaphores.jade.behaviours.StateCommunicationBehaviour;

public class ConsensualSemaphoricAgent extends SemaphoricAgent
{
	public ConsensualSemaphoricAgent(int id, int[] semaphoricIDs, int[] connectableIDs, int capacity)
	{
		super(id, semaphoricIDs, connectableIDs, capacity);
	}
	
	@Override
	public void setup()
	{
		super.setup();
		
		addBehaviour(new StateCommunicationBehaviour());
	}
}
