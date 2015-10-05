package de.t7soft.android.t7home.tasks;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import de.t7soft.android.t7home.R;
import de.t7soft.android.t7home.database.HomeDatabaseAdapter;
import de.t7soft.android.t7home.smarthome.api.SmartHomeLocation;
import de.t7soft.android.t7home.smarthome.api.SmartHomeSession;
import de.t7soft.android.t7home.smarthome.api.devices.DaySensor;
import de.t7soft.android.t7home.smarthome.api.devices.TemperatureHumidityDevice;
import de.t7soft.android.t7home.smarthome.api.devices.WindowDoorSensor;
import de.t7soft.android.t7home.smarthome.api.exceptions.SHTechnicalException;
import de.t7soft.android.t7home.smarthome.api.exceptions.SmartHomeSessionExpiredException;

public abstract class AbstractRefreshTask extends AsyncTask<String, Integer, Integer> {

	public static final int REFRESH_OK = 0;
	public static final int REFRESH_ERROR = 1;

	private final ProgressDialog progressDialog;
	private final AlertDialog.Builder alertDialogBuilder;
	private final Context context;
	private final HomeDatabaseAdapter dbAdapter;
	private final int titleId;

	public AbstractRefreshTask(final Context context, final HomeDatabaseAdapter dbAdapter) {
		this(context, dbAdapter, -1);
	}

	public AbstractRefreshTask(final Context context, final HomeDatabaseAdapter dbAdapter, final int titleId) {
		this.context = context;
		this.dbAdapter = dbAdapter;
		this.titleId = titleId;
		progressDialog = new ProgressDialog(context);
		alertDialogBuilder = new AlertDialog.Builder(context);
	}

	private void storeLocations(final SmartHomeSession session) {
		final ConcurrentHashMap<String, SmartHomeLocation> locationsMap = session.getLocations();
		if (locationsMap == null) {
			return;
		}
		final Enumeration<SmartHomeLocation> locations = locationsMap.elements();
		while (locations.hasMoreElements()) {
			dbAdapter.insertLocation(locations.nextElement());
		}
	}

	private void storeTemperatureHumidityDevices(final SmartHomeSession session) {
		final ConcurrentHashMap<String, TemperatureHumidityDevice> devicesMap = session.getTemperatureHumidityDevices();
		if (devicesMap == null) {
			return;
		}
		final Enumeration<TemperatureHumidityDevice> devices = devicesMap.elements();
		while (devices.hasMoreElements()) {
			dbAdapter.insertTemperatureHumidityDevice(devices.nextElement());
		}
	}

	private void storeWindowDoorSensors(final SmartHomeSession session) {
		final ConcurrentHashMap<String, WindowDoorSensor> devicesMap = session.getWindowDoorSensors();
		if (devicesMap == null) {
			return;
		}
		final Enumeration<WindowDoorSensor> devices = devicesMap.elements();
		while (devices.hasMoreElements()) {
			dbAdapter.insertWindowDoorSensor(devices.nextElement());
		}
	}

	private void storeDaySensors(final SmartHomeSession session) {
		final ConcurrentHashMap<String, DaySensor> devicesMap = session.getDaySensors();
		if (devicesMap == null) {
			return;
		}
		final Enumeration<DaySensor> devices = devicesMap.elements();
		while (devices.hasMoreElements()) {
			dbAdapter.insertDaySensor(devices.nextElement());
		}
	}

	@Override
	protected void onPreExecute() {
		// https://www.google.com/design/spec/components/progress-activity.html#
		// Put the view in a layout if it's not and set
		// android:animateLayoutChanges="true" for that layout.
		if (titleId >= 0) {
			progressDialog.setTitle(titleId);
		}
		progressDialog.setMessage(context.getString(R.string.refresh_in_progress));
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	@Override
	protected void onProgressUpdate(final Integer... resIds) {
		context.getString(R.string.refresh_in_progress);
		progressDialog.setMessage(context.getString(R.string.refresh_in_progress));
	}

	@Override
	protected Integer doInBackground(final String... params) {
		final String sessionId = params[0];
		final SmartHomeSession session = new SmartHomeSession(sessionId);
		try {
			publishProgress(R.string.refresh_configuration);
			session.refreshConfiguration();
			publishProgress(R.string.refresh_states);
			session.refreshLogicalDeviceState();
			publishProgress(R.string.refresh_store);
			dbAdapter.deleteAll();
			storeLocations(session);
			storeTemperatureHumidityDevices(session);
			storeWindowDoorSensors(session);
			storeDaySensors(session);
		} catch (final SHTechnicalException e) {
			return REFRESH_ERROR;
		} catch (final SmartHomeSessionExpiredException e) {
			return REFRESH_ERROR;
		}
		return REFRESH_OK;
	}

	@Override
	protected void onPostExecute(final Integer resultCode) {

		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (resultCode == REFRESH_ERROR) {
			if (titleId >= 0) {
				alertDialogBuilder.setTitle(titleId);
			}
			alertDialogBuilder.setCancelable(true);
			alertDialogBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int id) {
					dialog.cancel();
				}
			});
			alertDialogBuilder.setMessage(R.string.refresh_error);
			alertDialogBuilder.create().show();
		}

	}

}