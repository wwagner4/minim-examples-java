package net.entelijan.tryout;

import static net.entelijan.util.MinimUtil.*;

import ddf.minim.*;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.ugens.*;
import net.entelijan.util.*;

public class SamplerTryout {

	private enum EnumSample {
		A, B, C
	};

	private final String fileName = "tickrate_00.wav";
	private final boolean recording = false;

	public static void main(String[] args) {
		try {
			new SamplerTryout().run();
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

		Holder holder = new Holder(minim);

		Ctx ctx = new Ctx(out, rec, holder);

		run(ctx);
	}

	private void run(Ctx ctx) throws InterruptedException {

		ctx.out.setTempo(180);
		ctx.out.pauseNotes();

		playNote(0, EnumSample.A, ctx);
		playNote(1, EnumSample.C, ctx);
		playNote(3, EnumSample.C, ctx);
		playNote(5, EnumSample.B, ctx);
		playNote(6, EnumSample.B, ctx);

		if (recording) {
			ctx.rec.beginRecord();
		}
		ctx.out.resumeNotes();
		waitAndClose(10, ctx);
	}

	private void playNote(double time, EnumSample enumSample, Ctx ctx) {
		Instrument i = new Inst(ctx, enumSample);
		ctx.out.playNote(f(time), 5, i);
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

		public Inst(Ctx ctx, EnumSample enumSample) {
			super();
			this.out = ctx.out;
			switch (enumSample) {
			case A:
				sampler = ctx.holder.sampler1;
				break;
			case B:
				sampler = ctx.holder.sampler2;
				break;
			case C:
				sampler = ctx.holder.sampler3;
				break;
			default:
				throw new IllegalStateException("Unknown value in enum sample: " + enumSample);
			}

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

	private static class Holder {

		private Sampler sampler1;
		private Sampler sampler2;
		private Sampler sampler3;

		public Holder(Minim minim) {
			super();
			sampler1 = new Sampler("h1.wav", 4, minim);
			sampler2 = new Sampler("h2.wav", 4, minim);
			sampler3 = new Sampler("h3.wav", 4, minim);
		}

	}

	private static class Ctx {
		private AudioOutput out;
		private AudioRecorder rec;
		private Holder holder;

		public Ctx(AudioOutput out, AudioRecorder rec, Holder holder) {
			super();
			this.out = out;
			this.rec = rec;
			this.holder = holder;
		}
	}

}
