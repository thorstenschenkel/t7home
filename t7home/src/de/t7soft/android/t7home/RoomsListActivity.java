package de.t7soft.android.t7home;

import java.util.ArrayList;
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
import de.t7soft.android.t7home.database.HomeDatabaseAdapter;
import de.t7soft.android.t7home.smarthome.api.SmartHomeLocation;

public class RoomsListActivity extends ListActivity {

	public static final String LOCATION_ID = "locationId";

	private String sessionId;

	private final List<SmartHomeLocation> locations = new ArrayList<SmartHomeLocation>();
	private HomeDatabaseAdapter dbAdapter;
	private ArrayAdapter<SmartHomeLocation> listAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// TODO menu with logoff

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
		RefreshTask refreshTask = new RefreshTask(this, dbAdapter);
		refreshTask.execute(sessionId);
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

	private class RefreshTask extends AbstractRefreshTask {

		public RefreshTask(Context context, HomeDatabaseAdapter dbAdapter) {
			super(context, dbAdapter);
		}

		@Override
		protected void onPostExecute(Integer resultCode) {
			super.onPostExecute(resultCode);
			updateListAdapter();
		}

	}

}
