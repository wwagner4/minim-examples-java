package net.entelijan.tryout;

import static net.entelijan.tryout.MinimUtil.*;

import java.util.Random;

import ddf.minim.*;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.ugens.*;

public class Vibrato {

	private final String fileName = "vibrato_01.wav";
	private final boolean recording = false;

	private static class InstA implements Instrument {

		private AudioOutput out;
		private double freq;

		private Oscil toneOsc;
		private Oscil lfo;

		private ADSR adsr;

		public InstA(AudioOutput out, double freq) {
			super();
			this.out = out;
			this.freq = freq;
			
			lfo = new Oscil(10, 0.5f, Waves.SINE);
			
			toneOsc = new Oscil(f(this.freq), 0.2f, Waves.SINE);
			adsr = new ADSR(1f, 0.001f, 0.5f, 0.3f, 1f);

			lfo.patch(toneOsc.offset);
			
			toneOsc.patch(adsr);
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

	private void run(Ctx ctx) throws InterruptedException {

		ctx.out.setTempo(100);
		ctx.out.pauseNotes();

		for (int i = 0; i < 10; i += 1) {
			double baseDur = 0.5;
			double freq = 180;
			playNote(i + 0, baseDur * r(4, ctx.ran), freq, ctx);
		}

		if (recording) {
			ctx.rec.beginRecord();
		}
		ctx.out.resumeNotes();
		waitAndClose(10, ctx);
	}

	private void playNote(double time, double dur, double fbase, Ctx ctx) {
		double f = fbase * r(1.4, ctx.ran);
		Instrument i = new InstA(ctx.out, f);
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

	private void run() throws InterruptedException {
		Random ran = new Random();
		FileLoader fileLoader = new FileLoader();
		MinimServiceProvider serviceProvider = new JSMinim(fileLoader);
		Minim minim = new Minim(serviceProvider);
		System.out.println("Created minim: " + minim);
		AudioOutput out = minim.getLineOut();

		AudioRecorder rec = null;
		if (recording) {
			rec = minim.createRecorder(out, fileName);
		}
		Ctx ctx = new Ctx(out, ran, rec);
		run(ctx);
	}

	public static void main(String[] args) {
		try {
			new Vibrato().run();
		} catch (InterruptedException e) {
			e.printStackTrace();
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
