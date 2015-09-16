package de.t7soft.android.t7home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import de.t7soft.android.t7home.MainActivity.LogonData;
import de.t7soft.android.t7home.smarthome.api.SmartHomeSession;
import de.t7soft.android.t7home.smarthome.api.exceptions.LoginFailedException;
import de.t7soft.android.t7home.smarthome.api.exceptions.SHTechnicalException;
import de.t7soft.android.t7home.smarthome.api.exceptions.SmartHomeSessionExpiredException;

public abstract class AbstractLogonTask extends AsyncTask<LogonData, Void, LogonResult> {

	private final Context context;
	private final int titleId;
	private final ProgressDialog progressDialog;
	private final AlertDialog.Builder alertDialogBuilder;

	public AbstractLogonTask(Context context) {
		this(context, -1);
	}

	public AbstractLogonTask(Context context, int titleId) {
		this.context = context;
		this.titleId = titleId;
		progressDialog = new ProgressDialog(context);
		alertDialogBuilder = new AlertDialog.Builder(context);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// https://www.google.com/design/spec/components/progress-activity.html#
		// Put the view in a layout if it's not and set
		// android:animateLayoutChanges="true" for that layout.
		if (titleId >= 0) {
			progressDialog.setTitle(titleId);
		}
		progressDialog.setMessage(getString(R.string.logon_in_progress));
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	@Override
	protected LogonResult doInBackground(LogonData... params) {

		LogonData logonData = params[0];
		SmartHomeSession session = new SmartHomeSession();
		int resultCode = LogonResult.LOGON_OK;
		try {
			session.logon(logonData.getUsername(), logonData.getPassword(), logonData.getIpAddress());
		} catch (LoginFailedException e) {
			resultCode = LogonResult.LOGON_LOGIN_FAILED;
		} catch (SmartHomeSessionExpiredException e) {
			resultCode = LogonResult.LOGON_SESSION_EXPIRED;
		} catch (SHTechnicalException e) {
			resultCode = LogonResult.LOGON_TECHNICAL_EXCEPTION;
		}

		return new LogonResult(resultCode, session);
	}

	@Override
	protected void onPostExecute(LogonResult result) {

		progressDialog.dismiss();

		if (result.getResultCode() != LogonResult.LOGON_OK) {
			if (titleId >= 0) {
				alertDialogBuilder.setTitle(titleId);
			}
			alertDialogBuilder.setCancelable(true);
			alertDialogBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			int msgResId;
			switch (result.getResultCode()) {
				case LogonResult.LOGON_LOGIN_FAILED:
					msgResId = R.string.logon_error_login_failed;
					break;
				case LogonResult.LOGON_SESSION_EXPIRED:
					msgResId = R.string.logon_error_session_expired;
					break;
				case LogonResult.LOGON_TECHNICAL_EXCEPTION:
					msgResId = R.string.logon_error_technical_exception;
					break;
				default:
					msgResId = R.string.logon_error_unkown;
					break;
			}
			String msg = getString(msgResId);
			alertDialogBuilder.setMessage(msg);
			alertDialogBuilder.create().show();
		}

	}

	private String getString(int resId) {
		return context.getString(resId);
	}

}