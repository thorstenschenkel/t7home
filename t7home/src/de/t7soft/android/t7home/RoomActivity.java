package de.t7soft.android.t7home;

import android.app.ListActivity;
import android.os.Bundle;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {

		loactionId = getIntent().getExtras().getString(RoomsListActivity.LOCATION_ID);

		if (dbAdapter == null) {
			dbAdapter = new HomeDatabaseAdapter(this);
		}

		super.onCreate(savedInstanceState);
	}

}
