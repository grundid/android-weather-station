package de.grundid.weather.sensor;

public class SensorFactory {

	public static Sensor createSensor(byte[] data) {
		int type = (byte)(data[1] & (byte)0x7F);
		if (type >= 0x40 && type <= 0x4f) {
			return new InsideSensor(data);
		}
		else if (type >= 0x58 && type <= 0x5f) {
			return new LightSensor(data);
		}
		else if (type >= 0x00 && type <= 0x1f) {
			return new OutsideSensor(data);
		}
		throw new RuntimeException("Unknown sensor ID: " + type);
	}
}
