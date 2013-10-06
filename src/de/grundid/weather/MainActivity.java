package de.grundid.weather;

import android.app.Activity;
import android.os.Bundle;
import de.grundid.weather.io.AttachedDeviceHandler;
import de.grundid.weather.io.UsbConnectionException;
import de.grundid.weather.sensor.WeatherLogger;

public class MainActivity extends Activity {

	private Beeper beeper;
	private SerialDataHandler serialDataHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUi();
		SensorHandler sensorHandler = new SensorHandler(findViewById(R.id.inside), findViewById(R.id.outside),
				findViewById(R.id.pressure), findViewById(R.id.humidity), findViewById(R.id.inside_last_update),
				findViewById(R.id.outside_last_update), findViewById(R.id.pressure_last_update),
				findViewById(R.id.humidity_last_update));
		WeatherLogger weatherLogger = new WeatherLogger(sensorHandler);
		serialDataHandler = new SerialDataHandler(weatherLogger);
		AttachedDeviceHandler attachedDeviceHandler = new AttachedDeviceHandler(this, serialDataHandler);
		try {
			attachedDeviceHandler.handleIntent(getIntent());
		}
		catch (UsbConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	private void initUi() {
		setContentView(R.layout.activity_main);
		beeper = new Beeper(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		serialDataHandler.releaseDevice();
		super.onDestroy();
	}

	public void beepTwice() {
		Thread t = new Thread(beeper);
		t.start();
	}
}
