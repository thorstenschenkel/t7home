package de.t7soft.android.t7home;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class RoomsListActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		ListView listView = getListView();
		View header = getLayoutInflater().inflate(R.layout.rooms_list_header,
				null);
		listView.addHeaderView(header);

		super.onCreate(savedInstanceState);

		// ActionBar actionBar = getActionBar();
		// actionBar.setDisplayHomeAsUpEnabled(true);

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

}
