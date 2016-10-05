package net.entelijan.tryout;

import java.util.*;

import ddf.minim.*;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.ugens.*;
import net.entelijan.util.FileLoaderUserHome;

public class PlaySineTryout {
	
	private static Random ran = new java.util.Random();

	public static void main(String[] args) {
		
		Minim minim = createMinim();
		AudioOutput sink = minim.getLineOut();
		
		List<Oscil> oscils = createOscils();
		
		System.out.println("patching");
		for (Oscil oscil : oscils) {
			oscil.patch(sink);
		}
		
		pause(10_000);
		System.out.println("unpatching");
		for (Oscil oscil : oscils) {
			oscil.unpatch(sink);
		}
		
		pause(1_000);
		System.out.println("stopping");
		minim.stop();
		
	}

	private static List<Oscil> createOscils() {
		
		int oscilCnt = 100;
		float freq = 1000f;
		float maxFreq = 2000f;
		double d = Math.pow(maxFreq / freq, 1.0f / oscilCnt);
		float ampl = 1.0f / oscilCnt;
		
		System.out.println("ampl " + ampl);
		
		List<Oscil> re = new ArrayList<>();
		for(int i = 0; i < oscilCnt; i++) {
			float ranOffset = ran.nextFloat() * 2.0f - 1.0f;
//			float ranOffset = 0.0f;
			re.add(createOscil(freq + ranOffset, ampl));
			freq = freq * (float)d;
		}
		return re ;
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
