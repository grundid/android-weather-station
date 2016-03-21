package de.grundid.weather.io;

import tw.com.prolific.driver.pl2303.PL2303Driver;
import android.util.Log;
import de.grundid.weather.Constants;
import de.grundid.weather.utils.Utils;

public class SerialReceiver {

	private PL2303Driver driver;

	public SerialReceiver(PL2303Driver driver) {
		this.driver = driver;
	}

	public void send(byte[] data) {
		driver.write(data);
	}

	public byte[] receive() {
		byte[] buffer = new byte[256];
		int lengthReceived = 0;
		do {
			lengthReceived = driver.read(buffer);
			try {
				Thread.sleep(500);
			}
			catch (InterruptedException e) {
				return new byte[0];
			}
		} while (lengthReceived <= 0);
		byte[] result = new byte[lengthReceived];
		System.arraycopy(buffer, 0, result, 0, lengthReceived);
		Log.d(Constants.TAG, "USB-Received: " + Utils.bufferToString(result));
		return result;
	}

	public void releaseDevice() {
		Log.d(Constants.TAG, "releasing usb device");
		driver.end();
	}
}
