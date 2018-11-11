/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.repast;

import java.util.Random;

/**
 * The Class FluxGenerator.
 */
public abstract class FluxGenerator {

    /** The Constant MAX_Y. */
    protected final static int MAX_Y = 7;

    /** The random generator. */
    protected Random generator;

    /**
     * Instantiates a new flux generator.
     *
     * @param seed
     *            the seed
     */
    public FluxGenerator(long seed) {
	this.generator = new Random(seed);
    }

    /**
     * Calculate Y.
     *
     * @param x
     *            the x
     * @return the int
     */
    public abstract int calculateY(int x);
}
