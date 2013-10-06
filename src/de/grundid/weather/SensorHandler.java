package de.grundid.weather;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import de.grundid.weather.sensor.InsideSensor;
import de.grundid.weather.sensor.LightSensor;
import de.grundid.weather.sensor.OutsideSensor;
import de.grundid.weather.sensor.Sensor;
import de.grundid.weather.sensor.SensorListener;

public class SensorHandler extends Handler implements SensorListener {

	private TextView inside;
	private TextView outside;
	private TextView pressure;
	private TextView humidity;
	private TextView insideLastUpdate;
	private TextView outsideLastUpdate;
	private TextView pressureLastUpdate;
	private TextView humidityLastUpdate;
	private DecimalFormat degreeFormat = new DecimalFormat("0.0'°'");
	private DecimalFormat humidityFormat = new DecimalFormat("0'%'");
	private DecimalFormat pressureFormat = new DecimalFormat("0 hPa");
	private DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

	public SensorHandler(View inside, View outside, View pressure, View humidity, View insideLastUpdate,
			View outsideLastUpdate, View pressureLastUpdate, View humidityLastUpdate) {
		this.inside = (TextView)inside;
		this.outside = (TextView)outside;
		this.pressure = (TextView)pressure;
		this.humidity = (TextView)humidity;
		this.insideLastUpdate = (TextView)insideLastUpdate;
		this.outsideLastUpdate = (TextView)outsideLastUpdate;
		this.pressureLastUpdate = (TextView)pressureLastUpdate;
		this.humidityLastUpdate = (TextView)humidityLastUpdate;
	}

	@Override
	public void onSensorData(Sensor data) {
		sendMessage(Message.obtain(this, 1, data));
	}

	@Override
	public void handleMessage(Message msg) {
		if (msg.what == 1) {
			Date now = new Date();
			Sensor data = (Sensor)msg.obj;
			if (data instanceof OutsideSensor) {
				OutsideSensor as = (OutsideSensor)data;
				outside.setText(degreeFormat.format(as.getTemperature()));
				outsideLastUpdate.setText(df.format(now));
				Log.i(Constants.TAG, "Outside: " + as.getTemperature() + " " + as.toString());
			}
			if (data instanceof LightSensor) {
				LightSensor as = (LightSensor)data;
				Log.i(Constants.TAG, "Light: " + as.getLumen());
			}
			if (data instanceof InsideSensor) {
				InsideSensor as = (InsideSensor)data;
				Log.i(Constants.TAG,
						"Inside T: " + as.getTemperature() + " H: " + as.getHumidity() + " P: " + as.getPressure()
								+ "|" + as.toString());
				inside.setText(degreeFormat.format(as.getTemperature()));
				pressure.setText(pressureFormat.format(as.getPressure()));
				humidity.setText(humidityFormat.format(as.getHumidity()));
				insideLastUpdate.setText(df.format(now));
				pressureLastUpdate.setText(df.format(now));
				humidityLastUpdate.setText(df.format(now));
			}
		}
	}
}
