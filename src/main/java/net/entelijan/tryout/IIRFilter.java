package net.entelijan.tryout;

import static net.entelijan.util.MinimUtil.*;

import java.util.Random;

import ddf.minim.*;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.ugens.*;
import net.entelijan.util.FileLoader;

public class IIRFilter {

	private final String fileName = "iirfilter_00.wav";
	private final boolean recording = false;
	
	public static void main(String[] args) {
		try {
			new IIRFilter().run();
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

		double freq = 150;
		for (int i = 0; i < 6; i += 1) {
			double baseDur = 1;
			playNote(i * 0.7 + 0, baseDur, freq, ctx);
			freq = freq * 1.2;
		}

		if (recording) {
			ctx.rec.beginRecord();
		}
		ctx.out.resumeNotes();
		waitAndClose(10, ctx);
	}

	private void playNote(double time, double dur, double freq, Ctx ctx) {
		Instrument i = new Inst(ctx, f(freq));
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

	private class Inst implements Instrument {

		private AudioOutput out;

		private Oscil toneOsc;

		private ADSR adsr;

		public Inst(Ctx ctx, double freq) {
			super();
			this.out = ctx.out;
						
			toneOsc = new Oscil(f(freq), 0.1f, Waves.SQUARE);
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
