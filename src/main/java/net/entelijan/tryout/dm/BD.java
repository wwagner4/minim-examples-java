package net.entelijan.tryout.dm;
import ddf.minim.AudioOutput;
import ddf.minim.ugens.*;
import ddf.minim.ugens.MoogFilter.Type;
import ddf.minim.ugens.Noise.Tint;
import net.entelijan.tryout.dm.DrumMachine.Ctx;

import static net.entelijan.util.MinimUtil.*;

class BD implements Instrument {

	private AudioOutput out;
	private Oscil oscil;
	private ADSR adsr;
	
	private Noise noise;
	private MoogFilter noiseMoog;
	private MoogFilter oscilMoog;
	private Summer sum;

	public BD(double freq, Ctx ctx) {
		super();
		this.out = ctx.out;

		oscil = new Oscil(f(freq), 0.1f, Waves.TRIANGLE);
		oscilMoog = new MoogFilter(f(freq), 100, Type.HP);


		noise = new Noise(0.05f, Tint.RED);
		noiseMoog = new MoogFilter(f(freq), 700, Type.HP);
		
		sum = new Summer();
		adsr = new ADSR(1f, 0.001f, 0.2f, 0.0f, 0.5f);

		oscil.patch(oscilMoog).patch(sum);
		noise.patch(noiseMoog).patch(sum);
		
		sum.patch(adsr);
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