package de.grundid.weather.sensor;

public class InsideSensor extends Sensor {

	public InsideSensor(byte[] data) {
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

	public int getPressure() {
		return calc14Bit(getByte(5), getByte(6));
	}
};