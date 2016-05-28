package net.entelijan.inst;

import static net.entelijan.util.MinimUtil.*;

import ddf.minim.*;
import ddf.minim.effects.BandPass;
import ddf.minim.ugens.*;

public class Flute implements Instrument {

	private AudioOutput out;

	private Oscil toneOsc;
	
	private BandPass bp;

	private ADSR adsr;

	public Flute(Ctx ctx, double freq) {
		super();
		this.out = ctx.out;
					
		toneOsc = new Oscil(f(freq), 0.1f, Waves.SQUARE);
		bp = new BandPass(600, 100, out.sampleRate());
		
		adsr = new ADSR(14f, 0.05f, 0.3f, 0.05f, 0.5f);

		toneOsc.patch(bp).patch(adsr);
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

