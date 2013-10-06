package de.grundid.weather.sensor;

public class LightSensor extends Sensor {

	private int[] mult = { 1, 10, 100, 1000 };

	public LightSensor(byte[] data) {
		super(data);
	}

	public int getLumen() {
		return calc14Bit(getByte(2), getByte(3)) * mult[getByte(4)];
	}
};