package de.t7soft.android.t7home;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import de.t7soft.android.t7home.database.HomeDatabaseAdapter;
import de.t7soft.android.t7home.smarthome.api.SmartHomeSession;

public abstract class AbstractLogoutTask extends AsyncTask<String, Void, Integer> {

	public static final int LOGOUT_OK = 0;

	private final ProgressDialog progressDialog;
	private final Context context;
	private final HomeDatabaseAdapter dbAdapter;
	private final int titleId;

	public AbstractLogoutTask(Context context, HomeDatabaseAdapter dbAdapter) {
		this(context, dbAdapter, -1);
	}

	public AbstractLogoutTask(Context context, HomeDatabaseAdapter dbAdapter, int titleId) {
		this.context = context;
		this.dbAdapter = dbAdapter;
		this.titleId = titleId;
		progressDialog = new ProgressDialog(context);
	}

	@Override
	protected void onPreExecute() {
		// https://www.google.com/design/spec/components/progress-activity.html#
		// Put the view in a layout if it's not and set
		// android:animateLayoutChanges="true" for that layout.
		if (titleId >= 0) {
			progressDialog.setTitle(titleId);
		}
		progressDialog.setMessage(context.getString(R.string.refresh_in_progress)); // TODO
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	@Override
	protected Integer doInBackground(String... params) {
		String sessionId = params[0];
		SmartHomeSession session = new SmartHomeSession(sessionId);
		session.destroy();
		dbAdapter.deleteAll();
		return LOGOUT_OK;
	}

	@Override
	protected void onPostExecute(Integer resultCode) {

		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

	}

}
