package net.entelijan.tryout;

public class MathTryout {

	public static void main(String[] args) {
		for (float i = 0f; i < 1f; i+=0.1) {
			float y = f(Math.pow(1.1, i * 2.0 - 1.0));
			System.out.printf("%f -> %f%n", i, y);
		}
	}
	
	private static float f(double val) {
		return Double.valueOf(val).floatValue();
	}


}
