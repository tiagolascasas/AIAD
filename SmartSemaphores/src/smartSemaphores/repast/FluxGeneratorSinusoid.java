package smartSemaphores.repast;

import repast.simphony.random.RandomHelper;
import smartSemaphores.SmartSemaphores;

public class FluxGeneratorSinusoid extends FluxGenerator
{
	private double amplitude;
	private double displacement;
	private double period;
	
	public FluxGeneratorSinusoid(long seed)
	{
		super(seed);
		
		this.amplitude = calculateAmplitude();
		this.period = calculatePeriod(SmartSemaphores.MAX_TICKS);
		this.displacement = calculateDisplacement();
	}

	private double calculateDisplacement()
	{
		return RandomHelper.nextDoubleFromTo(-1.0f, 1.0f);
	}

	private double calculateAmplitude()
	{
		double amp = RandomHelper.nextDoubleFromTo(-3.5f, 3.5f);
		if (amp > -1.0f && amp < 0.0f)
			amp = -1.0f;
		if (amp < 1.0f && amp > 0.0f)
			amp = 1.0f;
		return amp;
	}

	private double calculatePeriod(int maxX)
	{
		double m = -8.333e-9f;
		double b = 4.5e-4f;
		
		return m * maxX + b;
	}
	
	@Override
	public int calculateY(int x)
	{
		double arg = this.displacement + x * this.period;
		return (int)(this.amplitude * Math.sin(arg) + 3.5f);
	}
}
