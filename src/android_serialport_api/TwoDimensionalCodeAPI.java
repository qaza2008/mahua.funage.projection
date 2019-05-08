package android_serialport_api;

import android.os.SystemClock;
import android.util.Log;

import com.authentication.utils.DataUtils;

public class TwoDimensionalCodeAPI {
	private static final byte[] OPEN_COMMAND = "D&C0004010B".getBytes();
	private static final byte[] CLOSE_COMMAND = "D&C0004010C".getBytes();

	private static final byte[] WAKEUP = { 0x00 };

	private static final byte[] RESTORE_COMMAND = { 0x04, (byte) 0xC8, 0x04,
			0x01, (byte) 0xFF, 0x2F };
	private static final byte[] SET_HOST_COMMAND = { 0x07, (byte) 0xc6, 0x04,
			0x01, 0x0d, (byte) 0x8a, 0x08, (byte) 0xfe, (byte) 0x8f };

	private static final byte[] START_DECODE = { 0x04, (byte) 0xe4, 0x04, 0x01,
			(byte) 0xff, 0x13 };

	private static final byte[] LED_ON = { 0x05, (byte) 0xe7, 0x04, 0x00, 0x01,
			(byte) 0xfe, 0x79 };

	private static final byte[] BEEP = { 0x05, (byte) 0xe6, 0x04, 0x00, 0x00,
			(byte) 0xfe, 0x7a };

	private static final byte[] DECODE_DATA_PACKET_FORMAT = { 0x07,
			(byte) 0xc6, 0x04, 0x01, 0x0d, (byte) 0xee, 0x01, (byte) 0xfe, 0x32 };

	private static final byte[] CMD_ACK = { 0x04, (byte) 0xd0, 0x04, 0x00,
			(byte) 0xff, 0x28 };

	private static final byte[] CMD_NAK = { 0x05, (byte) 0xd1, 0x04, 0x00,
			0x00, 0x06, (byte) 0xff, 0x20 };

	private static final byte[] buffer = new byte[1024];

	private boolean isOpen = false;

	public boolean isOpen() {
		return isOpen;
	}

	public void open() {
		SerialPortManager.getInstance().write(OPEN_COMMAND);
		int length = SerialPortManager.getInstance().read(buffer, 100, 100);
		Log.i("whw", "open length=" + length + "  buffer[0]=" + buffer[0]);
		isOpen = true;
		SystemClock.sleep(20);
	}

	public void close() {
		SerialPortManager.getInstance().write(CLOSE_COMMAND);
		isOpen = false;
	}

	public void restore() {
		SerialPortManager.getInstance().write(WAKEUP);
		SystemClock.sleep(20);
		SerialPortManager.getInstance().write(RESTORE_COMMAND);
		int length = SerialPortManager.getInstance().read(buffer, 500, 100);
		print("restore");
		SystemClock.sleep(200);
	}

	public boolean setHost() {
		for (int i = 0; i < 3; i++) {
			SerialPortManager.getInstance().write(WAKEUP);
			SystemClock.sleep(200);
			SerialPortManager.getInstance().write(SET_HOST_COMMAND);
			int length = SerialPortManager.getInstance().read(buffer, 100, 100);
			Log.i("whw", "setHost length=" + length);
			if (length == 0) {
				sendNAK();
			} else if (length == 6) {
				return true;
			}
			print("setHost");
		}
		return false;
	}

	public String startDecode() {
		for (int i = 0; i < 4; i++) {
			Log.i("whw", "for *********************** i=" + i);
			SerialPortManager.getInstance().write(WAKEUP);
			SystemClock.sleep(20);
			SerialPortManager.getInstance().write(START_DECODE);
			int length1 = SerialPortManager.getInstance()
					.read(buffer, 100, 100);
			Log.i("whw", "startDecode length=" + length1);
			print("startDecode");
			if (length1 > 6 && buffer[0] == 0x04) {

				int packetLenght = length1 - 6;
				if (packetLenght > 0) {
					sendACK();
					byte[] packet = new byte[packetLenght];
					System.arraycopy(buffer, 6, packet, 0, packetLenght);
					Log.i("whw", "packet hex=" + DataUtils.toHexString(packet));
					String data = getDecodeData(packet);
					Log.i("whw", "DecodeData=" + data);
					return data;
				}
			} else if (length1 == 0) {
				continue;
			} else if (length1 == 7) {
				break;
			}
		}
		return "";
	}

	private String getDecodeData(byte[] data) {
		if (data != null && data.length > 0) {
			return new String(data, 5, data[0] - 5);
		}

		return "";

	}

	public boolean decodeDataPacketFormat() {
		for (int i = 0; i < 3; i++) {
			SerialPortManager.getInstance().write(WAKEUP);
			SystemClock.sleep(200);
			SerialPortManager.getInstance().write(DECODE_DATA_PACKET_FORMAT);
			int length = SerialPortManager.getInstance().read(buffer, 100, 100);
			Log.i("whw", "DECODE_DATA_PACKET_FORMAT length=" + length);
			SystemClock.sleep(400);

			if (length == 0) {
				sendNAK();
			} else if (length == 6) {
				return true;
			}
		}
		return false;
	}

	public void ledOn() {
		SerialPortManager.getInstance().write(LED_ON);
		int length = SerialPortManager.getInstance().read(buffer, 100, 100);
		for (int i = 0; i < length; i++) {
			Log.i("whw", "ledOn length=" + length + "    buffer[" + i + "]="
					+ buffer[i]);
		}
	}

	public void beep() {
		SerialPortManager.getInstance().write(BEEP);
		int length = SerialPortManager.getInstance().read(buffer, 100, 100);
		for (int i = 0; i < length; i++) {
			Log.i("whw", "BEEP length=" + length + "    buffer[" + i + "]="
					+ buffer[i]);
		}
		Log.i("whw", "hex=" + DataUtils.toHexString(buffer));
	}

	public void sendACK() {
		SerialPortManager.getInstance().write(WAKEUP);
		SystemClock.sleep(20);
		SerialPortManager.getInstance().write(CMD_ACK);
	}

	public void sendNAK() {
		SerialPortManager.getInstance().write(WAKEUP);
		SystemClock.sleep(20);
		SerialPortManager.getInstance().write(CMD_NAK);
	}

	private void print(String str) {
		byte[] temp = new byte[10];
		System.arraycopy(buffer, 0, temp, 0, temp.length);
		Log.i("whw", str + " hex=" + DataUtils.toHexString(temp));
	}
}
