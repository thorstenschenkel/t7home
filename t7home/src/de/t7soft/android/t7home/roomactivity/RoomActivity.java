package de.t7soft.android.t7home.roomactivity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import de.t7soft.android.t7home.AboutDlg;
import de.t7soft.android.t7home.MainActivity;
import de.t7soft.android.t7home.R;
import de.t7soft.android.t7home.database.HomeDatabaseAdapter;
import de.t7soft.android.t7home.database.IDatabaseUpdateListener;
import de.t7soft.android.t7home.roomsactivity.RoomsListActivity;
import de.t7soft.android.t7home.smarthome.api.SmartHomeLocation;
import de.t7soft.android.t7home.smarthome.api.devices.LogicalDevice;
import de.t7soft.android.t7home.smarthome.api.devices.TemperatureHumidityDevice;
import de.t7soft.android.t7home.tasks.AbstractLogoutTask;
import de.t7soft.android.t7home.tasks.AbstractRefreshTask;

/**
 * http://stackoverflow.com/questions/4777272/android-listview-with-different-layout-for-each-row
 * 
 * http://www.fancyicons.com/frei-ikonen/232/mabeinheiten-icon-set/frei-temperatur-icon-png/ http://www.iconsdb.com/green-icons/thermometer-2-icon.html http://www.sjoarafting.de/a/i/temperatur.png http://www.shutterstock
 * .com/pic.mhtml?irgwc=1&utm_medium=Affiliate&language=de&utm_campaign=FindIcons.com&utm_source=38925& id=69743743&tpl=38925-42764
 * 
 * http://www.iconarchive.com/show/android-icons-by-icons8/Measurement-Units-Humidity-icon.html http://findicons.com/icon/557445/humidity http://www.iconarchive.com/show/outline-icons-by-iconsmind/Rain-Drop-icon.html
 * http://www.shutterstock.com/pic.mhtml?language=de&irgwc=1&id=96107042&utm_source=38925&tpl=38925-42764&utm_medium=Affiliate&utm_campaign=FindIcons.com
 * http://www.shutterstock.com/pic.mhtml?utm_campaign=FindIcons.com&irgwc=1&language=de&utm_medium=Affiliate&utm_source=38925&tpl=38925-42764&id=102080212
 * 
 * 
 */
public class RoomActivity extends ListActivity {

	private HomeDatabaseAdapter dbAdapter;
	private String locationId;
	private String sessionId;
	private final List<Object> devices = new ArrayList<Object>();
	private RoomListAdapter listAdapter;
	private TextView textViewRoomListHeader;

	@Override
	public void onCreate(final Bundle savedInstanceState) {

		locationId = getIntent().getExtras().getString(RoomsListActivity.LOCATION_ID);
		sessionId = getIntent().getExtras().getString(MainActivity.SESSION_ID_KEY);

		if (dbAdapter == null) {
			dbAdapter = new HomeDatabaseAdapter(this);
			dbAdapter.addUpdateListener(new DbUpdateListener());
		}

		final ListView listView = getListView();
		final View header = getLayoutInflater().inflate(R.layout.room_list_header, null);
		textViewRoomListHeader = (TextView) header.findViewById(R.id.textViewRoomListHeader);
		listView.addHeaderView(header);

		final RoomActuatorChangeListener changeListener = new RoomActuatorChangeListener(this, sessionId, dbAdapter);
		listAdapter = new RoomListAdapter(this, devices, changeListener);
		setListAdapter(listAdapter);

		super.onCreate(savedInstanceState);

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

	}

	@Override
	protected void onResume() {
		dbAdapter.open();
		updateListAdapter();
		super.onResume();
	}

	@Override
	protected void onPause() {
		dbAdapter.close();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.rooms, menu);
		return true;
	}

	private void updateListAdapter() {

		devices.clear();

		SmartHomeLocation location = dbAdapter.getLocation(locationId);

		String subTitle = getString(R.string.room_subtitle);
		if ((location != null) && (location.getName() != null) && !location.getName().isEmpty()) {
			subTitle += ": " + location.getName();
			textViewRoomListHeader.setText(subTitle);
		} else {
			location = new SmartHomeLocation();
			location.setLocationId(subTitle);
		}

		devices.addAll(dbAdapter.getAllDevices(location));
		listAdapter.notifyDataSetChanged();

	}

	private void refresh() {
		final RefreshTask refreshTask = new RefreshTask(this, dbAdapter);
		refreshTask.execute(sessionId);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.refresh_item:
				refresh();
				return true;
			case R.id.logout_item:
				final LogoutTask logoutTask = new LogoutTask(this, dbAdapter);
				logoutTask.execute(sessionId);
				return true;
			case R.id.about_item:
				final AboutDlg aboutDlg = new AboutDlg(this);
				aboutDlg.show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void logout() {
		final Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	private class RefreshTask extends AbstractRefreshTask {

		public RefreshTask(final Context context, final HomeDatabaseAdapter dbAdapter) {
			super(context, dbAdapter, R.string.room_subtitle);
		}

		@Override
		protected void onPostExecute(final Integer resultCode) {
			super.onPostExecute(resultCode);
			updateListAdapter();
		}

	}

	private class LogoutTask extends AbstractLogoutTask {

		public LogoutTask(final Context context, final HomeDatabaseAdapter dbAdapter) {
			super(context, dbAdapter, R.string.logout_subtitle);
		}

		@Override
		protected void onPostExecute(final Integer resultCode) {
			super.onPostExecute(resultCode);
			logout();
		}

	}

	private class DbUpdateListener implements IDatabaseUpdateListener {

		@Override
		public void updated(final LogicalDevice logicalDevice) {

			boolean update = false;
			final String logicalDeviceId = logicalDevice.getDeviceId();
			for (final Object device : devices) {
				if (device instanceof LogicalDevice) {
					if (((LogicalDevice) device).getDeviceId().equals(logicalDeviceId)) {
						update = true;
						break;
					}
				}
				if (device instanceof TemperatureHumidityDevice) {
					final TemperatureHumidityDevice thDevice = (TemperatureHumidityDevice) device;
					if (thDevice.getRoomHumiditySensor().getDeviceId().equals(logicalDeviceId)) {
						update = true;
						break;
					}
					if (thDevice.getTemperatureSensor().getDeviceId().equals(logicalDeviceId)) {
						update = true;
						break;
					}
					if (thDevice.getTemperatureActuator().getDeviceId().equals(logicalDeviceId)) {
						update = true;
						break;
					}
				}
			}
			if (update) {
				updateListAdapter();
			}

		}

	}

}
