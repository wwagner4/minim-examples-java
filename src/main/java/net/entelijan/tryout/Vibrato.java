package net.entelijan.tryout;

import static net.entelijan.util.MinimUtil.*;

import java.util.Random;

import ddf.minim.*;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.ugens.*;
import net.entelijan.util.FileLoaderUserHome;

public class Vibrato {

	private final String fileName = "vibrato_00.wav";
	private final boolean recording = false;
	
	public static void main(String[] args) {
		try {
			new Vibrato().run();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void run() throws InterruptedException {
		Random ran = new Random();

		FileLoaderUserHome fileLoader = new FileLoaderUserHome();
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

		ctx.out.setTempo(50);
		ctx.out.pauseNotes();

		double freq = 150;
		for (int i = 0; i < 6; i += 1) {
			double baseDur = 1;
			playNote(i * 0.7 + 0, baseDur * r(4, ctx.ran), freq, ctx);
			freq = freq * 1.2 * r(1.5, ctx.ran);
		}

		if (recording) {
			ctx.rec.beginRecord();
		}
		ctx.out.resumeNotes();
		waitAndClose(10, ctx);
	}

	private void playNote(double time, double dur, double freq, Ctx ctx) {
		Instrument i = new InstA(ctx.out, f(freq));
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

	private static class InstA implements Instrument {

		private AudioOutput out;

		private Oscil toneOsc;
		private Oscil lfo;

		private ADSR adsr;
		private Constant cons;

		public InstA(AudioOutput out, double freq) {
			super();
			this.out = out;
			
			cons = new Constant(f(freq));
			lfo = new Oscil(6, 6f, Waves.SINE);
			toneOsc = new Oscil(0, 0.2f, Waves.TRIANGLE);
			adsr = new ADSR(5f, 0.01f, 0.1f, 0.15f, 1.5f);

			cons.patch(lfo.offset);
			lfo.patch(toneOsc.frequency);
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
