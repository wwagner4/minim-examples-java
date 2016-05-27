package net.entelijan.tryout.dm;

import static net.entelijan.util.MinimUtil.*;

import ddf.minim.AudioOutput;
import ddf.minim.ugens.*;
import ddf.minim.ugens.MoogFilter.Type;
import ddf.minim.ugens.Noise.Tint;
import net.entelijan.tryout.dm.DrumMachine.Ctx;

class D1 implements Instrument {

	private AudioOutput out;

	private Noise noise;
	private Oscil lfo;
	private MoogFilter moog;

	private ADSR adsr;
	private Constant cons;

	public D1(Ctx ctx, double freq) {
		super();
		this.out = ctx.out;

		cons = new Constant(1.0f);
		lfo = new Oscil(6f, 0.9f, Waves.SQUARE);

		noise = new Noise(Tint.WHITE);
		moog = new MoogFilter(f(freq), 0.9f, Type.BP);
		adsr = new ADSR(1f, 0.002f, 0.2f, 0.03f, 0.2f);

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