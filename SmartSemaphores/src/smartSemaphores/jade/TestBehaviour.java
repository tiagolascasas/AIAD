package smartSemaphores.jade;

import repast.simphony.visualization.editedStyle.EditedEdgeStyle2D;
import sajas.core.behaviours.Behaviour;

public class TestBehaviour extends Behaviour
{
	
	public final static int MAX_OFF_TIME=180;
	public final static int MAX_ON_TIME=120;
	public final static int MIN_ON_TIME=20;
	
	private boolean done = false;
	private int counter = 0;
	
	@Override
	public void action()
	{	
		counter++;
		
		if (counter % 5 != 0)
			return;
		
		SemaphoricAgent agent = (SemaphoricAgent)myAgent;
		
		/*PLACEHOLDER till code refactor*/
		if(agent.getCurrentState() == SemaphoreStates.GREEN)
			agent.switchState(SemaphoreStates.RED);
		else 
			agent.switchState(SemaphoreStates.GREEN);
	}

	@Override
	public boolean done()
	{
		return done;
	}

}
