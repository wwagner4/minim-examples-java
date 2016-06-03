package net.entelijan.tryout;

import static net.entelijan.util.MinimUtil.*;

import ddf.minim.*;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.ugens.*;
import net.entelijan.util.*;

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

		FileLoaderResources fileLoader = new FileLoaderResources();
		MinimServiceProvider serviceProvider = new JSMinim(fileLoader);
		Minim minim = new Minim(serviceProvider);
		AudioOutput out = minim.getLineOut();

		AudioRecorder rec = null;
		if (recording) {
			rec = minim.createRecorder(out, fileName);
		}

		Ctx ctx = new Ctx(out, rec, minim);

		run(ctx);
	}

	private void run(Ctx ctx) throws InterruptedException {

		ctx.out.setTempo(180);
		ctx.out.pauseNotes();

		playNote(0, 5, 300, ctx);
		playNote(1, 5, 300, ctx);

		if (recording) {
			ctx.rec.beginRecord();
		}
		ctx.out.resumeNotes();
		waitAndClose(10, ctx);
	}

	private void playNote(double time, double dur, double freq, Ctx ctx) {
		Instrument i = new Inst(ctx, freq);
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

		private Sampler sampler;

		public Inst(Ctx ctx, double freq) {
			super();
			this.out = ctx.out;
			
			sampler = new Sampler("h3.wav", 4, ctx.minim);
		}

		@Override
		public void noteOn(float duration) {
			sampler.patch(out);
			sampler.trigger();
		}

		@Override
		public void noteOff() {
			sampler.unpatch(out);
			sampler.stop();
		}

	}

	private static class Ctx {
		private AudioOutput out;
		private AudioRecorder rec;
		private Minim minim;

		public Ctx(AudioOutput out, AudioRecorder rec, Minim minim) {
			super();
			this.out = out;
			this.rec = rec;
			this.minim = minim;
		}
	}

}
