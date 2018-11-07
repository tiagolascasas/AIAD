package smartSemaphores.repast;

public abstract class Vehicle
{
	protected int startTick;
	protected int endTick;
	protected int originPoint;
	protected int endPoint;
	protected boolean exited = false;
	
	protected Vehicle(int startTick, int originPoint)
	{
		this.startTick = startTick;
		this.originPoint = originPoint;
	}
	
	public int getStartTick()
	{
		return startTick;
	}
	
	public void setStartTick(int startTick)
	{
		this.startTick = startTick;
	}
	
	public int getEndTick()
	{
		return endTick;
	}
	
	public void setEndTick(int endTick)
	{
		this.endTick = endTick;
		this.exited = true;
	}
	
	public int getElapsedTime()
	{
		return this.endTick - this.startTick;
	}

	public int getOriginPoint()
	{
		return originPoint;
	}

	public void setOriginPoint(int originPoint)
	{
		this.originPoint = originPoint;
	}

	public int getEndPoint()
	{
		return endPoint;
	}

	public void setEndPoint(int endPoint)
	{
		this.endPoint = endPoint;
	}
	
	public boolean hasExited()
	{
		return this.exited;
	}
}
