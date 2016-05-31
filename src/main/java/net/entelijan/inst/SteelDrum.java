package net.entelijan.inst;

import static net.entelijan.util.MinimUtil.*;

import ddf.minim.AudioOutput;
import ddf.minim.ugens.*;

public class SteelDrum implements Instrument {

	private AudioOutput out;

	private ADSR adsr;

	public SteelDrum(AudioOutput out, double freq) {
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
