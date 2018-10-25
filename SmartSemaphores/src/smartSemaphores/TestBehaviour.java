package smartSemaphores;

import repast.simphony.visualization.editedStyle.EditedEdgeStyle2D;
import sajas.core.behaviours.Behaviour;

public class TestBehaviour extends Behaviour
{
	private boolean done = false;
	private int counter = 0;
	
	@Override
	public void action()
	{	
		counter++;
		
		if (counter % 5 != 0)
			return;
		
		SemaphoricAgent agent = (SemaphoricAgent)myAgent;
		agent.switchState(!agent.getCurrentState());
	}

	@Override
	public boolean done()
	{
		return done;
	}

}
