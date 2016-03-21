package de.grundid.weather.io;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;

public class ConnectedUsbDevice {

	private UsbDeviceConnection connection;
	private UsbInterface usbInterface;
	private UsbEndpoint in;
	private UsbEndpoint out;

	public ConnectedUsbDevice(UsbDeviceConnection connection, UsbInterface usbInterface) {
		this.connection = connection;
		this.usbInterface = usbInterface;
		initConnection(connection);
		int endPoints = usbInterface.getEndpointCount();
		int interfaceProtocol = usbInterface.getInterfaceProtocol();
		System.out.println("EndPoints: " + endPoints + " | interfaces: " + interfaceProtocol);
		out = usbInterface.getEndpoint(1);
		in = usbInterface.getEndpoint(2);
		for (int x = 0; x < endPoints; x++) {
			UsbEndpoint endpoint = usbInterface.getEndpoint(x);
			boolean bulk = endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK;
			boolean crtl = endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_CONTROL;
			boolean inDir = endpoint.getDirection() == UsbConstants.USB_DIR_IN;
			boolean outDir = endpoint.getDirection() == UsbConstants.USB_DIR_OUT;
			System.out.println("ID: " + x + " Bulk: " + bulk + " Ctrl: " + crtl + " Out: " + outDir + " In: " + inDir);
		}
	}

	private void initConnection(UsbDeviceConnection connection) {
		connection.controlTransfer(0x40, 0, 0, 0, null, 0, 0);// reset
		// mConnection.controlTransfer(0×40,
		// 0, 1, 0, null, 0,
		// 0);//clear Rx
		connection.controlTransfer(0x40, 0, 2, 0, null, 0, 0);// clear Tx
		connection.controlTransfer(0x40, 0x02, 0x0000, 0, null, 0, 0);// flow control none
		connection.controlTransfer(0x40, 0x03, 0x4138, 0, null, 0, 0);//baudrate 9600
		//		connection.controlTransfer(0x40, 0x03, 0x0034, 0, null, 0, 0);// baudrate 57600
		//		connection.controlTransfer(0x40, 0x04, 0x0008, 0, null, 0, 0);// data bit 8, parity none, stop bit 1, tx off
	}

	public int send(byte[] buffer, int timeout) {
		return connection.bulkTransfer(out, buffer, buffer.length, timeout);
	}

	public int receive(byte[] buffer, int timeout) {
		return connection.bulkTransfer(in, buffer, buffer.length, timeout);
	}

	public void releaseDevice() {
		connection.releaseInterface(usbInterface);
		connection.close();
	}
}
