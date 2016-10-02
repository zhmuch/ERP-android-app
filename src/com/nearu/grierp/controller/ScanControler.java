package com.nearu.grierp.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.gpio.GpioJNI;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.nearu.grierp.comm.BeepManager;
import com.nearu.grierp.serialport.AbsSerialPort;

public class ScanControler extends AbsSerialPort {

	private boolean isPlaySound = false;

	public AudioManager manager;
	public boolean isPlaySound() {
		return isPlaySound;
	}

	public void setPlaySound(boolean isPlaySound) {
		this.isPlaySound = isPlaySound;
	}
	Context context;

	BeepManager beepManager;
	
	public ScanControler(Context context) {
		super("/dev/ttySAC1", 9600,8,'N',1);
		this.context = context;
	}
	
	public void initBM(Activity activity)
	{
		beepManager = new BeepManager(activity);
	}

	public void doScan() {
		checkIsSleep();
		gongzuo();
	}
	public void gongzuo()
	{
		GpioJNI.gpio_switch_scan_trig(1);		
	}
	
	public void kaidian()
	{
		try {
			GpioJNI.gpio_switch_scan_rf_ired(0);
			GpioJNI.gpio_switch_scan_power(1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
		
	public void xiadian()
	{
		GpioJNI.gpio_switch_scan_power(0);
		super.destroy();
	}
	
	public void cxhuanxing()
	{
		xiuxi();
		gongzuo();
	}
	
	public void xiuxi()
	{
		GpioJNI.gpio_switch_scan_trig(0);
	}
	
	private void sleep(int mm) {
		try {
			Thread.currentThread();
			Thread.sleep(mm);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static File power_en = new File(
			"/sys/class/jiebao/scanner/power_en/value");
	private static File trigger = new File(
			"/sys/class/jiebao/scanner/trigger/value");

	private void writePowerFile(String value) {
		writeFile(power_en, value);
	}

	private void writeTriggerFile(String value) {
		writeFile(trigger, value);
	}

	private void writeFile(File file, String value) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(value);
			writer.flush();
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	void checkIsSleep()
	{
		int triggerVal = GpioJNI.gpio_get_scan_trig();
		System.err.println("checkIsSleep: " + triggerVal);
		if(triggerVal == 1)
			xiuxi();
	}

	public static String a = "/sys/class/jiebao/scanner/trigger/value";
	public static String b = "/sys/class/jiebao/scanner/power_en/value";
	String c = "";
	
	private void writeFile(String filePath, String value) {
		File tmpFile = new File(filePath);
		FileWriter writer = null;
		try {
			writer = new FileWriter(tmpFile);
			writer.write(value);
			writer.flush();
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return;
	}
	public void trunVON(){
		beepManager.turnOnV();
	}
	public void trunVOFF(){
		beepManager.turnOffV();
	}
	public void turnOFFV(){
		beepManager.release();
	}
	private void playAudio() {
		AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, numSamples,
				AudioTrack.MODE_STATIC);
		audioTrack.write(generatedSnd, 0, numSamples);
		audioTrack.play();
		try {
			Thread.sleep(30); // 30
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		audioTrack.stop();
		audioTrack.release();
	}

	private final int duration = 1; // seconds
	private final int sampleRate = 2000;
	private final int numSamples = duration * sampleRate;
	private final double sample[] = new double[numSamples];
	private final double freqOfTone = 1600; // hz
	private final byte generatedSnd[] = new byte[2 * numSamples];

	private void genTone() {
		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
		}

		int idx = 0;
		for (double dVal : sample) {
			short val = (short) (dVal * 32767);
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
		}
	}

	boolean music = true;
	
	public synchronized void playSound() {
		if (isPlaySound ){
			return;
	}else
		{
			beepManager.playBeepSoundAndVibrate();
		}

	}
}
