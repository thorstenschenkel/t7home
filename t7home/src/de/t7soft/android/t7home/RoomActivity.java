package de.t7soft.android.t7home;

import android.app.ListActivity;
import android.os.Bundle;
import de.t7soft.android.t7home.database.HomeDatabaseAdapter;

/**
 * http://stackoverflow.com/questions/4777272/android-listview-with-different-
 * layout-for-each-row
 * 
 * @author tsc
 * 
 */
public class RoomActivity extends ListActivity {

	private HomeDatabaseAdapter dbAdapter;
	private Object loactionId;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		loactionId = getIntent().getExtras().getString(
				RoomsListActivity.LOCATION_ID);

		if (dbAdapter == null) {
			dbAdapter = new HomeDatabaseAdapter(this);
		}

		super.onCreate(savedInstanceState);
	}

}
