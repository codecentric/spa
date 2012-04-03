package de.codecentric.voicenotes;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Activity presenting application preferences.
 */
public class PreferencesActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.voice_notes_prefs);
	}

}
