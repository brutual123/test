package pe.niyux.calllog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CallLog;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView txtOutput = (TextView) findViewById(R.id.txtOutput);
		// File sdcard = Environment.getExternalStorageDirectory();
		// File dirs = new File(sdcard.getAbsolutePath());
		// File file = null;
		//
		// if(dirs.exists()) {
		// File[] files = dirs.listFiles();
		// for (File f : files) {
		// Log.d(TAG, f.getAbsolutePath());
		// if(f.getAbsolutePath().trim().equals("/storage/sdcard0/Download")){
		// file = f;
		// break;
		// }
		// }
		// }
		if (isExternalStorageWritable()) {
			File fileName = Environment.getExternalStorageDirectory();
			// File file2 = new File(file, "calllog.txt");
			// File dir = getExternalFilesDir(null);
			File file2 = new File(fileName, "calllog.txt");
			FileOutputStream fos;
			try {
				file2.createNewFile();
				Log.d(TAG, file2.getAbsolutePath());
				fos = new FileOutputStream(file2);
				OutputStreamWriter osw = new OutputStreamWriter(fos);
				getCallDetails(osw);

				osw.close();
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			txtOutput.setText(getCallDetails());
		} else {
			txtOutput.setText("not writeable");
		}

	}

	public File getAlbumStorageDir(String albumName) {
		// Get the directory for the user's public pictures directory.
		File file = new File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
				albumName);
		if (!file.mkdirs()) {
			Log.e(TAG, "Directory not created");
		}
		return file;
	}

	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	private String getCallDetails(OutputStreamWriter osw) throws IOException {

		Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);
//		osw.append("getColumnCount:" + managedCursor.getColumnCount() + "\n");
		String[] columnNames = managedCursor.getColumnNames();
		for (String s : columnNames) {
			osw.append(s);
			osw.append(",");
		}

		osw.append("\n");
		int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
		while (managedCursor.moveToNext()) {
			for (String s : columnNames) {
				int index = managedCursor.getColumnIndex(s);
				int cursorType = managedCursor.getType(index);
				switch (cursorType) {
				case Cursor.FIELD_TYPE_STRING:
					osw.append(managedCursor.getString(index));
					break;
				case Cursor.FIELD_TYPE_INTEGER:
					osw.append(String.valueOf(managedCursor.getInt(index)));
					break;
				case Cursor.FIELD_TYPE_FLOAT: 
					osw.append(String.valueOf(managedCursor.getFloat(index)));
					break;
				case Cursor.FIELD_TYPE_NULL:
					osw.append(null);
					break;
				case Cursor.FIELD_TYPE_BLOB:
					osw.append(new String(managedCursor.getBlob(index)));
					break;
				default:
					osw.append("NOT EXPECTED");
					break;
				}
				osw.append(",");
			}
			osw.append("\n");

			String phNumber = managedCursor.getString(number);
			String callType = managedCursor.getString(type);
			String callDate = managedCursor.getString(date);
			Date callDayTime = new Date(Long.valueOf(callDate));
			String callDuration = managedCursor.getString(duration);
			String dir = null;
			int dircode = Integer.parseInt(callType);
			switch (dircode) {
			case CallLog.Calls.OUTGOING_TYPE:
				dir = "OUTGOING";
				break;

			case CallLog.Calls.INCOMING_TYPE:
				dir = "INCOMING";
				break;

			case CallLog.Calls.MISSED_TYPE:
				dir = "MISSED";
				break;
			}
//			osw.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir
//					+ " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- "
//					+ callDuration);
//			osw.append("\n----------------------------------");
		}
		managedCursor.close();
		return osw.toString();

	}

	private String getCallDetails() {

		StringBuffer sb = new StringBuffer();
		Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);
		sb.append("getColumnCount:" + managedCursor.getColumnCount() + "\n");
		for (String s : managedCursor.getColumnNames()) {
			sb.append(s);
			sb.append(",");
		}

		sb.append("-------------------------------------");
		int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
		sb.append("Call Details :");
		while (managedCursor.moveToNext()) {
			String phNumber = managedCursor.getString(number);
			String callType = managedCursor.getString(type);
			String callDate = managedCursor.getString(date);
			Date callDayTime = new Date(Long.valueOf(callDate));
			String callDuration = managedCursor.getString(duration);
			String dir = null;
			int dircode = Integer.parseInt(callType);
			switch (dircode) {
			case CallLog.Calls.OUTGOING_TYPE:
				dir = "OUTGOING";
				break;

			case CallLog.Calls.INCOMING_TYPE:
				dir = "INCOMING";
				break;

			case CallLog.Calls.MISSED_TYPE:
				dir = "MISSED";
				break;
			}
			sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir
					+ " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- "
					+ callDuration);
			sb.append("\n----------------------------------");
		}
		managedCursor.close();
		return sb.toString();

	}
}
