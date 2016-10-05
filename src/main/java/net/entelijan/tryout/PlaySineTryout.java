package net.entelijan.tryout;

import ddf.minim.*;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.ugens.*;
import net.entelijan.util.FileLoaderUserHome;

public class PlaySineTryout {

	public static void main(String[] args) {
		
		Minim minim = createMinim();
		
		Oscil src = new Oscil(500f, 0.1f, Waves.SINE);		
		AudioOutput sink = minim.getLineOut();
		System.out.println("patching");
		src.patch(sink);

		pause(1000);
		System.out.println("unpatching");
		src.unpatch(sink);
		
		pause(1000);
		System.out.println("stopping");
		minim.stop();
		
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
