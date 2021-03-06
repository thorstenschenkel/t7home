package de.t7soft.android.t7home.roomsactivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.t7soft.android.t7home.AboutDlg;
import de.t7soft.android.t7home.MainActivity;
import de.t7soft.android.t7home.R;
import de.t7soft.android.t7home.database.HomeDatabaseAdapter;
import de.t7soft.android.t7home.roomactivity.RoomActivity;
import de.t7soft.android.t7home.smarthome.api.SmartHomeLocation;
import de.t7soft.android.t7home.tasks.AbstractLogoutTask;
import de.t7soft.android.t7home.tasks.AbstractRefreshTask;

public class RoomsListActivity extends ListActivity {

	public static final String LOCATION_ID = "locationId";

	private String sessionId;
	private HomeDatabaseAdapter dbAdapter;
	private final List<SmartHomeLocation> locations = new ArrayList<SmartHomeLocation>();
	private ArrayAdapter<SmartHomeLocation> listAdapter;

	@Override
	public void onCreate(final Bundle savedInstanceState) {

		// TODO: null after "back"
		sessionId = getIntent().getExtras().getString(MainActivity.SESSION_ID_KEY);

		if (dbAdapter == null) {
			dbAdapter = new HomeDatabaseAdapter(this);
		}

		final ListView listView = getListView();
		final View header = getLayoutInflater().inflate(R.layout.rooms_list_header, null);
		listView.addHeaderView(header);

		listAdapter = createListAdapter(locations);
		setListAdapter(listAdapter);
		// listView.setTextFilterEnabled(true);

		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onResume() {

		dbAdapter.open();

		if (dbAdapter.getLocationsCount() == 0) {
			refresh();
		}

		super.onResume();

	}

	private void refresh() {
		final RefreshTask refreshTask = new RefreshTask(this, dbAdapter);
		refreshTask.execute(sessionId);
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

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
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

	@Override
	protected void onListItemClick(final ListView l, final View v, final int position, final long id) {

		final int itemPosition = position - getListView().getHeaderViewsCount();
		if ((itemPosition >= 0) && (itemPosition < getListAdapter().getCount())) {

			final SmartHomeLocation location = (SmartHomeLocation) getListAdapter().getItem(itemPosition);

			final Intent intent = new Intent(this, RoomActivity.class);
			intent.putExtra(LOCATION_ID, location.getLocationId());
			intent.putExtra(MainActivity.SESSION_ID_KEY, sessionId);
			startActivity(intent);

		}

	}

	private ArrayAdapter<SmartHomeLocation> createListAdapter(final List<SmartHomeLocation> locations) {
		return new ArrayAdapter<SmartHomeLocation>(this, android.R.layout.simple_list_item_1, locations);
	}

	private void updateListAdapter() {

		locations.clear();
		locations.addAll(dbAdapter.getAllLocations());
		Collections.sort(locations, new Comparator<SmartHomeLocation>() {
			@Override
			public int compare(final SmartHomeLocation location1, final SmartHomeLocation location2) {
				return location1.getPosition().compareTo(location2.getPosition());
			}
		});
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

	private class RefreshTask extends AbstractRefreshTask {

		public RefreshTask(final Context context, final HomeDatabaseAdapter dbAdapter) {
			super(context, dbAdapter, R.string.rooms_subtitle);
		}

		@Override
		protected void onPostExecute(final Integer resultCode) {
			super.onPostExecute(resultCode);
			updateListAdapter();
		}

		@Override
		public void gotoLogin() {
			logout();
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

}
