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

	private final ProgressDialog progressDialog;
	private final AlertDialog.Builder alertDialogBuilder;

	public AbstractLogonTask(Context context) {
		progressDialog = new ProgressDialog(context);
		alertDialogBuilder = new AlertDialog.Builder(context);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// https://www.google.com/design/spec/components/progress-activity.html#
		// Put the view in a layout if it's not and set
		// android:animateLayoutChanges="true" for that layout.
		progressDialog.setMessage("Anmeldung läuft..."); // TODO
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
			alertDialogBuilder.setTitle("Anmeldung"); // TODO
			alertDialogBuilder.setCancelable(true);
			// TODO
			alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			String msg;
			switch (result.getResultCode()) {
				case LogonResult.LOGON_LOGIN_FAILED:
					msg = "Anmeldung ist mit den Anmeldedaten nicht möglich!"; // TODO
					break;
				case LogonResult.LOGON_SESSION_EXPIRED:
					msg = "Anmeldung ist fehlgeschlagen. Die Session ist abgelaufen."; // TODO
					break;
				case LogonResult.LOGON_TECHNICAL_EXCEPTION:
					msg = "Anmeldung ist fehlgeschlagen. Es ist ein technischer Fehler aufgetreten."; // TODO
					break;
				default:
					msg = "Anmeldung ist fehlgeschlagen. Es ist ein unbekannter Fehler aufgetreten."; // TODO
					break;
			}
			alertDialogBuilder.setMessage(msg);
			alertDialogBuilder.create().show();
		}

	}

}