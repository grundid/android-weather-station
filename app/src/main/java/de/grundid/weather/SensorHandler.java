package de.grundid.weather;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import de.grundid.weather.sensor.InsideSensor;
import de.grundid.weather.sensor.LightSensor;
import de.grundid.weather.sensor.OutsideSensor;
import de.grundid.weather.sensor.Sensor;
import de.grundid.weather.sensor.SensorListener;

public class SensorHandler extends Handler implements SensorListener {

	private final Activity activity;
	private TextView inside;
	private TextView outside;
	private TextView outside2;
	private TextView pressure;
	private TextView humidity;
	private TextView humidity2;
	private TextView insideLastUpdate;
	private TextView outsideLastUpdate;
	private TextView outsideLastUpdate2;
	private TextView pressureLastUpdate;
	private TextView humidityLastUpdate;
	private TextView humidityLastUpdate2;
	private DecimalFormat degreeFormat = new DecimalFormat("0.0'°'");
	private DecimalFormat humidityFormat = new DecimalFormat("0'%'");
	private DecimalFormat pressureFormat = new DecimalFormat("0 hPa");
	private DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

	public SensorHandler(Activity activity, View inside, View outside, View pressure, View humidity, View insideLastUpdate,
			View outsideLastUpdate, View pressureLastUpdate, View humidityLastUpdate) {
		this.activity = activity;
		this.inside = (TextView)inside;
		this.outside = (TextView)outside;
		this.pressure = (TextView)pressure;
		this.humidity = (TextView)humidity;
		this.insideLastUpdate = (TextView)insideLastUpdate;
		this.outsideLastUpdate = (TextView)outsideLastUpdate;
		this.pressureLastUpdate = (TextView)pressureLastUpdate;
		this.humidityLastUpdate = (TextView)humidityLastUpdate;
		this.outside2 = (TextView) activity.findViewById(R.id.outside2);
		this.humidity2 = (TextView) activity.findViewById(R.id.humidity2);
		this.outsideLastUpdate2 = (TextView) activity.findViewById(R.id.outside_last_update2);
		this.humidityLastUpdate2 = (TextView) activity.findViewById(R.id.humidity2_last_update);
	}

	@Override
	public void onSensorData(Sensor data) {
		sendMessage(Message.obtain(this, 1, data));
	}

	@Override
	public void handleMessage(Message msg) {
		if (msg.what == 1) {
			List<SensorData> sensorDataList = new ArrayList<>();
			Date now = new Date();
			Sensor data = (Sensor)msg.obj;
			Log.i(Constants.TAG, "Sensor Type: " + data.getTyp());

			if (data instanceof OutsideSensor) {
				OutsideSensor as = (OutsideSensor)data;
				if (data.getTyp() == 25) {
					outside2.setText(degreeFormat.format(as.getTemperature()));
					outsideLastUpdate2.setText("Typ: " + data.getTyp() + " - " + df.format(now));
					humidity2.setText(humidityFormat.format(as.getHumidity()));
					humidityLastUpdate2.setText("Typ: " + data.getTyp() + " - " + df.format(now));
					sensorDataList.add(new SensorData("cowo.outside.temperature", as.getTemperature(), now.getTime()));
					sensorDataList.add(new SensorData("cowo.outside.humidity", as.getHumidity(), now.getTime()));

				} else if (data.getTyp() == 26) {
					outside.setText(degreeFormat.format(as.getTemperature()));
					outsideLastUpdate.setText("Typ: " + data.getTyp() + " - " + df.format(now));
					sensorDataList.add(new SensorData("cowo.inside1.temperature", as.getTemperature(), now.getTime()));

				}
				Log.i(Constants.TAG, "Outside: " + as.getTemperature() + " |  " +as.getTyp()+" => " + as.toString());
			}
			if (data instanceof LightSensor) {
				LightSensor as = (LightSensor)data;
				Log.i(Constants.TAG, "Light: " + as.getLumen());
				sensorDataList.add(new SensorData("cowo.outside.lumen", as.getLumen(), now.getTime()));
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

				sensorDataList.add(new SensorData("cowo.inside2.temperature", as.getTemperature(), now.getTime()));
				sensorDataList.add(new SensorData("cowo.inside2.pressure", as.getPressure(), now.getTime()));
				sensorDataList.add(new SensorData("cowo.inside2.humidity", as.getHumidity(), now.getTime()));
			}
			if (!sensorDataList.isEmpty()) {
				Log.i("SENSORS","Sending Sensor Data: "+sensorDataList.size());
				Ion.with(activity)
						.load("POST","http://api.grundid.de/sensor").setJsonPojoBody(sensorDataList).asString()
						.setCallback(new FutureCallback<String>() {
							@Override
							public void onCompleted(Exception e, String  result) {
								if (e != null) {
									e.printStackTrace();
								} else {
									Log.i("SENSORS", "Data send ok");
								}
							}
						});

			}
		}
	}
}
