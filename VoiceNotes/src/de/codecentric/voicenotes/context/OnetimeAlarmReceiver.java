package de.codecentric.voicenotes.context;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;
import de.codecentric.voicenotes.R;
import de.codecentric.voicenotes.TextualNoteActivity;

public class OnetimeAlarmReceiver extends BroadcastReceiver {

	public static final int TEXTUAL_NOTE_NOTIFICATION_ID = 1;

	@Override
	public void onReceive(Context context, Intent intent) {
		// read extras in order to crete notification
		Bundle extras = intent.getExtras();
		String notificationText = extras
				.getString(Constants.EXTRA_NOTIFICATION_TEXT);
		String notificationTitle = extras
				.getString(Constants.EXTRA_NOTIFICATION_TITLE);
		String notificationActivity = extras
				.getString(Constants.EXTRA_NOTIFICATION_ACTIVITY_NAME);

		@SuppressWarnings("rawtypes")
		Class cls = null;
		if ("TextualNoteActivity".equals(notificationActivity)) {
			cls = TextualNoteActivity.class;
		}

		Toast.makeText(context, notificationText, Toast.LENGTH_LONG).show();

		Vibrator v = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		if (v != null) {
			v.vibrate(1000);
		}

		if (cls != null) {
			Intent notificationIntent = new Intent(context, cls);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			// copy extras in order to start activity with same data when
			// notification icon is clicked
			notificationIntent.putExtras(extras);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			// the next two lines initialize the Notification, using the
			// configurations above
			Notification notification = new Notification(
					R.drawable.notification_icon, notificationTitle,
					System.currentTimeMillis());
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.setLatestEventInfo(context, notificationTitle,
					notificationText, contentIntent);

			NotificationManager manager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			manager.notify(TEXTUAL_NOTE_NOTIFICATION_ID, notification);
		}
	}
}
