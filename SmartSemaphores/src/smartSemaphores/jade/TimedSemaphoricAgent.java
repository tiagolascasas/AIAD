package smartSemaphores.jade;

import smartSemaphores.jade.behaviours.TimedBehaviour;

public class TimedSemaphoricAgent extends SemaphoricAgent
{
	private int sequence;

	public TimedSemaphoricAgent(int id, int[] semaphoricIDs, int[] connectableIDs, int capacity, int sequence)
	{
		super(id, semaphoricIDs, connectableIDs, capacity);
		
		this.sequence = sequence;
	}
	
	@Override
	protected void setup()
	{
		super.setup();
		
		this.addBehaviour(new TimedBehaviour());
	}

	public int getSequence()
	{
		return sequence;
	}

	public void setSequence(int sequence)
	{
		this.sequence = sequence;
	}

}
