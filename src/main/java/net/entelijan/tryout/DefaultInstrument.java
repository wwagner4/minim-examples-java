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

		out.setTempo(100);

		out.pauseNotes();
		
		seqB(0, ctx);
		seqB(8, ctx);
		seqB(10, ctx);

		out.resumeNotes();

		waitAndClose(30, ctx);
	}

	private void seqB(double time, Ctx ctx) {
		double base = 170;
		seqA(base, time + 0, ctx);
		seqA(base * 4.0 / 3.0, time + 3, ctx);
		seqA(base * 3.0 / 2.0, time + 4, ctx);
		seqA(base, time + 7, ctx);
	}

	private void seqA(double frq, double time, Ctx ctx) {
		playNote(frq, time + 0.0, ctx);
		playNote(frq, time + 0.5, ctx);
		playNote(frq, time + 6.0, ctx);
		playNote(frq, time + 6.25, ctx);
	}

	private void playNote(double frq, double time, Ctx ctx) {
		ctx.out.playNote(f(time), 3f, f(frq) + ctx.ran.nextFloat() * 5f);
	}

	private float f(double val) {
		return Double.valueOf(val).floatValue();
	}

	private void waitAndClose(int seconds, Ctx ctx) throws InterruptedException {
		Thread.sleep(seconds * 1000);
		ctx.out.close();
		System.out.printf("Closed after %ds%n", seconds);
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
