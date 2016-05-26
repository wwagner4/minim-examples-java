package net.entelijan.tryout;

import static net.entelijan.util.MinimUtil.*;

import java.util.Random;

import ddf.minim.*;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.ugens.*;
import ddf.minim.ugens.MoogFilter.Type;
import ddf.minim.ugens.Noise.Tint;
import net.entelijan.util.FileLoader;

public class DrumMachine {

	private final String fileName = "tremolo_00.wav";
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

		ctx.out.setTempo(100);
		ctx.out.pauseNotes();

		double freq = 150;
		for (int i = 0; i < 6; i += 1) {
			double baseDur = 0.3;
			playNote(i * 0.7 + 0, baseDur, freq, ctx);
			freq = freq * 1.2 * r(1.5, ctx.ran);
		}

		if (recording) {
			ctx.rec.beginRecord();
		}
		ctx.out.resumeNotes();
		waitAndClose(10, ctx);
	}

	private void playNote(double time, double dur, double freq, Ctx ctx) {
		Instrument i = new BD(ctx);
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

	private class BD implements Instrument {

		private AudioOutput out;

		private Noise noise;
		private Oscil lfo;
		private MoogFilter moog;

		private ADSR adsr;
		private Constant cons;

		public BD(Ctx ctx) {
			super();
			this.out = ctx.out;

			cons = new Constant(1.0f);
			lfo = new Oscil(10f, 0.4f, Waves.SINE);

			noise = new Noise(Tint.WHITE);
			moog = new MoogFilter(300f, 0.0f, Type.LP);
			adsr = new ADSR(30f, 0.0001f, 0.2f, 0.05f, 1.0f);

			cons.patch(lfo.offset);
			lfo.patch(noise.amplitude);
			noise.patch(moog).patch(adsr);
			;
		}

		@Override
		public void noteOn(float duration) {
			adsr.noteOn();
			adsr.patch(out);
		}

		@Override
		public void noteOff() {
			adsr.unpatchAfterRelease(out);
			adsr.noteOff();
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
