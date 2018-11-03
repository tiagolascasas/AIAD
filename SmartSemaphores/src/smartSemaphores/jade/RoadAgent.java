package smartSemaphores.jade;

import sajas.core.Agent;

public abstract class RoadAgent extends Agent
{
	int currentCars = 0;
	
	public abstract int addCars(int increment);
	
	public int getCurrentCars()
	{
		return currentCars;
	}

	public void setCurrentCars(int currentCars)
	{
		this.currentCars = currentCars;
	}
}
