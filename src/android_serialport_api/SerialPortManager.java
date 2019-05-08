package android_serialport_api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android.os.SystemClock;
import android.util.Log;
import android_serialport_api.BeidouAPI.ReceiveData;

import com.authentication.utils.DataUtils;

public class SerialPortManager {
	/**
	 * 串口设备路径
	 */
	private static final String PATH = "/dev/ttyHS1";
	/**
	 * 串口波特率
	 */

//	 private static final int BAUDRATE = 115200;
//	private static final int BAUDRATE = 230400;
	// private static final int BAUDRATE = 345600;
	 private static final int BAUDRATE = 460800;

	public static boolean switchRFID = false;

	final String GPIO_DEV = "/sys/GPIO/GPIO13/value";
	final byte[] UP = { '1' };
	final byte[] DOWN = { '0' };

	private static SerialPortManager mSerialPortManager = new SerialPortManager();

	private SerialPort mSerialPort = null;

	private boolean isOpen;

	private boolean firstOpen = false;

	private OutputStream mOutputStream;

	private InputStream mInputStream;

	private byte[] mBuffer = new byte[50 * 1024];

	private int mCurrentSize = 0;

	private ReadThread mReadThread;

	private SerialPortManager() {
	}

	/**
	 * 获取该类的实例对象，为单例
	 * 
	 * @return
	 */
	public static SerialPortManager getInstance() {
		return mSerialPortManager;
	}

	/**
	 * 判断串口是否打开
	 * 
	 * @return true：打开 false：未打开
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * 打开串口，如果需要读取身份证和指纹信息，必须先打开串口，调用此方法
	 * 
	 * @throws SecurityException
	 * @throws IOException
	 * @throws InvalidParameterException
	 */
	public boolean openSerialPort() throws SecurityException, IOException,
			InvalidParameterException {
		if (mSerialPort == null) {
			// 上电
			setUpGpio();
			Log.i("whw", "setUpGpio status=" + getGpioStatus());
			/* Open the serial port */
			mSerialPort = new SerialPort(new File(PATH), BAUDRATE, 0);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			mReadThread = new ReadThread();
			mReadThread.start();
			isOpen = true;
			firstOpen = true;
			return true;
		}
		return false;
	}

