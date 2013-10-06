package de.grundid.weather.sensor;

public class OutsideSensor extends Sensor {

	public OutsideSensor(byte[] data) {
		super(data);
	}

	public double getTemperature() {
		return (double)calc14Bit(getByte(2), getByte(3)) / 10;
	}

	public int getTemperatureInt() {
		return calc14Bit(getByte(2), getByte(3));
	}

	public int getHumidity() {
		return getByte(4);
	}
};