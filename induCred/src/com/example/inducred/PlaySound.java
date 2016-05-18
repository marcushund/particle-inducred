package com.example.inducred;

import utilities.Config;
import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


public class PlaySound extends Activity implements TimerCallback {

	private final int duration = 10;// seconds
	private final int sampleRate = 32000;
	private final int numSamples = duration * sampleRate;
	private final double sample[] = new double[numSamples];

	AudioTrack audioTrack;

	private final double freqOfTone = 5500; // hz

	private final byte generatedSnd[] = new byte[2 * numSamples];
	private final byte generatedSilence[] = new byte[2 * numSamples];

	Handler handler = new Handler();
	Timer timer = new Timer(this);
	int ptr = 0;
	int idx = 0;
	int leadIn = sampleRate/8;
	int streep = leadIn / 4;
	int dot = leadIn / 8;
	int bitBreak = dot;
	int charBreak = leadIn /2; 
	private Config config;

	PlaySound(Context context) {
		config = new Config(context);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public void start(String credentials) {
	
		credentials +=  createChecksum(credentials);
		credentials += (char) '|';
		Log.d("credentials",credentials);
		genMorse(credentials);

		final Thread thread = new Thread(new Runnable() {
			public void run() {

				handler.post(new Runnable() {
					public void run() {
						playSound(1);
					}
				});
			}
		});
		thread.start();
	}

	private String createChecksum(String credentials) {
		int chk = 0;
		//int ndx=0;
		byte b=0;
		for (int ii = 0; ii < credentials.length(); ii++) {
			char chr =credentials.charAt(ii);
			 b = (byte) chr;
			chk = (chk+b) & 0xFF;		
			Log.d(String.valueOf(chr), Integer.toString(chk));		
		}
		String result = String.format("%03d",chk);
		Log.d("chk", Integer.toString(chk) + " " + result);
		return result;
	}

	void genTone() {
		// fill out the array
		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
		}

		// convert to 16 bit pcm sound array
		// assumes the sample buffer is normalised.
		int idx = 0;
		for (final double dVal : sample) {
			// scale to maximum amplitude
			final short val = (short) ((dVal * 32767));
			// in 16 bit wav PCM, first byte is the low order byte
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
		}
	}

	void addSamples(double mode, int len) {
		int ii;
		double dVal = 0;

		int ramp = len/50; // Amplitude ramp as a percent of sample count
		
		
		for (ii = ptr; ii < ramp; ++ii) { // Ramp amplitude up (to avoid clicks)
			dVal = sample[ii];
			// Ramp up to maximum

			final short val = (short)((dVal * 32767 *  mode) * ii / ramp);

			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
		}

		for (; ii < len - ramp; ++ii) {
			dVal = sample[ii];
			final short val = (short) ((dVal * 32767 * mode));
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
		}

		for (; ii < len; ++ii) { // Ramp amplitude down
			dVal = sample[ii];
			// Ramp down to zero
			final short val = (short) (dVal * 32767 * (((len - ii) * mode) / ramp));
			// in 16 bit wav PCM, first byte is the low order byte
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
		}

		//ptr = ii;

	}

//	private String morse = new String();

	void addOne() {

		addSamples(1, streep);
		addSamples(0, bitBreak);

	}

	void addZero() {
		addSamples(1, dot);
		addSamples(0, bitBreak);

	}

	void genMorse(String message) {

		ptr = 0;
		idx = 0;
		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
		}
		addSamples(1, leadIn);
		addSamples(0, bitBreak);

		for (int ii = 0; ii < message.length(); ii++) {
			char chr = message.charAt(ii);
			int bt =  chr;

			String bits = String.format("%8s", Integer.toBinaryString(bt).replace(" ", "0"));
			Log.d(String.valueOf(chr), bits);
			for (int jj = 0; jj < bits.length(); jj++) {
			//	morse += bits.charAt(jj);
				if (bits.charAt(jj) == '1')
					addOne();
				else
					addZero();
			}
		//	addSamples(1, charBreak);
			addSamples(0, bitBreak);
		}

	}

	void genSound() {
		// fill out the array
		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
		}

		int idx = 0;
		for (final double dVal : sample) {
			// scale to maximum amplitude
			final short val = (short) ((dVal * 32767));
			// in 16 bit wav PCM, first byte is the low order byte
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
		}
	}

	void playSound(int mode) {
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
				AudioTrack.MODE_STATIC);
		Log.d("pos", "hoi");

		audioTrack.write(generatedSnd, 0, generatedSnd.length);
		audioTrack.play();
	}

	int level = 0;

	@Override
	public void timerReady() {
		// if (level == 0) {
		// audioTrack.setStereoVolume(0, 0);
		// level = 1;
		// } else {
		// float maxv = AudioTrack.getMaxVolume();
		// audioTrack.setStereoVolume(maxv, maxv);
		// level = 0;
		// }
		// audioTrack.setPlaybackHeadPosition(0);
		// timer.set(5);
		// int pos = audioTrack.getPlaybackHeadPosition();
		// Log.d("timer", Integer.toString(pos));

	}

//	public String getMorse() {
//
//		return morse;
//	}

	public void abort() {

		try {
			if (audioTrack == null)
				return;
			audioTrack.pause();
			audioTrack.flush();
			audioTrack.release();
		} catch (IllegalStateException e) {

		}
	}

	public void savePassword(String password) {
		config.store("password", password);
	}

	public String getPassword() {
		return config.get("password", "password");
	}

}