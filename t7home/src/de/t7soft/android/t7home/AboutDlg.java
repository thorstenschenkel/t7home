package de.t7soft.android.t7home;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.webkit.WebView;

public class AboutDlg {

	private static final String LOG_TAG = AboutDlg.class.getSimpleName();

	private final Context context;

	public AboutDlg(Context context) {
		this.context = context;
	}

	public void show() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		String infoText = createInfoText();
		WebView wv = new WebView(context);
		wv.loadData(infoText, "text/html", "utf-8");
		wv.setBackgroundColor(context.getResources().getColor(android.R.color.white));
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
			String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			Date buildTime = getBuildTime();
			String dateString = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(buildTime);
			String msgString = context.getString(R.string.main_info_msg);
			msgString = MessageFormat.format(msgString, versionName, dateString);
			return msgString;
		} catch (NameNotFoundException e) {
			Log.e(LOG_TAG, "No infos found!", e);
			return "ERROR";
		}
	}

	private Date getBuildTime() {
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
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
