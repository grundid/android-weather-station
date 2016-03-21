package de.grundid.weather.sensor;

import de.grundid.weather.utils.Utils;
import android.util.Log;

public class WeatherLogger {

	private final byte PROT_STARTBYTE = 0x02;
	private final byte PROT_ENDBYTE = 0x03;
	private static final int BUFFER_SIZE = 8; // muss 8 sein
	private byte[] message = new byte[BUFFER_SIZE];
	private byte[] completeMsg = new byte[8];
	private int offset = 0;
	private SensorListener sensorListener;

	public WeatherLogger(SensorListener sensorListener) {
		this.sensorListener = sensorListener;
	}

	private void saveData(byte[] message) {
		try {
			Sensor sensor = SensorFactory.createSensor(message);
			sensorListener.onSensorData(sensor);
		}
		catch (Exception e) {
			Log.e("WEATHER", e.getMessage(), e);
		}
	}

	public void readData(byte[] buffer, int readData) {
		try {
			Log.d("WEATHER", "Data read: " + readData + " | " + Utils.bufferToString(buffer, readData));
			for (int x = 0; x < readData; x++) {
				message[offset] = buffer[x];
				if ((message[(offset + 1) % BUFFER_SIZE] == PROT_STARTBYTE) && (message[offset] == PROT_ENDBYTE)) {
					for (int i = 0; i < BUFFER_SIZE; i++) {
						completeMsg[i] = message[(offset + i + 1) % BUFFER_SIZE];
					}
					saveData(completeMsg);
					message[offset] = 0x00;
				}
				offset = ++offset % BUFFER_SIZE;
			}
		}
		catch (Exception e) {
			Log.e("WEATHER", e.getMessage(), e);
		}
	}
};