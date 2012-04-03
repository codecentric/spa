package de.codecentric.voicenotes.context;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {

	public static boolean getBooleanPreference(Activity activity, String key) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(activity.getBaseContext());
		return prefs.getBoolean(key, false);
	}

}
