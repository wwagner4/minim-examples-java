package net.entelijan.tryout;

import static net.entelijan.util.MinimUtil.*;

import ddf.minim.*;
import ddf.minim.effects.*;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.ugens.*;
import ddf.minim.ugens.Noise.Tint;
import net.entelijan.util.FileLoader;

public class IIRFilterImpl {

	private final String fileName = "iirfilter_00.wav";
	private final boolean recording = false;

	public static void main(String[] args) {
		try {
			new IIRFilterImpl().run();
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

		ctx.out.setTempo(50);
		ctx.out.pauseNotes();

		double baseFreq = 400;
		double dur = 0.7;
		int time = 0;
		{
			double freq = baseFreq;
			for (int i = 0; i < 4; i += 1) {
				Instrument inst = new InstPlain(ctx, f(freq));
				 playNote(time++, dur, inst, ctx);
				freq = freq * 1.2;
			}
		}
		{
			double freq = baseFreq;
			for (int i = 0; i < 4; i += 1) {
				Instrument inst = new InstNotch(ctx, f(freq));
				 playNote(time++, dur, inst, ctx);
				freq = freq * 1.2;
			}
		}
		{
			double freq = baseFreq;
			for (int i = 0; i < 4; i += 1) {
				Instrument inst = new InstBpNoise(ctx, f(freq));
				playNote(time++, dur, inst, ctx);
				freq = freq * 1.2;
			}
		}
		{
			double freq = baseFreq;
			for (int i = 0; i < 4; i += 1) {
				Instrument inst = new InstCheb(ctx, f(freq));
				playNote(time++, dur, inst, ctx);
				freq = freq * 1.2;
			}
		}

		if (recording) {
			ctx.rec.beginRecord();
		}
		ctx.out.resumeNotes();
		waitAndClose(20, ctx);
	}

	private void playNote(double time, double dur, Instrument i, Ctx ctx) {
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

	private class InstPlain implements Instrument {

		private AudioOutput out;

		private Oscil toneOsc;

		private ADSR adsr;

		public InstPlain(Ctx ctx, double freq) {
			super();
			this.out = ctx.out;

			toneOsc = new Oscil(f(freq), 0.1f, Waves.SAW);

			adsr = new ADSR(1f, 0.05f, 0.3f, 0.05f, 0.5f);

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

	private class InstNotch implements Instrument {

		private AudioOutput out;

		private Oscil toneOsc;

		private IIRFilter iir;

		private ADSR adsr;

		public InstNotch(Ctx ctx, double freq) {
			super();
			this.out = ctx.out;

			toneOsc = new Oscil(f(freq), 0.1f, Waves.SAW);
			iir = new NotchFilter(f(freq), 10, out.sampleRate());

			adsr = new ADSR(5f, 0.005f, 0.6f, 0.1f, 0.5f);

			toneOsc.patch(iir).patch(adsr);
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

	private class InstBpNoise implements Instrument {

		private AudioOutput out;

		private Noise noise;

		private IIRFilter iir;

		private ADSR adsr;

		public InstBpNoise(Ctx ctx, double freq) {
			super();
			this.out = ctx.out;

			noise = new Noise(5, Tint.WHITE);
			iir = new BandPass(f(freq), 1, out.sampleRate());

			adsr = new ADSR(15f, 0.05f, 0.6f, 0f, 0.5f);

			noise.patch(iir).patch(adsr);
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

	private class InstCheb implements Instrument {

		private AudioOutput out;

		private Noise noise;

		private IIRFilter lp;
		private IIRFilter hp;

		private ADSR adsr;

		public InstCheb(Ctx ctx, double freq) {
			super();
			this.out = ctx.out;

			noise = new Noise(5, Tint.RED);
			hp = new ChebFilter(f(freq), ChebFilter.HP, 0.5f, 5, out.sampleRate());
			lp = new ChebFilter(f(freq), ChebFilter.LP, 0.5f, 5, out.sampleRate());

			adsr = new ADSR(5f, 0.5f, 0.6f, 0.0f, 0.1f);

			noise.patch(hp).patch(lp).patch(adsr);
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
