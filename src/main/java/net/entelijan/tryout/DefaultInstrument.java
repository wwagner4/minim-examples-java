package net.entelijan.tryout;

import java.util.Random;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import net.entelijan.tryout.common.FileLoader;

public class DefaultInstrument {
	public static void main(String[] args) {
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

		Ctx ctx = new Ctx(out, ran);

		out.setTempo(60);

		out.pauseNotes();
		playNote(0.0, ctx);
		playNote(3.0, ctx);
		playNote(4.0, ctx);

		out.resumeNotes();
		System.out.println("Resumed notes");

		waitAndClose(15, ctx);
	}

	private void waitAndClose(int seconds, Ctx ctx) throws InterruptedException {
		Thread.sleep(seconds * 1000);
		ctx.out.close();
		System.out.printf("Closed after %ds%n", seconds);
	}

	private void playNote(Double time, Ctx ctx) {
		float t = time.floatValue();
		ctx.out.playNote(t, 6f, 97.99f + ctx.ran.nextFloat() * 5f);
		ctx.out.playNote(t, 5f, 200f + ctx.ran.nextFloat() * 5f);
		ctx.out.playNote(t, 4f, 200f + ctx.ran.nextFloat() * 5f);
		ctx.out.playNote(t, 3f, 200f + ctx.ran.nextFloat() * 5f);
	}

	private static class Ctx {
		private AudioOutput out;
		private Random ran;

		public Ctx(AudioOutput out, Random ran) {
			super();
			this.out = out;
			this.ran = ran;
		}
	}

}
