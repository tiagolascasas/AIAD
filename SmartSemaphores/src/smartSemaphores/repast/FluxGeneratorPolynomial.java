package smartSemaphores.repast;

public class FluxGeneratorPolynomial extends FluxGenerator
{
	float grade3constant, grade2constant, grade1constant, grade0constant;

	public FluxGeneratorPolynomial(long seed)
	{
		super(seed);
		
		grade3constant = generator.nextFloat() * 10;
		grade2constant = generator.nextFloat() * 10;
		grade1constant = generator.nextFloat() * 10;
		grade0constant = generator.nextFloat() * 10;
	}

	@Override
	public int calculateY(int x)
	{
		int y;

		y = (int) (grade3constant * Math.pow(x, 3) + grade2constant * Math.pow(x, 2) + grade1constant * x
				+ grade0constant);
		y = Math.abs(y);
		y = y % MAX_Y;

		return y;
	}
}
