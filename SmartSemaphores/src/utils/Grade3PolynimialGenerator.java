package utils;

import java.util.Random;

public class Grade3PolynimialGenerator {
	private final static int MAX_Y = 7;

	float grade3constant, grade2constant, grade1constant, grade0constant;

	public Grade3PolynimialGenerator(long seed) {

		Random generator = new Random(seed);
		grade3constant = generator.nextFloat() * 10;
		grade2constant = generator.nextFloat() * 10;
		grade1constant = generator.nextFloat() * 10;
		grade0constant = generator.nextFloat() * 10;
	}

	public int calculateY(int x) {
		int y;

		y = (int) (grade3constant * Math.pow(x, 3) + grade2constant * Math.pow(x, 2) + grade1constant * x
				+ grade0constant);
		y = Math.abs(y);
		y = y % MAX_Y;

		return y;
	}
	
	public static void main(String [] args)
	{
		Grade3PolynimialGenerator CarGenerator= new Grade3PolynimialGenerator(System.currentTimeMillis());
		System.out.println(CarGenerator.calculateY(1));
		System.out.println(CarGenerator.calculateY(100));
		System.out.println(CarGenerator.calculateY(47));
		System.out.println(CarGenerator.calculateY(1000));
	}
}
