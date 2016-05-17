package net.entelijan.tryout;

import java.util.Random;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import net.entelijan.tryout.common.FileLoader;

public class DefaultInstrument {

	private void run(Ctx ctx) throws InterruptedException {
		ctx.out.setTempo(50);
		ctx.out.pauseNotes();
		double base = 220;
		for (int i = 0; i < 25; i++) {
			seqB(base * r(1.5, ctx.ran), i * r(1.7, ctx.ran) * 3, ctx);
		}
		ctx.out.resumeNotes();
		waitAndClose(120, ctx);
	}

	private void seqB(double base, double time, Ctx ctx) {
		seqA(base * r(1.1, ctx.ran), time + 0, ctx);
		seqD(base * r(1.1, ctx.ran), time + 5, ctx);
		seqA(base * r(1.1, ctx.ran), time + 20, ctx);
		seqD(base * r(1.1, ctx.ran), time + 25, ctx);
	}

	private void seqA(double frq, double time, Ctx ctx) {
		double f = frq;
		playNote(f, time + 0.2, ctx);
		f *= 1.1;
		playNote(f, time + 0.3, ctx);
		f *= 1.1;
		playNote(f, time + 0.5, ctx);
		f *= 1.1;
		playNote(f, time + 0.8, ctx);
		f *= 1.1;
		playNote(f, time + 1.3, ctx);
	}

	private void seqD(double frq, double time, Ctx ctx) {
		double f = frq;
		playNote(f, time + 0.2, ctx);
		f *= 0.9;
		playNote(f, time + 0.3, ctx);
		f *= 0.9;
		playNote(f, time + 0.5, ctx);
		f *= 0.9;
		playNote(f, time + 0.8, ctx);
		f *= 0.9;
		playNote(f, time + 1.3, ctx);
	}

	private void playNote(double frq, double time, Ctx ctx) {
		ctx.out.playNote(f(time), 0.6f * r(1.5, ctx.ran), f(frq) * r(1.2, ctx.ran));
	}

	private float f(double val) {
		return Double.valueOf(val).floatValue();
	}

	private float r(double val, Random ran) {
		return f(Math.pow(val, ran.nextDouble() * 2.0 - 1.0));
	}

	private void waitAndClose(int seconds, Ctx ctx) throws InterruptedException {
		Thread.sleep(seconds * 1000);
		ctx.out.close();
		System.out.printf("Closed after %ds%n", seconds);
	}

	private void run() throws InterruptedException {
		Random ran = new Random();
		FileLoader fileLoader = new FileLoader();
		MinimServiceProvider serviceProvider = new JSMinim(fileLoader);
		Minim minim = new Minim(serviceProvider);
		System.out.println("Created minim: " + minim);
		AudioOutput out = minim.getLineOut();
		Ctx ctx = new Ctx(out, ran);
		run(ctx);
	}

	public static void main(String[] args) {
		try {
			new DefaultInstrument().run();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
