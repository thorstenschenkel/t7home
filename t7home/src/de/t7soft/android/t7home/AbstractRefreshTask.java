package de.t7soft.android.t7home;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import de.t7soft.android.t7home.database.HomeDatabaseAdapter;
import de.t7soft.android.t7home.smarthome.api.SmartHomeLocation;
import de.t7soft.android.t7home.smarthome.api.SmartHomeSession;
import de.t7soft.android.t7home.smarthome.api.devices.TemperatureHumidityDevice;
import de.t7soft.android.t7home.smarthome.api.exceptions.SmartHomeSessionExpiredException;

public abstract class AbstractRefreshTask extends AsyncTask<String, Void, Integer> {

	public static final int REFRESH_OK = 0;
	public static final int REFRESH_ERROR = 1;

	private final ProgressDialog progressDialog;
	private final AlertDialog.Builder alertDialogBuilder;
	private final HomeDatabaseAdapter dbAdapter;
	private final Context context;

	public AbstractRefreshTask(Context context, HomeDatabaseAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
		this.context = context;
		progressDialog = new ProgressDialog(context);
		alertDialogBuilder = new AlertDialog.Builder(context);
	}

	private void storeLocations(SmartHomeSession session) {
		dbAdapter.deleteAllLocations();
		ConcurrentHashMap<String, SmartHomeLocation> locationsMap = session.getLocations();
		if (locationsMap == null) {
			return;
		}
		Enumeration<SmartHomeLocation> locations = locationsMap.elements();
		while (locations.hasMoreElements()) {
			dbAdapter.insertLocation(locations.nextElement());
		}
	}

	private void storeTemperatureHumidityDevices(SmartHomeSession session) {
		dbAdapter.deleteAllTemperatureHumidityDevices();
		dbAdapter.deleteAllTemperatureSensors();
		dbAdapter.deleteAllRoomHumidtySensors();
		ConcurrentHashMap<String, TemperatureHumidityDevice> devicesMap = session.getTemperatureHumidityDevices();
		if (devicesMap == null) {
			return;
		}
		Enumeration<TemperatureHumidityDevice> devices = devicesMap.elements();
		while (devices.hasMoreElements()) {
			dbAdapter.insertTemperatureHumidityDevice(devices.nextElement());
		}
	}

	@Override
	protected void onPreExecute() {
		// https://www.google.com/design/spec/components/progress-activity.html#
		// Put the view in a layout if it's not and set
		// android:animateLayoutChanges="true" for that layout.
		progressDialog.setMessage("Aktualisierung der Räume läuft..."); // TODO
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	@Override
	protected Integer doInBackground(String... params) {
		String sessionId = params[0];
		SmartHomeSession session = new SmartHomeSession(sessionId);
		try {
			session.refreshConfiguration();
			storeLocations(session);
			storeTemperatureHumidityDevices(session);
		} catch (SmartHomeSessionExpiredException e) {
			return REFRESH_ERROR;
		}
		return REFRESH_OK;
	}

	@Override
	protected void onPostExecute(Integer resultCode) {

		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (resultCode == REFRESH_ERROR) {
			alertDialogBuilder.setTitle("Aktualisierung"); // TODO
			alertDialogBuilder.setCancelable(true);
			// TODO
			alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			String msg = "Aktualisierung der Räume ist fehlgeschlagen!";
			alertDialogBuilder.setMessage(msg);
			alertDialogBuilder.create().show();
		}

	}

}