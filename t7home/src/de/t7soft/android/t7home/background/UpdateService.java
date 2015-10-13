package de.t7soft.android.t7home.background;

import java.util.List;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import de.t7soft.android.t7home.smarthome.api.devices.LogicalDevice;

public class UpdateService extends IntentService {

	private static final String LOGTAG = UpdateService.class.getSimpleName();
	private static final String MESSENGER = "messenger";

	public UpdateService() {
		super("UpdateService");
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		Log.i(LOGTAG, "Service running");

		final List<LogicalDevice> changedDevices = null;

		final Bundle extras = intent.getExtras();
		if (extras != null) {
			// TODO
			// SmartHomeSession....
		}

		if (changedDevices != null) {
			if (extras != null) {
				final Messenger messenger = (Messenger) extras.get("MESSENGER");
				try {
					final Message msg = Message.obtain();
					msg.arg1 = Activity.RESULT_OK;
					msg.obj = changedDevices;
					messenger.send(msg);
				} catch (final android.os.RemoteException ex) {
					Log.w(LOGTAG, "Exception sending message", ex);
				}
			}
		}

	}
}
