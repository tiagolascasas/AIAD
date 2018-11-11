package smartSemaphores.repast;

public class Pedestrian
{
	private int startTick;
	private int semaphore;
	private int endTick;

	public Pedestrian(int semaphore, int startTick)
	{
		this.semaphore = semaphore;
		this.startTick = startTick;
	}

	public int getEndTick()
	{
		return endTick;
	}

	public void setEndTick(int endTick)
	{
		this.endTick = endTick;
	}

	public int getSemaphore()
	{
		return semaphore;
	}

	public void setSemaphore(int semaphore)
	{
		this.semaphore = semaphore;
	}

	public int getStartTick()
	{
		return startTick;
	}

	public void setStartTick(int startTick)
	{
		this.startTick = startTick;
	}
	
	public int getElapsedTime()
	{
		return this.endTick - this.startTick;
	}
}
