package de.t7soft.android.t7home;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import de.t7soft.android.t7home.smarthome.api.SmartHomeSession;
import de.t7soft.android.t7home.smarthome.api.exceptions.LoginFailedException;
import de.t7soft.android.t7home.smarthome.api.exceptions.SHTechnicalException;
import de.t7soft.android.t7home.smarthome.api.exceptions.SmartHomeSessionExpiredException;

/**
 * Logon
 * 
 * https://code.google.com/p/smarthome-java-library/
 * 
 * (VPN) http://www.tecchannel.de/kommunikation/handy_pda/2033962/
 * smartphone_android_praxis_vpn_einrichten_und_nutzen/
 * 
 */
public class MainActivity extends Activity {

	static final String SESSION_ID_KEY = "sessionId";

	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	private static final int LOGON_OK = 0;
	private static final int LOGON_LOGIN_FAILED = 1;
	private static final int LOGON_SESSION_EXPIRED = 2;
	private static final int LOGON_TECHNICAL_EXCEPTION = 3;

	private LogonData logonData;
	private LogonTask logonTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		if (logonTask != null) {
			logonTask.cancel(true);
		}

		super.onCreate(savedInstanceState);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
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
			showAboutDlg();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showAboutDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String infoText = createInfoText();
		WebView wv = new WebView(this);
		wv.loadData(infoText, "text/html", "utf-8");
		wv.setBackgroundColor(getResources().getColor(android.R.color.white));
		wv.getSettings().setDefaultTextEncodingName("utf-8");
		builder.setView(wv);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(R.string.app_name);
		builder.setCancelable(true);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.setNegativeButton(null, null);
		AlertDialog alert = builder.create();
		alert.show();
	}

	private String createInfoText() {
		try {
			String versionName = getPackageManager().getPackageInfo(
					getPackageName(), 0).versionName;
			Date buildTime = getBuildTime();
			String dateString = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
					.format(buildTime);
			String msgString = getString(R.string.main_info_msg);
			msgString = MessageFormat
					.format(msgString, versionName, dateString);
			return msgString;
		} catch (NameNotFoundException e) {
			Log.e(LOG_TAG, "No infos found!", e);
			return "ERROR";
		}
	}

	private Date getBuildTime() {
		try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(
					getPackageName(), 0);
			ZipFile zf = new ZipFile(ai.sourceDir);
			ZipEntry ze = zf.getEntry("classes.dex");
			long time = ze.getTime();
			zf.close();
			return new Date(time);
		} catch (Exception e) {
			Log.e(LOG_TAG, "No build time!", e);
			return null;
		}
	}

	private void initView() {

		SharedPreferences sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key_logon),
				Context.MODE_PRIVATE);
		boolean keepInMind = sharedPref.getBoolean(
				getString(R.string.preference_key_logon_keep_in_mind), false);
		final CheckBox checkBoxKeepInMind = (CheckBox) findViewById(R.id.checkBoxKeepInMind);
		checkBoxKeepInMind.setChecked(keepInMind);

		if (keepInMind) {
			String username = sharedPref.getString(
					getString(R.string.preference_key_logon_username), "");
			String password = sharedPref.getString(
					getString(R.string.preference_key_logon_password), "");
			String ipAddress = sharedPref.getString(
					getString(R.string.preference_key_logon_ip_address), "");
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
		// logonTask = new LogonTask();
		// logonTask.execute(logonData);
		goRoomsList(null);
	}

	private void storeLogonPreferences() {

		final EditText editTextUsername = (EditText) findViewById(R.id.editTextUsername);
		String username = editTextUsername.getText().toString();
		final EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		String password = editTextPassword.getText().toString();
		final EditText editTextIpAddress = (EditText) findViewById(R.id.editTextIpAddress);
		String ipAddress = editTextIpAddress.getText().toString();
		logonData = new LogonData(username, password, ipAddress);

		SharedPreferences sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key_logon),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();

		final CheckBox checkBoxKeepInMind = (CheckBox) findViewById(R.id.checkBoxKeepInMind);
		boolean keepInMind = checkBoxKeepInMind.isChecked();
		editor.putBoolean(
				getString(R.string.preference_key_logon_keep_in_mind),
				keepInMind);
		if (keepInMind) {
			editor.putString(getString(R.string.preference_key_logon_username),
					logonData.getUsername());
			editor.putString(getString(R.string.preference_key_logon_password),
					logonData.getPassword());
			editor.putString(
					getString(R.string.preference_key_logon_ip_address),
					logonData.getIpAddress());
		} else {
			editor.remove(getString(R.string.preference_key_logon_username));
			editor.remove(getString(R.string.preference_key_logon_password));
			editor.remove(getString(R.string.preference_key_logon_ip_address));
		}

		editor.commit();
	}

	private class LogonTask extends AsyncTask<LogonData, Void, LogonResult> {

		private ProgressDialog progressDialog;
		private AlertDialog.Builder alertDialogBuilder;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// https://www.google.com/design/spec/components/progress-activity.html#
			// Put the view in a layout if it's not and set
			// android:animateLayoutChanges="true" for that layout.
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setMessage("Anmeldung l�uft..."); // TODO
			progressDialog.show();
			alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
		}

		@Override
		protected LogonResult doInBackground(LogonData... params) {

			SmartHomeSession session = new SmartHomeSession();
			int resultCode = LOGON_OK;
			try {
				session.logon(logonData.getUsername(), logonData.getPassword(),
						logonData.getIpAddress());
			} catch (LoginFailedException e) {
				resultCode = LOGON_LOGIN_FAILED;
			} catch (SmartHomeSessionExpiredException e) {
				resultCode = LOGON_SESSION_EXPIRED;
			} catch (SHTechnicalException e) {
				resultCode = LOGON_TECHNICAL_EXCEPTION;
			}

			return new LogonResult(resultCode, session);
		}

		@Override
		protected void onPostExecute(LogonResult result) {

			progressDialog.dismiss();

			if (result.resultCode == LOGON_OK) {
				goRoomsList(result.session);
			} else {
				alertDialogBuilder.setTitle("Anmeldung"); // TODO
				alertDialogBuilder.setCancelable(true);
				// TODO
				alertDialogBuilder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				String msg;
				switch (result.resultCode) {
				case LOGON_LOGIN_FAILED:
					msg = "Anmeldung ist mit den Anmeldedaten nicht m�glich!"; // TODO
					break;
				case LOGON_SESSION_EXPIRED:
					msg = "Anmeldung ist fehlgeschlagen. Die Session ist abgelaufen."; // TODO
					break;
				case LOGON_TECHNICAL_EXCEPTION:
					msg = "Anmeldung ist fehlgeschlagen. Es ist eine technischer Fehler aufgetreten."; // TODO
					break;
				default:
					msg = "Anmeldung ist fehlgeschlagen. Es ist eine unbekannter Fehler aufgetreten."; // TODO
					break;
				}
				alertDialogBuilder.setMessage(msg);
				alertDialogBuilder.create().show();
			}

		}

	}

	private void goRoomsList(SmartHomeSession session) {
		Intent intent = new Intent(this, RoomsListActivity.class);
		intent.putExtra(SESSION_ID_KEY, session.getSessionId());
		MainActivity.this.startActivity(intent);
	}

	private class LogonResult {

		private int resultCode;
		private SmartHomeSession session;

		public LogonResult(int resultCode, SmartHomeSession session) {
			super();
			this.resultCode = resultCode;
			this.session = session;
		}

		public int getResultCode() {
			return resultCode;
		}

		public void setResultCode(int resultCode) {
			this.resultCode = resultCode;
		}

		public SmartHomeSession getSession() {
			return session;
		}

		public void setSession(SmartHomeSession session) {
			this.session = session;
		}

	}

	private class LogonData {

		private String username;
		private String password;
		private String ipAddress;

		public LogonData() {

		}

		public LogonData(String username, String password, String ipAddress) {
			super();
			this.username = username;
			this.password = password;
			this.ipAddress = ipAddress;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getIpAddress() {
			return ipAddress;
		}

		public void setIpAddress(String ipAddress) {
			this.ipAddress = ipAddress;
		}

	}

}
