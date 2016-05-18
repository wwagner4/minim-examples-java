package net.entelijan.tryout;

import java.util.Random;

import ddf.minim.AudioOutput;
import ddf.minim.AudioRecorder;
import ddf.minim.Minim;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.ugens.DefaultInstrument;
import ddf.minim.ugens.Instrument;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.Waves;

public class MyInst {

	private final String fileName = "myinst_01.wav";

	private static class Inst implements Instrument {

		private AudioOutput out;

		private Oscil toneOsc = new Oscil(333f, 0.4f, Waves.TRIANGLE);

		public Inst(AudioOutput out) {
			super();
			this.out = out;
		}

		@Override
		public void noteOn(float duration) {
			toneOsc.patch(out);
		}

		@Override
		public void noteOff() {
			toneOsc.unpatch(out);
		}

	}

	private void run(Ctx ctx) throws InterruptedException {

		ctx.out.setTempo(80);
		ctx.out.pauseNotes();

		float f = 400 * r(1.1, ctx.ran);
		Instrument i = new Inst(ctx.out);

		ctx.out.playNote(0, 4, i);

		ctx.rec.beginRecord();
		ctx.out.resumeNotes();
		waitAndClose(5, ctx);
	}

	private float f(double val) {
		return Double.valueOf(val).floatValue();
	}

	private float r(double val, Random ran) {
		return f(Math.pow(val, ran.nextDouble() * 2.0 - 1.0));
	}

	private void waitAndClose(int seconds, Ctx ctx) throws InterruptedException {
		try {
			Thread.sleep(seconds * 1000);
			ctx.rec.save();
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
		AudioRecorder rec = minim.createRecorder(out, fileName);
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