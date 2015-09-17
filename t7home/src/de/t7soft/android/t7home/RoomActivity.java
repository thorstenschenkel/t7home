package de.t7soft.android.t7home;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import de.t7soft.android.t7home.database.HomeDatabaseAdapter;

/**
 * http://stackoverflow.com/questions/4777272/android-listview-with-different-layout-for-each-row
 * 
 * http://www.fancyicons.com/frei-ikonen/232/mabeinheiten-icon-set/frei-temperatur-icon-png/ http://www.iconsdb.com/green-icons/thermometer-2-icon.html http://www.sjoarafting.de/a/i/temperatur.png
 * http://www.shutterstock.com/pic.mhtml?irgwc=1&utm_medium=Affiliate&language=de&utm_campaign=FindIcons.com&utm_source=38925&id=69743743&tpl=38925-42764
 * 
 * http://www.iconarchive.com/show/android-icons-by-icons8/Measurement-Units-Humidity-icon.html http://findicons.com/icon/557445/humidity http://www.iconarchive.com/show/outline-icons-by-iconsmind/Rain-Drop-icon.html
 * http://www.shutterstock.com/pic.mhtml?language=de&irgwc=1&id=96107042&utm_source=38925&tpl=38925-42764&utm_medium=Affiliate&utm_campaign=FindIcons.com
 * http://www.shutterstock.com/pic.mhtml?utm_campaign=FindIcons.com&irgwc=1&language=de&utm_medium=Affiliate&utm_source=38925&tpl=38925-42764&id=102080212
 * 
 * 
 */
public class RoomActivity extends ListActivity {

	private HomeDatabaseAdapter dbAdapter;
	private Object loactionId;
	private String sessionId;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		loactionId = getIntent().getExtras().getString(RoomsListActivity.LOCATION_ID);
		sessionId = getIntent().getExtras().getString(MainActivity.SESSION_ID_KEY);

		if (dbAdapter == null) {
			dbAdapter = new HomeDatabaseAdapter(this);
		}

		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

	}

	@Override
	protected void onResume() {
		dbAdapter.open();
		super.onResume();
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

	private void refresh() {
		RefreshTask refreshTask = new RefreshTask(this, dbAdapter);
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
				LogoutTask logoutTask = new LogoutTask(this, dbAdapter);
				logoutTask.execute(sessionId);
				return true;
			case R.id.about_item:
				AboutDlg aboutDlg = new AboutDlg(this);
				aboutDlg.show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void logout() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	private class RefreshTask extends AbstractRefreshTask {

		public RefreshTask(Context context, HomeDatabaseAdapter dbAdapter) {
			super(context, dbAdapter, R.string.room_subtitle);
		}

		@Override
		protected void onPostExecute(Integer resultCode) {
			super.onPostExecute(resultCode);
			// updateListAdapter(); TODO
		}

	}

	private class LogoutTask extends AbstractLogoutTask {

		public LogoutTask(Context context, HomeDatabaseAdapter dbAdapter) {
			super(context, dbAdapter, R.string.logout_subtitle);
		}

		@Override
		protected void onPostExecute(Integer resultCode) {
			super.onPostExecute(resultCode);
			logout();
		}

	}

}
