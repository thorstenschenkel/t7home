package de.t7soft.android.t7home;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class MainActivity extends Activity {

	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
			String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			Date buildTime = getBuildTime();
			String dateString = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(buildTime);
			String msgString = getString(R.string.main_info_msg);
			msgString = MessageFormat.format(msgString, versionName, dateString);
			return msgString;
		} catch (NameNotFoundException e) {
			Log.e(LOG_TAG, "No infos found!", e);
			return "ERROR";
		}
	}

	private Date getBuildTime() {
		try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), 0);
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

}
