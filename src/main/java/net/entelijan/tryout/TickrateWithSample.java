package net.entelijan.tryout;

import static net.entelijan.util.MinimUtil.*;

import ddf.minim.*;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.ugens.*;
import net.entelijan.util.FileLoader;

public class TickrateWithSample {

	private final String fileName = "tickrate_00.wav";
	private final boolean recording = false;

	public static void main(String[] args) {
		try {
			new TickrateWithSample().run();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void run() throws InterruptedException {

		FileLoader fileLoader = new FileLoader();
		MinimServiceProvider serviceProvider = new JSMinim(fileLoader);
		Minim minim = new Minim(serviceProvider);
		AudioOutput out = minim.getLineOut();

		AudioRecorder rec = null;
		if (recording) {
			rec = minim.createRecorder(out, fileName);
		}

		Ctx ctx = new Ctx(out, rec);

		run(ctx);
	}

	private void run(Ctx ctx) throws InterruptedException {

		ctx.out.setTempo(180);
		ctx.out.pauseNotes();

		playNote(0, 1, 300, ctx);

		if (recording) {
			ctx.rec.beginRecord();
		}
		ctx.out.resumeNotes();
		waitAndClose(10, ctx);
	}

	private void playNote(double time, double dur, double freq, Ctx ctx) {
		Instrument i = new Inst(ctx.out, freq);
		ctx.out.playNote(f(time), f(dur), i);
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

	private static class Inst implements Instrument {

		private AudioOutput out;

		private ADSR adsr;

		public Inst(AudioOutput out, double freq) {
			super();
			this.out = out;

			Oscil lfo1 = new Oscil(200f, 200f, Waves.SQUARE);
			Constant cons1 = new Constant(100f);
			cons1.patch(lfo1.offset);

			Oscil lfo0 = new Oscil(0, 50f, Waves.SQUARE);
			lfo1.patch(lfo0.frequency);
			Constant cons0 = new Constant(f(freq));
			cons0.patch(lfo0.offset);

			Oscil toneOsc = new Oscil(0, 0.1f, Waves.SINE);
			lfo0.patch(toneOsc.frequency);

			adsr = new ADSR(1f, 0.001f, 0.4f, 0.05f, 0.5f);
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
		private AudioRecorder rec;

		public Ctx(AudioOutput out, AudioRecorder rec) {
			super();
			this.out = out;
			this.rec = rec;
		}
	}

}
