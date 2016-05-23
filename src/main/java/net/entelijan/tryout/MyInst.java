package net.entelijan.tryout;

import static net.entelijan.tryout.MinimUtil.f;

import java.util.Random;

import ddf.minim.AudioOutput;
import ddf.minim.AudioRecorder;
import ddf.minim.Minim;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.ugens.ADSR;
import ddf.minim.ugens.Damp;
import ddf.minim.ugens.Instrument;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.Waves;

public class MyInst {

	private enum Inst {
		A, B;
	}

	private final String fileName = "myinst_03.wav";
	private final boolean recording = false;

	private static class InstA implements Instrument {

		private AudioOutput out;
		private double freq;

		private Oscil toneOsc;

		private Damp damp;

		public InstA(AudioOutput out, double freq) {
			super();
			this.out = out;
			this.freq = freq;
			toneOsc = new Oscil(f(this.freq), 0.2f, Waves.TRIANGLE);
			damp = new Damp(0.01f, 0.5f, 1.0f);

			toneOsc.patch(damp);
		}

		@Override
		public void noteOn(float duration) {
			damp.patch(out);
			damp.setDampTimeFromDuration(duration);
			damp.activate();
		}

		@Override
		public void noteOff() {
			damp.unpatch(out);
		}

	}

	private static class InstB implements Instrument {

		private AudioOutput out;
		private double freq;

		private Oscil toneOsc;

		private ADSR adsr;

		public InstB(AudioOutput out, double freq) {
			super();
			this.out = out;
			this.freq = freq;
			toneOsc = new Oscil(f(this.freq), 0.2f, Waves.TRIANGLE);
			adsr = new ADSR(2f, 0.01f, 0.7f, 0.1f, 1f);

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

		for (int i = 0; i < 10; i += 4) {
			double baseDur = 0.5;
			double baseDiff = 1.0;
			double speedFac = 0.6;

			double freq = 444;
			double freqFac = 0.8;

			playNote(i + 0, baseDur * r(4, ctx.ran), freq, Inst.A, ctx);
			double diff = baseDiff;
			freq = freq * freqFac;
			playNote(i + diff, baseDur * r(4, ctx.ran), freq, Inst.A, ctx);
			diff = baseDiff + diff * speedFac;
			freq = freq * freqFac;
			playNote(i + diff, baseDur * r(4, ctx.ran), freq, Inst.A, ctx);
			diff = baseDiff + diff * speedFac;
			freq = freq * freqFac;
			playNote(i + diff, baseDur * r(4, ctx.ran), freq, Inst.B, ctx);
			diff = baseDiff + diff * speedFac;
			freq = freq * freqFac;
			playNote(i + diff, baseDur * r(4, ctx.ran), freq, Inst.B, ctx);
			diff = baseDiff + diff * speedFac;
			freq = freq * freqFac;
			playNote(i + diff, baseDur * r(4, ctx.ran), freq, Inst.B, ctx);
		}

		if (recording) {
			ctx.rec.beginRecord();
		}
		ctx.out.resumeNotes();
		waitAndClose(10, ctx);
	}

	private void playNote(double time, double dur, double fbase, Inst inst, Ctx ctx) {
		double f = fbase * r(1.1, ctx.ran);
		Instrument i = new InstA(ctx.out, f);
		switch (inst) {
		case A:
			i = new InstA(ctx.out, f);
			break;
		case B:
			i = new InstB(ctx.out, f);
			break;
		default:
			throw new IllegalStateException("Unknown inst value " + inst);
		}
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
			new MyInst().run();
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
