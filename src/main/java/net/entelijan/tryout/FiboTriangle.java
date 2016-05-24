package net.entelijan.tryout;

import static net.entelijan.util.MinimUtil.*;

import java.util.Random;

import ddf.minim.*;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import net.entelijan.util.FileLoader;

public class FiboTriangle {
	
	private final String fileName = "fibo_triangle_00.wav";
	private boolean record = false;
	
	public static void main(String[] args) {
		try {
			new FiboTriangle().run();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void run() throws InterruptedException {
		Random ran = new Random();
		FileLoader fileLoader = new FileLoader();
		MinimServiceProvider serviceProvider = new JSMinim(fileLoader);
		Minim minim = new Minim(serviceProvider);
		System.out.println("Created minim: " + minim);
		AudioOutput out = minim.getLineOut();
		AudioRecorder rec = minim.createRecorder(out, fileName);
		Ctx ctx = new Ctx(out, ran, rec);
		run(ctx);
	}



	private void run(Ctx ctx) throws InterruptedException {
		ctx.out.setTempo(80);
		ctx.out.pauseNotes();
		seqE(ctx);
		if (record) {
			ctx.rec.beginRecord();
		}
		ctx.out.resumeNotes();
		waitAndClose(60, ctx);
	}

	private void seqE(Ctx ctx) {
		double base = 300;
		seqB(base * r(1.8, ctx.ran), 0, ctx);
		seqB(base * r(1.8, ctx.ran), 2 + r(1.1, ctx.ran), ctx);
		seqB(base * r(1.8, ctx.ran), 6 + r(1.1, ctx.ran), ctx);
		seqB(base * r(1.8, ctx.ran), 9 + r(1.1, ctx.ran), ctx);
		seqB(base * r(1.8, ctx.ran), 20 + r(1.1, ctx.ran), ctx);
		seqB(base * r(1.8, ctx.ran), 35 + r(1.1, ctx.ran), ctx);
	}

	private void seqB(double base, double time, Ctx ctx) {
		seqA(base * r(1.1, ctx.ran), time + 0 + r(1.1, ctx.ran), ctx);
		seqD(base * r(1.1, ctx.ran), time + 10 + r(1.1, ctx.ran), ctx);
		seqA(base * r(1.1, ctx.ran), time + 20 + r(1.1, ctx.ran), ctx);
		seqD(base * r(1.1, ctx.ran), time + 30 + r(1.1, ctx.ran), ctx);
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
		f *= 0.9;
		playNote(f, time + 2.1, ctx);
		f *= 0.9;
		playNote(f, time + 3.4, ctx);
	}

	private void playNote(double frq, double time, Ctx ctx) {
		ctx.out.playNote(f(time), 1.1f * r(1.5, ctx.ran), f(frq) * r(1.1, ctx.ran));
	}

	private float r(double val, Random ran) {
		return f(Math.pow(val, ran.nextDouble() * 2.0 - 1.0));
	}

	private void waitAndClose(int seconds, Ctx ctx) throws InterruptedException {
		try {
			Thread.sleep(seconds * 1000);
			if (record) {
				ctx.rec.save();
			}
		} finally {
			ctx.out.close();
			System.out.printf("Closed after %ds%n", seconds);
		}
	}

	private static class Ctx {
		private AudioOutput out;
		private Random ran;
		private AudioRecorder rec;

		public Ctx(AudioOutput out, Random ran, AudioRecorder rec) {
			super();
			this.out = out;
			this.ran = ran;
			this.rec = rec;
		}
	}

}
