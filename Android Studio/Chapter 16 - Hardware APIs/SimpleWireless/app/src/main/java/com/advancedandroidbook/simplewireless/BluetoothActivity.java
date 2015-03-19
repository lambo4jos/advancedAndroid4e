package com.advancedandroidbook.simplewireless;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class BluetoothActivity extends Activity {
	private static final String SIMPLE_BT_RESPONSE_POLO = "polo";
	private static final String SIMPLE_BT_COMMAND_MARCO = "marco";
	private static final String SIMPLE_BT_COMMAND_PING = "ping";
	private static final int BT_DISCOVERABLE_DURATION = 300; // max duration,
																// useful for
																// debuggin
																// //120;
	private static final String DEBUG_TAG = "SimpleBluetooth";
	// generated randomly online; search for "web uuid generate"; and then
	// edited slightly, keep in mind it's hex
	private static final UUID SIMPLE_BT_APP_UUID = UUID
			.fromString("0dfb786a-cafe-feed-cafe-982fdfe4bcbf");
	private static final String SIMPLE_BT_NAME = "SimpleBT";
	// dialog id
	private static final int DEVICE_PICKER_DIALOG = 1001;
	// intercom with threads
	private final Handler handler = new Handler();
	private BluetoothAdapter btAdapter;
	private BtReceiver btReceiver;
	private ServerListenThread serverListenThread;
	private ClientConnectThread clientConnectThread;
	private BluetoothDataCommThread bluetoothDataCommThread;
	private BluetoothDevice remoteDevice;
	private BluetoothSocket activeBluetoothSocket;

	// for sound
	private MediaPlayer player;
	private HashMap<String, BluetoothDevice> discoveredDevices = new HashMap<String, BluetoothDevice>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null) {
			// no bluetooth available on device
			setStatus("No bluetooth available. :(");
			disableAllButtons();
		} else {
			setStatus("Bluetooth available! :)");
			// we need a broadcast receiver now
			btReceiver = new BtReceiver();
			// register for state change broadcast events
			IntentFilter stateChangedFilter = new IntentFilter(
					BluetoothAdapter.ACTION_STATE_CHANGED);
			this.registerReceiver(btReceiver, stateChangedFilter);
			// register for discovery events
			IntentFilter actionFoundFilter = new IntentFilter(
					BluetoothDevice.ACTION_FOUND);
			registerReceiver(btReceiver, actionFoundFilter);
			// check current state
			int currentState = btAdapter.getState();
			setUIForBTState(currentState);
			if (currentState == BluetoothAdapter.STATE_ON) {
				findDevices();
			}
		}
	}

	private void findDevices() {
		String lastUsedRemoteDevice = getLastUsedRemoteBTDevice();
		if (lastUsedRemoteDevice != null) {
			setStatus("Checking for known paired devices, namely: "
					+ lastUsedRemoteDevice);
			// see if this device is in a list of currently visible (?), paired
			// devices
			Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
			for (BluetoothDevice pairedDevice : pairedDevices) {
				if (pairedDevice.getAddress().equals(lastUsedRemoteDevice)) {
					setStatus("Found device: " + pairedDevice.getName() + "@"
							+ lastUsedRemoteDevice);
					remoteDevice = pairedDevice;
				}
			}
		}

		if (remoteDevice == null) {
			setStatus("Starting discovery...");
			// start discovery
			if (btAdapter.startDiscovery()) {
				setStatus("Discovery started...");
			}

		}

		// also set discoverable
		setStatus("Enabling discoverable, user will see dialog...");
		Intent discoverMe = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverMe.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
				BT_DISCOVERABLE_DURATION);
		startActivity(discoverMe);

		// also start listening for connections
		setStatus("Enabling listening socket thread");
		serverListenThread = new ServerListenThread();
		serverListenThread.start();
	}

	private String getLastUsedRemoteBTDevice() {
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		String result = prefs.getString("LAST_REMOTE_DEVICE_ADDRESS", null);
		return result;
	}

	private void setLastUsedRemoteBTDevice(String name) {
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		Editor edit = prefs.edit();
		edit.putString("LAST_REMOTE_DEVICE_ADDRESS", name);
		edit.commit();
	}

	private void disableAllButtons() {
		Button button;
		int[] buttonIds = { R.id.bt_toggle, R.id.connect, R.id.marco, R.id.ping };
		for (int buttonId : buttonIds) {
			button = (Button) findViewById(buttonId);
			button.setEnabled(false);
		}
	}

	private ToggleButton btToggle;

	private void setUIForBTState(int state) {
		if (btToggle == null) {
			btToggle = (ToggleButton) findViewById(R.id.bt_toggle);
		}
		switch (state) {
		case BluetoothAdapter.STATE_ON:
			btToggle.setChecked(true);
			btToggle.setEnabled(true);
			setStatus("BT state now on");
			break;
		case BluetoothAdapter.STATE_OFF:
			btToggle.setChecked(false);
			btToggle.setEnabled(true);
			setStatus("BT state now off");
			break;
		case BluetoothAdapter.STATE_TURNING_OFF:
			btToggle.setChecked(true);
			btToggle.setEnabled(false);
			setStatus("BT state turning off");
			break;
		case BluetoothAdapter.STATE_TURNING_ON:
			btToggle.setChecked(false);
			btToggle.setEnabled(false);
			setStatus("BT state turning on");
			break;
		}
	}

	private TextView statusField;

	private void setStatus(String string) {
		if (statusField == null) {
			statusField = (TextView) findViewById(R.id.output_display);
		}
		String current = (String) statusField.getText();
		current = string + "\n" + current;
		// don't let it get too long
		if (current.length() > 1500) {
			int truncPoint = current.lastIndexOf("\n");
			current = (String) current.subSequence(0, truncPoint);
		}
		statusField.setText(current);
	}

	public void doToggleBT(View view) {
		Log.v(DEBUG_TAG, "doToggleBT() called");
		if (btToggle == null) {
			btToggle = (ToggleButton) findViewById(R.id.bt_toggle);
		}
		// check the new state of the button to see what the user wants done
		if (btToggle.isChecked() == false) {
			if (serverListenThread != null) {
				serverListenThread.stopListening();
			}
			if (clientConnectThread != null) {
				clientConnectThread.stopConnecting();
			}
			if (bluetoothDataCommThread != null) {
				bluetoothDataCommThread.disconnect();
			}
			btAdapter.cancelDiscovery();
			if (!btAdapter.disable()) {
				setStatus("Disable adapter failed");
			}

			remoteDevice = null;
			activeBluetoothSocket = null;
			serverListenThread = null;
			clientConnectThread = null;
			bluetoothDataCommThread = null;
			discoveredDevices.clear();
		} else {
			if (!btAdapter.enable()) {
				setStatus("Enable adapter failed");
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void doConnectBT(View view) {
		Log.v(DEBUG_TAG, "doConnectBT() called");
		if (remoteDevice != null) {
			// connect to remoteDevice
			doConnectToDevice(remoteDevice);
		} else {
			// get the device the user wants to connect to
			showDialog(DEVICE_PICKER_DIALOG);
		}
	}

	public void doConnectToDevice(BluetoothDevice device) {
		// halt discovery
		btAdapter.cancelDiscovery();
		setStatus("Starting connect thread");
		clientConnectThread = new ClientConnectThread(device);
		clientConnectThread.start();
	}

	public void doStartDataCommThread() {
		if (activeBluetoothSocket == null) {
			setStatus("Can't start datacomm");
			Log.w(DEBUG_TAG,
					"Something is wrong, shouldn't be trying to use datacomm when no socket");
		} else {
			setStatus("Data comm thread starting");
			bluetoothDataCommThread = new BluetoothDataCommThread(
					activeBluetoothSocket);
			bluetoothDataCommThread.start();
		}
	}

	public void doSendPing(View view) {
		Log.v(DEBUG_TAG, "doSendPing() called");
		if (bluetoothDataCommThread != null) {
			bluetoothDataCommThread.send(SIMPLE_BT_COMMAND_PING);
		}
	}

	public void doSendMarco(View view) {
		Log.v(DEBUG_TAG, "doSendMarco() called");
		if (bluetoothDataCommThread != null) {
			bluetoothDataCommThread.send(SIMPLE_BT_COMMAND_MARCO);
		}
	}

	public void doHandleReceivedCommand(String rawCommand) {
		String command = rawCommand.trim();
		setStatus("Got: " + command);
		if (command.equals(SIMPLE_BT_COMMAND_PING)) {
			if (player != null) {
				player.release();
			}
			player = new MediaPlayer();
			try {
				player.setDataSource(
						this,
						Uri.parse("android.resource://com.advancedandroidbook.simplewireless/"
								+ R.raw.ping));
				player.prepare();
				player.start();
				bluetoothDataCommThread.send("pong");
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "Failed to start audio", e);
				setStatus("Got ping, can't play sound");
				bluetoothDataCommThread.send("failed");
			}

		} else if (command.equals(SIMPLE_BT_COMMAND_MARCO)) {
			bluetoothDataCommThread.send(SIMPLE_BT_RESPONSE_POLO);
		}
	}

	private class BtReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				setStatus("Broadcast: Got ACTION_STATE_CHANGED");
				int currentState = intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE,
						BluetoothAdapter.STATE_OFF);
				setUIForBTState(currentState);
				if (currentState == BluetoothAdapter.STATE_ON) {
					findDevices();
				}
			} else if (action.equals(BluetoothDevice.ACTION_FOUND)) {
				setStatus("Broadcast: Got ACTION_FOUND");
				BluetoothDevice foundDevice = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				setStatus("Device: " + foundDevice.getName() + "@"
						+ foundDevice.getAddress());
				discoveredDevices.put(foundDevice.getName(), foundDevice);
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (serverListenThread != null) {
			serverListenThread.stopListening();
		}
		if (clientConnectThread != null) {
			clientConnectThread.stopConnecting();
		}
		if (bluetoothDataCommThread != null) {
			bluetoothDataCommThread.disconnect();
		}
		if (activeBluetoothSocket != null) {
			try {
				activeBluetoothSocket.close();
			} catch (IOException e) {
				Log.e(DEBUG_TAG, "Failed to close socket", e);
			}
		}
		btAdapter.cancelDiscovery();
		this.unregisterReceiver(btReceiver);
		if (player != null) {
			player.stop();
			player.release();
			player = null;
		}
		super.onDestroy();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DEVICE_PICKER_DIALOG:
			if (discoveredDevices.size() > 0) {
				ListView list = new ListView(this);
				String[] deviceNames = discoveredDevices.keySet().toArray(
						new String[discoveredDevices.keySet().size()]);
				ArrayAdapter<String> deviceAdapter = new ArrayAdapter<String>(
						this, android.R.layout.simple_list_item_1, deviceNames);
				list.setAdapter(deviceAdapter);
				list.setOnItemClickListener(new OnItemClickListener() {
					@SuppressWarnings("deprecation")
					@Override
					public void onItemClick(AdapterView<?> adapter, View view,
							int position, long id) {
						String name = (String) ((TextView) view).getText();
						removeDialog(DEVICE_PICKER_DIALOG);
						setStatus("Remote device chosen: " + name);
						doConnectToDevice(discoveredDevices.get(name));
					}
				});
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setView(list);
				builder.setTitle(R.string.pick_device);
				builder.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							@SuppressWarnings("deprecation")
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								removeDialog(DEVICE_PICKER_DIALOG);
								setStatus("No remote BT picked.");
							}
						});
				dialog = builder.create();
			} else {
				setStatus("No devices found to pick from");
			}
			break;
		}
		return dialog;
	}

	// helper threads
	// server thread; listen for connections. both sides do this. always have to
	// have one server and one client
	// if a client connects, we get a connected BluetoothSocket to use
	// if this device conncets to another server, we'll cancel the listening and
	// use that connection, also a BluetoothSocket
	private class ServerListenThread extends Thread {
		private final BluetoothServerSocket btServerSocket;

		public ServerListenThread() {
			BluetoothServerSocket btServerSocket = null;
			try {
				btServerSocket = btAdapter.listenUsingRfcommWithServiceRecord(
						SIMPLE_BT_NAME, SIMPLE_BT_APP_UUID);
			} catch (IOException e) {
				Log.e(DEBUG_TAG, "Failed to start listening", e);
			}
			// finalize
			this.btServerSocket = btServerSocket;
		}

		public void run() {
			BluetoothSocket socket = null;
			try {
				while (true) {
					handler.post(new Runnable() {
						public void run() {
							setStatus("ServerThread: calling accept");
						}
					});
					socket = btServerSocket.accept();
					if (socket != null) {
						activeBluetoothSocket = socket;
						// Do work to manage the connection (in a separate
						// thread)
						handler.post(new Runnable() {
							public void run() {
								setStatus("Got a device socket");
								doStartDataCommThread();
							}
						});
						btServerSocket.close();
						break;
					}
				}
			} catch (Exception e) {
				handler.post(new Runnable() {
					public void run() {
						setStatus("Listening socket done - failed or cancelled");
					}
				});
			}
		}

		public void stopListening() {
			try {
				btServerSocket.close();
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "Failed to close listening socket", e);
			}
		}
	}

	// client thread: used to make a synchronous connect call to a device
	private class ClientConnectThread extends Thread {
		private final BluetoothDevice remoteDevice;
		private final BluetoothSocket clientSocket;

		public ClientConnectThread(BluetoothDevice remoteDevice) {
			this.remoteDevice = remoteDevice;
			BluetoothSocket clientSocket = null;
			try {
				clientSocket = remoteDevice
						.createRfcommSocketToServiceRecord(SIMPLE_BT_APP_UUID);
			} catch (IOException e) {
				Log.e(DEBUG_TAG, "Failed to open local client socket");
			}
			// finalize
			this.clientSocket = clientSocket;
		}

		public void run() {
			boolean success = false;
			try {
				clientSocket.connect();
				success = true;
			} catch (IOException e) {
				Log.e(DEBUG_TAG, "Client connect failed or cancelled");
				try {
					clientSocket.close();
				} catch (IOException e1) {
					Log.e(DEBUG_TAG, "Failed to close socket on error", e);
				}
			}
			final String status;
			if (success) {
				status = "Connected to remote device";
				activeBluetoothSocket = clientSocket;
				// we don't need to keep listening
				serverListenThread.stopListening();
			} else {
				status = "Failed to connect to remote device";
				activeBluetoothSocket = null;
			}
			handler.post(new Runnable() {
				public void run() {
					setStatus(status);
					setLastUsedRemoteBTDevice(remoteDevice.getAddress());
					doStartDataCommThread();
				}
			});
		}

		public void stopConnecting() {
			try {
				clientSocket.close();
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "Failed to stop connecting", e);
			}
		}
	}

	private class BluetoothDataCommThread extends Thread {
		private final BluetoothSocket dataSocket;
		private final OutputStream outData;
		private final InputStream inData;

		public BluetoothDataCommThread(BluetoothSocket dataSocket) {
			this.dataSocket = dataSocket;
			OutputStream outData = null;
			InputStream inData = null;
			try {
				outData = dataSocket.getOutputStream();
				inData = dataSocket.getInputStream();
			} catch (IOException e) {
				Log.e(DEBUG_TAG, "Failed to get iostream", e);
			}
			this.inData = inData;
			this.outData = outData;
		}

		public void run() {
			byte[] readBuffer = new byte[64];
			int readSize = 0;
			try {
				while (true) {
					readSize = inData.read(readBuffer);

					final String inStr = new String(readBuffer, 0, readSize);
					handler.post(new Runnable() {
						public void run() {
							doHandleReceivedCommand(inStr);
						}
					});
				}
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "Socket failure or closed", e);
			}
		}

		public boolean send(String out) {
			boolean success = false;
			try {
				outData.write(out.getBytes(), 0, out.length());
				success = true;
			} catch (IOException e) {
				Log.e(DEBUG_TAG, "Failed to write to remote device", e);
				setStatus("Send failed");
			}
			return success;
		}

		public void disconnect() {
			try {
				dataSocket.close();
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "Failed to close datacomm socket", e);
			}
		}
	}
}