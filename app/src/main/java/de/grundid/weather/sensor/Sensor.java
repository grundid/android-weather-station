package de.grundid.weather.sensor;

public abstract class Sensor {

	protected final int SENSOR_TEMP = 1;
	protected final int SENSOR_FEUCHTE = 2;
	protected final int SENSOR_LUFTDRUCK = 3;
	protected final int SENSOR_HELLIGKEIT = 4;
	protected byte[] data = new byte[8];

	public Sensor(byte[] data) {
		init(data);
	}

	protected void init(byte[] data) {
		this.data[0] = data[0];
		for (int x = 1; x < 7; x++) {
			this.data[x] = (byte)(data[x] & (byte)0x7F);
		}
		this.data[7] = data[7];
	}

	public byte getStartByte() {
		return data[0];
	}

	public byte getTyp() {
		return data[1];
	}

	public byte getEndByte() {
		return data[7];
	}

	public byte getByte(int x) {
		return data[x];
	}

	public static int calc14Bit(byte b1, byte b2) {
		int data = (int)(((int)b1 << 7) | (int)b2);
		if ((data & 0x2000) == 0x2000) {
			return (data - 0x4000);
		}
		else {
			return data;
		}
	}

	@Override
	public String toString() {
		String buffer = "";
		for (int x = 0; x < 8; x++) {
			buffer += Integer.toHexString(data[x]) + " ";
		}
		return buffer;
	}
};