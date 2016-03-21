package de.grundid.weather;

import de.grundid.weather.io.SerialReceiver;
import de.grundid.weather.io.SerialReceiverListener;
import de.grundid.weather.sensor.WeatherLogger;

public class SerialDataHandler implements SerialReceiverListener {

	private WeatherLogger weatherLogger;
	private Thread thread;

	public SerialDataHandler(WeatherLogger weatherLogger) {
		this.weatherLogger = weatherLogger;
	}

	@Override
	public void onSerialReceiver(final SerialReceiver serialReceiver) {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				while (!Thread.interrupted()) {
					byte[] data = serialReceiver.receive();
					if (data.length > 0) {
						weatherLogger.readData(data, data.length);
					}
					try {
						Thread.sleep(100);
					}
					catch (InterruptedException e) {
						return;
					}
				}
				serialReceiver.releaseDevice();
			}
		};
		thread = new Thread(runnable);
		thread.start();
	}

	public void releaseDevice() {
		thread.interrupt();
	}
}
