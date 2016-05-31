package net.entelijan.inst;

import static net.entelijan.util.MinimUtil.*;

import ddf.minim.AudioOutput;
import ddf.minim.ugens.*;
import ddf.minim.ugens.Noise.Tint;

public class Quark implements Instrument {

	private AudioOutput out;

	private ADSR adsr;
	private ADSR noiseAdsr;

	public Quark(AudioOutput out, double freq) {
		super();
		this.out = out;

		Noise noise = new Noise(1f, Tint.RED);
		Summer sum = new Summer();
		noiseAdsr = new ADSR(0.04f, 0.2f, 0.2f, 0, 0.5f);

		Oscil lfo1 = new Oscil(f(freq / 1), 200f, Waves.SINE);
		Constant cons1 = new Constant(f(freq / 30));
		cons1.patch(lfo1.offset);

		Oscil lfo0 = new Oscil(0, 50f, Waves.SINE);
		lfo1.patch(lfo0.frequency);
		Constant cons0 = new Constant(f(freq));
		cons0.patch(lfo0.offset);

		Oscil toneOsc = new Oscil(0, 0.4f, Waves.TRIANGLE);
		lfo0.patch(toneOsc.frequency);

		adsr = new ADSR(0.6f, 0.02f, 0.15f, 0.1f, 0.5f);
		noise.patch(noiseAdsr).patch(sum);
		toneOsc.patch(sum);
		sum.patch(adsr);
	}

	@Override
	public void noteOn(float duration) {
		adsr.noteOn();
		noiseAdsr.noteOn();
		adsr.patch(out);
	}

	@Override
	public void noteOff() {
		adsr.unpatchAfterRelease(out);
		adsr.noteOff();
		noiseAdsr.noteOff();
	}

}
