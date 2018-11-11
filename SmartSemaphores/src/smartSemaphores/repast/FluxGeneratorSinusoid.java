/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.repast;

import repast.simphony.random.RandomHelper;
import smartSemaphores.SmartSemaphores;

/**
 * The Class FluxGeneratorSinusoid.
 */
public class FluxGeneratorSinusoid extends FluxGenerator {

    /** The amplitude. */
    private double amplitude;

    /** The displacement. */
    private double displacement;

    /** The period. */
    private double period;

    /**
     * Instantiates a new flux generator sinusoid.
     *
     * @param seed
     *            the seed
     */
    public FluxGeneratorSinusoid(long seed) {
	super(seed);

	this.amplitude = calculateAmplitude();
	this.period = calculatePeriod(SmartSemaphores.MAX_TICKS);
	this.displacement = calculateDisplacement();
    }

    /**
     * Calculate displacement.
     *
     * @return the double
     */
    private double calculateDisplacement() {
	return RandomHelper.nextDoubleFromTo(-1.0f, 1.0f);
    }

    /**
     * Calculate amplitude.
     *
     * @return the double
     */
    private double calculateAmplitude() {
	double amp = RandomHelper.nextDoubleFromTo(-3.5f, 3.5f);
	if (amp > -1.0f && amp < 0.0f)
	    amp = -1.0f;
	if (amp < 1.0f && amp > 0.0f)
	    amp = 1.0f;
	return amp;
    }

    /**
     * Calculate period.
     *
     * @param maxX
     *            the max X
     * @return the double
     */
    private double calculatePeriod(int maxX) {
	double m = -8.333e-9f;
	double b = 4.5e-4f;

	return m * maxX + b;
    }

    /*
     * (non-Javadoc)
     * 
     * @see smartSemaphores.repast.FluxGenerator#calculateY(int)
     */
    @Override
    public int calculateY(int x) {
	double arg = this.displacement + x * this.period;
	return (int) (this.amplitude * Math.sin(arg) + 3.5f);
    }
}
