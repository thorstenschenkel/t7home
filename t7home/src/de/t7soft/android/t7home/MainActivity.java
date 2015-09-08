package de.t7soft.android.t7home;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends Activity {

	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// http://www.easyinfogeek.com/2015/02/android-example-ip-address-input-control.html
		EditText ipAddress = (EditText) findViewById(R.id.editTextIpAddress);
		InputFilter[] filters = new InputFilter[1];
		filters[0] = new IpAddressInputFilter();
		ipAddress.setFilters(filters);

		initView();

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
		checkBoxKeepInMind.setSelected(keepInMind);

		if (keepInMind) {
			String username = sharedPref.getString(
					getString(R.string.preference_key_logon_username), "");
			String password = sharedPref.getString(
					getString(R.string.preference_key_logon_password), "");
			String ipAddress = sharedPref.getString(
					getString(R.string.preference_key_logon_ip_address), "");
			final EditText editTextUsername = (EditText) findViewById(R.id.editTextUsername);
			editTextUsername.setText(username);
			final EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);
			editTextPassword.setText(password);
			final EditText editTextIpAddress = (EditText) findViewById(R.id.editTextIpAddress);
			editTextIpAddress.setText(ipAddress);
		}

	}

	public void onLogon(View view) {

		final EditText editTextUsername = (EditText) findViewById(R.id.editTextUsername);
		String username = editTextUsername.getText().toString();
		final EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		String password = editTextPassword.getText().toString();
		final EditText editTextIpAddress = (EditText) findViewById(R.id.editTextIpAddress);
		String ipAddress = editTextIpAddress.getText().toString();

		SharedPreferences sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key_logon),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();

		final CheckBox checkBoxKeepInMind = (CheckBox) findViewById(R.id.checkBoxKeepInMind);
		boolean keepInMind = checkBoxKeepInMind.isSelected();
		editor.putBoolean(
				getString(R.string.preference_key_logon_keep_in_mind),
				keepInMind);
		if (keepInMind) {
			editor.putString(getString(R.string.preference_key_logon_username),
					username);
			editor.putString(getString(R.string.preference_key_logon_password),
					password);
			editor.putString(
					getString(R.string.preference_key_logon_ip_address),
					ipAddress);
		} else {
			editor.remove(getString(R.string.preference_key_logon_username));
			editor.remove(getString(R.string.preference_key_logon_password));
			editor.remove(getString(R.string.preference_key_logon_ip_address));
		}

		editor.commit();

		// TODO

	}

}
