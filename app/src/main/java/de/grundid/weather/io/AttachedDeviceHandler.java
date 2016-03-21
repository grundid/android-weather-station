package de.grundid.weather.io;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tw.com.prolific.driver.pl2303.PL2303Driver;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import de.grundid.weather.Constants;

public class AttachedDeviceHandler {

	private static final int VENDOR_ID = 0x067B;
	private static final int PRODUCT_ID = 0x2303;
	private Context context;
	private UsbManager usbManager;
	private Set<PermissionBroadcastReceiver> broadcastReceivers = new HashSet<PermissionBroadcastReceiver>();
	private SerialReceiverListener serialReceiverListener;

	public AttachedDeviceHandler(Context context, SerialReceiverListener serialReceiverListener) {
		this.context = context;
		this.serialReceiverListener = serialReceiverListener;
		usbManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
	}

	public void onStop() {
		for (PermissionBroadcastReceiver br : broadcastReceivers) {
			context.unregisterReceiver(br);
		}
	}

	public void handleIntent(Intent intent) throws UsbConnectionException {
		if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
			UsbDevice usbDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
			connectWithPermission(usbDevice);
		}
		else {
			initConnectedDevices();
		}
	}

	public void initConnectedDevices() throws UsbConnectionException {
		Map<String, UsbDevice> deviceList = usbManager.getDeviceList();
		boolean found = false;
		for (Map.Entry<String, UsbDevice> e : deviceList.entrySet()) {
			Log.d(Constants.TAG, "USB Device: " + e.getKey() + " " + e.getValue());
			UsbDevice device = e.getValue();
			int vendorId = device.getVendorId();
			int productId = device.getProductId();
			Log.d(Constants.TAG, Integer.toHexString(vendorId) + " " + Integer.toHexString(productId));
			if (vendorId == VENDOR_ID && productId == PRODUCT_ID) {
				connectWithPermission(device);
				found = true;
				Toast.makeText(context, "Device found!", Toast.LENGTH_LONG).show();
			}
		}
		if (!found) {
			Log.e(Constants.TAG, "device not found");
			Toast.makeText(context, "Device not found", Toast.LENGTH_LONG).show();
		}
	}

	private void connectWithPermission(UsbDevice device) throws UsbConnectionException {
		if (!usbManager.hasPermission(device)) {
			PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(Constants.ACTION_USB_PERMISSION), 0);
			IntentFilter filter = new IntentFilter(Constants.ACTION_USB_PERMISSION);
			PermissionBroadcastReceiver permissionBroadcastReceiver = new PermissionBroadcastReceiver(this);
			broadcastReceivers.add(permissionBroadcastReceiver);
			context.registerReceiver(permissionBroadcastReceiver, filter);
			usbManager.requestPermission(device, pi);
		}
		else {
			connectUsb(device);
		}
	}

	protected void connectUsb(UsbDevice usbDevice) throws UsbConnectionException {
		Log.d(Constants.TAG, "has permission: " + usbManager.hasPermission(usbDevice));
		UsbDeviceConnection connection = usbManager.openDevice(usbDevice);
		if (connection != null) {
			UsbInterface usbInterface = usbDevice.getInterface(0);
			if (!connection.claimInterface(usbInterface, true)) {
				throw new UsbConnectionException("no exclusive rights");
			}
			PL2303Driver driver = new PL2303Driver(usbManager, context, "USB_PERMISSION");
			driver.enumerate();
			int timeout = 0;
			while (!driver.isConnected() && timeout < 10) {
				try {
					Thread.sleep(1000);
					timeout++;
				}
				catch (InterruptedException e) {
					break;
				}
			}
			System.out.println("Connected: " + driver.isConnected());
			driver.InitByPortSetting(PL2303Driver.BaudRate.B19200, PL2303Driver.DataBits.D8, PL2303Driver.StopBits.S2,
					PL2303Driver.Parity.ODD, PL2303Driver.FlowControl.OFF);
			SerialReceiver serialReceiver = new SerialReceiver(driver);
			serialReceiverListener.onSerialReceiver(serialReceiver);
		}
		else {
			throw new UsbConnectionException("null connection received");
		}
	}
}
