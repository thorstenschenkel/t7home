package de.t7soft.android.t7home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import de.t7soft.android.t7home.database.HomeDatabaseAdapter;
import de.t7soft.android.t7home.roomsactivity.RoomsListActivity;
import de.t7soft.android.t7home.smarthome.api.SmartHomeSession;
import de.t7soft.android.t7home.tasks.AbstractLogonTask;

/**
 * Logon
 * 
 * https://code.google.com/p/smarthome-java-library/
 * 
 * (VPN) http://www.tecchannel.de/kommunikation/handy_pda/2033962/ smartphone_android_praxis_vpn_einrichten_und_nutzen/
 * 
 */
public class MainActivity extends Activity {

	public static final String SESSION_ID_KEY = "sessionId";

	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	private LogonData logonData;
	private LogonTask logonTask;
	private HomeDatabaseAdapter dbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		if (logonTask != null) {
			logonTask.cancel(true);
		}
		if (dbAdapter == null) {
			dbAdapter = new HomeDatabaseAdapter(this);
		}
		super.onCreate(savedInstanceState);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		setContentView(R.layout.activity_main);

		// http://www.easyinfogeek.com/2015/02/android-example-ip-address-input-control.html
		EditText ipAddress = (EditText) findViewById(R.id.editTextIpAddress);
		InputFilter[] filters = new InputFilter[1];
		filters[0] = new IpAddressInputFilter();
		ipAddress.setFilters(filters);

		initView();

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
	protected void onStop() {
		if (logonTask != null) {
			logonTask.cancel(true);
		}
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.about_item:
				AboutDlg aboutDlg = new AboutDlg(this);
				aboutDlg.show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void initView() {

		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key_logon),
				Context.MODE_PRIVATE);
		boolean keepInMind = sharedPref.getBoolean(getString(R.string.preference_key_logon_keep_in_mind), false);
		final CheckBox checkBoxKeepInMind = (CheckBox) findViewById(R.id.checkBoxKeepInMind);
		checkBoxKeepInMind.setChecked(keepInMind);

		if (keepInMind) {
			String username = sharedPref.getString(getString(R.string.preference_key_logon_username), "");
			String password = sharedPref.getString(getString(R.string.preference_key_logon_password), "");
			String ipAddress = sharedPref.getString(getString(R.string.preference_key_logon_ip_address), "");
			logonData = new LogonData(username, password, ipAddress);
			final EditText editTextUsername = (EditText) findViewById(R.id.editTextUsername);
			editTextUsername.setText(logonData.getUsername());
			final EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);
			editTextPassword.setText(logonData.getPassword());
			final EditText editTextIpAddress = (EditText) findViewById(R.id.editTextIpAddress);
			editTextIpAddress.setText(logonData.getIpAddress());
		} else {
			logonData = new LogonData();
		}

	}

	public void onLogon(View view) {

		// Store Logon preferences

		storeLogonPreferences();

		// Logon

		if (logonTask != null) {
			logonTask.cancel(true);
		}
		logonTask = new LogonTask(this);
		logonTask.execute(logonData);
	}

	private void storeLogonPreferences() {

		final EditText editTextUsername = (EditText) findViewById(R.id.editTextUsername);
		String username = editTextUsername.getText().toString();
		final EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		String password = editTextPassword.getText().toString();
		final EditText editTextIpAddress = (EditText) findViewById(R.id.editTextIpAddress);
		String ipAddress = editTextIpAddress.getText().toString();
		logonData = new LogonData(username, password, ipAddress);

		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key_logon),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();

		final CheckBox checkBoxKeepInMind = (CheckBox) findViewById(R.id.checkBoxKeepInMind);
		boolean keepInMind = checkBoxKeepInMind.isChecked();
		editor.putBoolean(getString(R.string.preference_key_logon_keep_in_mind), keepInMind);
		if (keepInMind) {
			editor.putString(getString(R.string.preference_key_logon_username), logonData.getUsername());
			editor.putString(getString(R.string.preference_key_logon_password), logonData.getPassword());
			editor.putString(getString(R.string.preference_key_logon_ip_address), logonData.getIpAddress());
		} else {
			editor.remove(getString(R.string.preference_key_logon_username));
			editor.remove(getString(R.string.preference_key_logon_password));
			editor.remove(getString(R.string.preference_key_logon_ip_address));
		}

		editor.commit();
	}

	private void goRoomsList(SmartHomeSession session) {
		Intent intent = new Intent(this, RoomsListActivity.class);
		intent.putExtra(SESSION_ID_KEY, session.getSessionId());
		MainActivity.this.startActivity(intent);
	}

	private class LogonTask extends AbstractLogonTask {

		public LogonTask(Context context) {
			super(context, R.string.logon_subtitle);
		}

		@Override
		protected void onPostExecute(LogonResult result) {
			super.onPostExecute(result);
			if (result.getResultCode() == LogonResult.LOGON_OK) {
				dbAdapter.deleteAll();
				goRoomsList(result.getSession());
				finish();
			}
		}
	}

}
