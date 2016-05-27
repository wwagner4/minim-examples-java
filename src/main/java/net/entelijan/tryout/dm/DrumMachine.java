package net.entelijan.tryout.dm;

import static net.entelijan.util.MinimUtil.*;

import java.util.Random;

import ddf.minim.*;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.ugens.*;
import net.entelijan.util.FileLoader;

public class DrumMachine {

	private final String fileName = "dm_05.wav";
	private final boolean recording = false;

	public static void main(String[] args) {
		try {
			new DrumMachine().run();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void run() throws InterruptedException {
		Random ran = new Random();

		FileLoader fileLoader = new FileLoader();
		MinimServiceProvider serviceProvider = new JSMinim(fileLoader);
		Minim minim = new Minim(serviceProvider);
		AudioOutput out = minim.getLineOut();

		AudioRecorder rec = null;
		if (recording) {
			rec = minim.createRecorder(out, fileName);
		}

		Ctx ctx = new Ctx(out, ran, rec);

		run(ctx);
	}

	private void run(Ctx ctx) throws InterruptedException {

		ctx.out.pauseNotes();

		ctx.out.setTempo(220);
		int t = 0;
		play(t++, 1, 0, 0, ctx);
		play(t++, 0, 0, 0, ctx);
		play(t++, 0, 0, 0, ctx);
		play(t++, 0, 0, 0, ctx);
		play(t++, 1, 0, 0, ctx);
		play(t++, 0, 0, 0, ctx);
		play(t++, 0, 0, 0, ctx);
		play(t++, 0, 0, 0, ctx);
		play(t++, 1, 0, 0, ctx);
		play(t++, 0, 0, 0, ctx);
		play(t++, 1, 0, 0, ctx);
		play(t++, 0, 0, 0, ctx);
		play(t++, 1, 0, 0, ctx);
		play(t++, 0, 0, 0, ctx);
		play(t++, 1, 0, 0, ctx);
		play(t++, 0, 0, 0, ctx);
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 2; i++) {
				play(t++, 1, 0, 1, ctx);
				play(t++, 0, 0, 0, ctx);
				play(t++, 1, 0, 1, ctx);
				play(t++, 0, 0, 0, ctx);
				play(t++, 1, 0, 1, ctx);
				play(t++, 0, 0, 1, ctx);
				play(t++, 1, 0, 1, ctx);
				play(t++, 1, 1, 0, ctx);
			}
			for (int i = 0; i < 1; i++) {
				play(t++, 1, 0, 1, ctx);
				play(t++, 0, 0, 0, ctx);
				play(t++, 1, 0, 1, ctx);
				play(t++, 0, 1, 0, ctx);
				play(t++, 0, 0, 1, ctx);
				play(t++, 0, 0, 1, ctx);
				play(t++, 0, 0, 1, ctx);
				play(t++, 0, 1, 1, ctx);
			}
		}
		play(t++, 0, 0, 1, ctx);
		play(t++, 0, 0, 0, ctx);
		play(t++, 0, 0, 1, ctx);
		play(t++, 0, 0, 0, ctx);
		play(t++, 0, 0, 0, ctx);
		play(t++, 0, 0, 0, ctx);
		play(t++, 1, 1, 1, ctx);
		play(t++, 1, 1, 1, ctx);
		
		if (recording) {
			ctx.rec.beginRecord();
		}
		ctx.out.resumeNotes();
		waitAndClose(50, ctx);
	}

	private void play(int time, int bd, int d1, int d2, Ctx ctx) {
		double t = time + r(1.02, ctx.ran);
		if (is(bd)) {
			playBD(t, 0.5, 200, ctx);
		}
		if (is(d1)) {
			playD1(t, 2, 700, ctx);
		}
		if (is(d2)) {
			playD2(t, 0.8, 1200, ctx);
		}
	}

	private boolean is(int zeroOrOne) {
		if (zeroOrOne == 0)
			return false;
		else
			return true;
	}

	private void playD1(double time, double dur, double freq, Ctx ctx) {
		Instrument i = new D1(ctx, freq);
		ctx.out.playNote(f(time), f(dur), i);
	}

	private void playD2(double time, double dur, double freq, Ctx ctx) {
		Instrument i = new D2(ctx, freq);
		ctx.out.playNote(f(time), f(dur), i);
	}

	private void playBD(double time, double dur, double freq, Ctx ctx) {
		Instrument i = new BD(freq, ctx);
		ctx.out.playNote(f(time), f(dur), i);
	}

	private float r(double val, Random ran) {
		return f(Math.pow(val, ran.nextDouble() * 2.0 - 1.0));
	}

	private void waitAndClose(int seconds, Ctx ctx) throws InterruptedException {
		try {
			Thread.sleep(seconds * 1000);
			if (recording) {
				ctx.rec.endRecord();
				ctx.rec.save();
			}
		} finally {
			ctx.out.close();
			System.out.printf("Closed after %ds%n", seconds);
		}
	}

	static class Ctx {
		AudioOutput out;
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
