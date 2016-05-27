package net.entelijan.tryout.dm;
import static net.entelijan.util.MinimUtil.*;

import ddf.minim.AudioOutput;
import ddf.minim.ugens.*;
import ddf.minim.ugens.MoogFilter.Type;
import ddf.minim.ugens.Noise.Tint;
import net.entelijan.tryout.dm.DrumMachine.Ctx;

class BD implements Instrument {

	private AudioOutput out;

	private Noise noise;
	private Oscil lfo;
	private MoogFilter moog;

	private ADSR adsr;
	private Constant cons;

	public BD(double freq, Ctx ctx) {
		super();
		this.out = ctx.out;

		cons = new Constant(1.0f);
		lfo = new Oscil(10f, 0.6f, Waves.SINE);

		noise = new Noise(Tint.PINK);
		moog = new MoogFilter(f(freq), 0.8f, Type.BP);
		adsr = new ADSR(3f, 0.01f, 0.1f, 0.1f, 0.5f);

		cons.patch(lfo.offset);
		lfo.patch(noise.amplitude);
		noise.patch(moog).patch(adsr);
		;
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