package de.t7soft.android.t7home;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.t7soft.android.t7home.database.HomeDatabaseAdapter;
import de.t7soft.android.t7home.smarthome.api.SmartHomeLocation;
import de.t7soft.android.t7home.smarthome.api.SmartHomeSession;
import de.t7soft.android.t7home.smarthome.api.devices.TemperatureHumidityDevice;
import de.t7soft.android.t7home.smarthome.api.exceptions.SmartHomeSessionExpiredException;

public class RoomsListActivity extends ListActivity {

	public static final String LOCATION_ID = "locationId";

	private String sessionId;

	private static final int REFRESH_OK = 0;
	private static final int REFRESH_ERROR = 1;

	private final List<SmartHomeLocation> locations = new ArrayList<SmartHomeLocation>();
	private HomeDatabaseAdapter dbAdapter;
	private ArrayAdapter<SmartHomeLocation> listAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// TODO menu with logoff, refresh

		sessionId = getIntent().getExtras().getString(MainActivity.SESSION_ID_KEY);

		if (dbAdapter == null) {
			dbAdapter = new HomeDatabaseAdapter(this);
		}

		ListView listView = getListView();
		View header = getLayoutInflater().inflate(R.layout.rooms_list_header, null);
		listView.addHeaderView(header);

		listAdapter = createListAdapter(locations);
		setListAdapter(listAdapter);
		listView.setTextFilterEnabled(true);

		super.onCreate(savedInstanceState);

		// ActionBar actionBar = getActionBar();
		// actionBar.setDisplayHomeAsUpEnabled(true);

	}

	@Override
	protected void onResume() {

		dbAdapter.open();

		refresh();

		super.onResume();

	}

	private void refresh() {
		RefreshTask refreshTask = new RefreshTask(this);
		refreshTask.execute(sessionId);
		updateListAdapter();
	}

	@Override
	protected void onPause() {
		dbAdapter.close();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.rooms, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.about_item:
				AboutDlg aboutDlg = new AboutDlg(this);
				aboutDlg.show();
				return true;
			case R.id.refresh_item:
				refresh();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		int itemPosition = position - getListView().getHeaderViewsCount();
		if (itemPosition >= 0 && itemPosition < getListAdapter().getCount()) {

			SmartHomeLocation location = (SmartHomeLocation) getListAdapter().getItem(itemPosition);

			Intent intent = new Intent(this, RoomActivity.class);
			intent.putExtra(LOCATION_ID, location.getLocationId());
			startActivity(intent);

		}

	}

	private ArrayAdapter<SmartHomeLocation> createListAdapter(final List<SmartHomeLocation> locations) {
		return new ArrayAdapter<SmartHomeLocation>(this, android.R.layout.simple_list_item_1, locations);
	}

	private void updateListAdapter() {

		locations.clear();
		locations.addAll(dbAdapter.getAllLocations());
		listAdapter.notifyDataSetChanged();

	}

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case android.R.id.home:
	// NavUtils.navigateUpFromSameTask(this);
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }

	private class RefreshTask extends AsyncTask<String, Void, Integer> {

		private final ProgressDialog progressDialog;
		private final AlertDialog.Builder alertDialogBuilder;

		public RefreshTask(Context context) {
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

			// TODO refresh list
		}

	}

}
