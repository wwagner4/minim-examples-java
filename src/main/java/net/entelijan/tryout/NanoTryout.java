package net.entelijan.tryout;

public class NanoTryout {

	public static void main(String[] args) {
		for(int i=0; i< 10; i++) {
			long t1 = System.nanoTime();
			pauseOneSecond();
			long t2 = System.nanoTime();
			System.out.println(t1);
			System.out.println(t2);
			System.out.println(t2 - t1);
		}

	}

	private static void pauseOneSecond() {
		try {
			Thread.sleep(1_000);
		} catch (InterruptedException e) {
			// nothing to do
		}
		
	}

	
}
