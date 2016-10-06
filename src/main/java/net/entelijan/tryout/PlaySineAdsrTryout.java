package net.entelijan.tryout;

import ddf.minim.*;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.ugens.*;
import net.entelijan.util.FileLoaderUserHome;

public class PlaySineAdsrTryout {

	public static void main(String[] args) {

		Minim minim = createMinim();
		AudioOutput sink = minim.getLineOut();

		Oscil oscil = createOscil(444f, 0.1f);

		oscil.patch(sink);

		pause(5_000);
		System.out.println("unpatching");
		oscil.unpatch(sink);

		pause(1_000);
		System.out.println("stopping");
		minim.stop();

	}

	private static Oscil createOscil(float freq, float ampl) {
		System.out.println("create oscil " + freq);
		return new Oscil(freq, ampl, Waves.TRIANGLE);
	}

	private static void pause(int timeInMilliseconds) {
		try {
			Thread.sleep(timeInMilliseconds);
		} catch (InterruptedException e) {
			// Nothing to do here
		}
	}

	private static Minim createMinim() {
		FileLoaderUserHome fileLoader = new FileLoaderUserHome();
		MinimServiceProvider serviceProvider = new JSMinim(fileLoader);
		return new Minim(serviceProvider);
	}

}
