package de.t7soft.android.t7home;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import de.t7soft.android.t7home.smarthome.api.SmartHomeSession;
import de.t7soft.android.t7home.smarthome.api.exceptions.SmartHomeSessionExpiredException;

public class RoomsListActivity extends ListActivity {

	private String sessionId;

	private static final int REFRESH_OK = 0;
	private static final int REFRESH_ERROR = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// TODO menu with logoff, refresh

		sessionId = getIntent().getExtras().getString(MainActivity.SESSION_ID_KEY);

		ListView listView = getListView();
		View header = getLayoutInflater().inflate(R.layout.rooms_list_header, null);
		listView.addHeaderView(header);

		super.onCreate(savedInstanceState);

		// ActionBar actionBar = getActionBar();
		// actionBar.setDisplayHomeAsUpEnabled(true);

	}

	@Override
	protected void onResume() {

		super.onResume();

		RefreshTask refreshTask = new RefreshTask();
		refreshTask.execute(sessionId);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		int itemPosition = position - getListView().getHeaderViewsCount();
		if (itemPosition >= 0 && itemPosition < getListAdapter().getCount()) {
			// TODO
		}
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

		private ProgressDialog progressDialog;
		private AlertDialog.Builder alertDialogBuilder;

		@Override
		protected void onPreExecute() {
			// https://www.google.com/design/spec/components/progress-activity.html#
			// Put the view in a layout if it's not and set
			// android:animateLayoutChanges="true" for that layout.
			progressDialog = new ProgressDialog(RoomsListActivity.this);
			progressDialog.setMessage("Aktualisierung der Räume läuft..."); // TODO
			progressDialog.show();
			alertDialogBuilder = new AlertDialog.Builder(RoomsListActivity.this);
		}

		@Override
		protected Integer doInBackground(String... params) {
			String sessionId = params[0];
			SmartHomeSession session = new SmartHomeSession(sessionId);
			try {
				session.refreshConfiguration();
				// TODO store Locations etc. in data base
			} catch (SmartHomeSessionExpiredException e) {
				return REFRESH_ERROR;
			}
			return REFRESH_OK;
		}

		@Override
		protected void onPostExecute(Integer resultCode) {
			progressDialog.dismiss();

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
