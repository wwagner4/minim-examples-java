package net.entelijan.tryout;

import java.util.Random;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import net.entelijan.tryout.common.FileLoader;

public class DefaultInstrument {
	public static void main(String[] args)  {
		try {
			new DefaultInstrument().run();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void run() throws InterruptedException {
		Random ran = new Random();

		FileLoader fileLoader = new FileLoader();
		MinimServiceProvider serviceProvider = new JSMinim(fileLoader);
		System.out.println("Created a service provider: " + serviceProvider);
		Minim minim = new Minim(serviceProvider);
		System.out.println("Created minim: " + minim);

		AudioOutput out = minim.getLineOut();
		System.out.println("Created audio output: " + out);

		out.setTempo(60);

		out.pauseNotes();
		playNote(out, ran, 0.0);
		playNote(out, ran, 3.0);
		playNote(out, ran, 4.0);

		out.resumeNotes();
		System.out.println("Resumed notes");

		waitAndClose(15, out);
	}

	private void waitAndClose(int seconds, AudioOutput out) throws InterruptedException {
		Thread.sleep(seconds * 1000);
		out.close();
		System.out.printf("Closed after %ds%n", seconds);
	}

	private void playNote(AudioOutput out, Random ran, Double time) {
		float t = time.floatValue();
		out.playNote(t, 6f, 97.99f + ran.nextFloat() * 5f);
		out.playNote(t, 5f, 200f + ran.nextFloat() * 5f);
		out.playNote(t, 4f, 200f + ran.nextFloat() * 5f);
		out.playNote(t, 3f, 200f + ran.nextFloat() * 5f);
	}

}
