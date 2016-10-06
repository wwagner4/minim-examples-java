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
		ADSR adsr = new ADSR(1.0f, 0.01f, 0.5f, 0.5f, 2.0f); 

		Oscil oscil = createOscil(444f, 0.1f);

		oscil.patch(adsr);
		adsr.patch(sink);
		System.out.println("note on");
		adsr.noteOn();

		pause(2_000);
		System.out.println("note off");
		adsr.noteOff();

		pause(4_000);
		System.out.println("stopping");
//		oscil.unpatch(sink);
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