	/**
	 * 关闭串口，如果不需要读取指纹或身份证信息时，就关闭串口(可以节约电池电量)，建议程序退出时关闭
	 */
	public void closeSerialPort() {
		if (mReadThread != null)
			mReadThread.interrupt();
		mReadThread = null;
		try {
			// 断电
			setDownGpio();
			Log.i("whw", "setDownGpio status=" + getGpioStatus());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (mSerialPort != null) {
			try {
				mOutputStream.close();
				mInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mSerialPort.close();
			mSerialPort = null;
		}
		isOpen = false;
		firstOpen = false;
		mCurrentSize = 0;
		switchRFID = false;
	}

	protected synchronized int read(byte buffer[], int waittime,
			int endWaitTimeout) {
		if (!isOpen) {
			return 0;
		}
		int sleepTime = 50;
		int length = waittime / sleepTime;
		boolean shutDown = false;
		int[] readDataLength = new int[3];
		for (int i = 0; i < length; i++) {
			if (mCurrentSize == 0) {
				SystemClock.sleep(sleepTime);
				continue;
			} else {
				break;
			}
		}

		if (mCurrentSize > 0) {
			while (!shutDown) {
				SystemClock.sleep(endWaitTimeout / 3);
				readDataLength[0] = readDataLength[1];
				readDataLength[1] = readDataLength[2];
				readDataLength[2] = mCurrentSize;
				Log.i("whw", "read2    mCurrentSize=" + mCurrentSize);
				if (readDataLength[0] == readDataLength[1]
						&& readDataLength[1] == readDataLength[2]) {
					shutDown = true;
				}
			}
			if (mCurrentSize <= buffer.length) {
				System.arraycopy(mBuffer, 0, buffer, 0, mCurrentSize);
			}
		}
		return mCurrentSize;
	}
	
	protected synchronized int readFixedLength(byte buffer[], int waittime,
			int requestLength) {
		if (!isOpen) {
			return 0;
		}
		int sleepTime = 10;
		int length = waittime / sleepTime;
		boolean shutDown = false;
		int[] readDataLength = new int[3];
		for (int i = 0; i < length; i++) {
			if (mCurrentSize == 0) {
				SystemClock.sleep(sleepTime);
				continue;
			} else {
				break;
			}
		}
		
		
		if (mCurrentSize > 0) {
			while (!shutDown) {
				if (mCurrentSize == requestLength) {
					shutDown = true;
				} else {
					shutDown = isTimeout(readDataLength);
				}
			}

			if (mCurrentSize <= buffer.length) {
				System.arraycopy(mBuffer, 0, buffer, 0, mCurrentSize);
			}
		}
		return mCurrentSize;
	}
	
	public boolean isTimeout(int[] data) {
		if (data != null) {
			SystemClock.sleep(40);
			for (int i = 0; i < data.length; i++) {
				if (i == data.length - 1) {
					data[i] = mCurrentSize;
				} else {
					data[i] = data[i + 1];
				}
			}
			if (data[0] == data[data.length - 1]) {
				Log.i("whw", "&&&&&&&&&&data[0]="+data[0]);
				return true;
			}
		}
		return false;
	}
	
	protected  void readBeidou(int waittime) {
		if (!isOpen) {
			return ;
		}
		int sleepTime = 50;
		int length = waittime / sleepTime;
		for (int i = 0; i < length; i++) {
			if (mCurrentSize == 0) {
				SystemClock.sleep(sleepTime);
				continue;
			} else {
				break;
			}
		}
		byte buffer[] = null;
		while(BeidouAPI.isSwitch()){
			if(mCurrentSize > 2){
				if(mBuffer[mCurrentSize-2]=='\r' && mBuffer[mCurrentSize-1] == '\n'){
					buffer = new byte[mCurrentSize];
					System.arraycopy(mBuffer, 0, buffer, 0, mCurrentSize);
					mCurrentSize = 0;
					receiveData.onReceive(buffer);
				}
			}
		}
	}
	
	private void writeCommand(byte[] data){
		if (!isOpen) {
			return;
		}
		if (firstOpen) {
			SystemClock.sleep(2000);
			firstOpen = false;
		}
		mCurrentSize = 0;
		try {
			mOutputStream.write(data);
		} catch (IOException e) {
		}
	}

	protected synchronized void write(byte[] data) {
		Log.i("whw", "send commnad="+DataUtils.toHexString(data));
		writeCommand(data);
	}
	
	protected  void writeBeidou(byte[] data,ReceiveData receiveData) {
		this.receiveData = receiveData;
		writeCommand(data);
	}

	private void setUpGpio() throws IOException {
		FileOutputStream fw = new FileOutputStream(GPIO_DEV);
		fw.write(UP);
		fw.close();
	}

	private void setDownGpio() throws IOException {
		FileOutputStream fw = new FileOutputStream(GPIO_DEV);
		fw.write(DOWN);
		fw.close();
	}

	private String getGpioStatus() throws IOException {
		String value;
		BufferedReader br = null;

		FileInputStream inStream = new FileInputStream(GPIO_DEV);
		br = new BufferedReader(new InputStreamReader(inStream));

		value = br.readLine();
		inStream.close();
		System.out.println(value);
		return value;

	}
	
	private ReceiveData receiveData;

	private class ReadThread extends Thread {

		@Override
		public void run() {
			byte[] buffer = new byte[100];
			while (!isInterrupted()) {
				int length = 0;
				try {
					if (mInputStream == null)
						return;
					length = mInputStream.read(buffer);
					if (length > 0) {
						System.arraycopy(buffer, 0, mBuffer, mCurrentSize,
								length);
						mCurrentSize += length;
					}
					Log.i("whw", "mCurrentSize=" + mCurrentSize + "  length="
							+ length);
					
					if(BeidouAPI.isSwitch()){
						if(mCurrentSize > 2){
							if(mBuffer[mCurrentSize-2]=='\r' && mBuffer[mCurrentSize-1] == '\n'){
								byte[] tempBuffer = new byte[mCurrentSize];
								System.arraycopy(mBuffer, 0, tempBuffer, 0, mCurrentSize);
								mCurrentSize = 0;
								receiveData.onReceive(tempBuffer);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

}
