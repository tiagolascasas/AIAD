package smartSemaphores.repast;

import java.util.Random;

public abstract class FluxGenerator
{
	protected final static int MAX_Y = 7;
	protected Random generator;
	
	public FluxGenerator(long seed)
	{
		this.generator = new Random(seed);
	}

	public abstract int calculateY(int x);
}
