package net.entelijan.tryout;

import static net.entelijan.tryout.MinimUtil.f;

import java.util.Random;

import ddf.minim.AudioOutput;
import ddf.minim.AudioRecorder;
import ddf.minim.Minim;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.ugens.Damp;
import ddf.minim.ugens.Instrument;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.Waves;

public class MyInst {

	private final String fileName = "myinst_01.wav";
	private final boolean recording = true;

	private static class Inst implements Instrument {

		private AudioOutput out;
		private double freq;

		private Oscil toneOsc;

		private Damp damp;

		public Inst(AudioOutput out, double freq) {
			super();
			this.out = out;
			this.freq = freq;
			toneOsc = new Oscil(f(this.freq), 0.2f, Waves.TRIANGLE);
			damp = new Damp(0.1f, 2f);

			toneOsc.patch(damp);
		}

		@Override
		public void noteOn(float duration) {
			damp.patch(out);
			damp.activate();
		}

		@Override
		public void noteOff() {
			damp.unpatch(out);
		}

	}

	private void run(Ctx ctx) throws InterruptedException {

		ctx.out.setTempo(80);
		ctx.out.pauseNotes();

		for (int i = 0; i < 20; i += 4) {
			playNote(i + 0, 400, ctx);
			playNote(i + 1, 400, ctx);
			playNote(i + 1.1, 300, ctx);
			playNote(i + 1.5, 200, ctx);
			playNote(i + 2, 400, ctx);
			playNote(i + 2.1, 300, ctx);
			playNote(i + 2.5, 200, ctx);
			playNote(i + 3, 400, ctx);
		}

		if (recording) {
			ctx.rec.beginRecord();
		}
		ctx.out.resumeNotes();
		waitAndClose(20, ctx);
	}

	private void playNote(double time, double fbase, Ctx ctx) {
		double f = fbase * r(1.1, ctx.ran);
		Instrument i = new Inst(ctx.out, f);
		ctx.out.playNote(f(time), 4, i);
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
