package de.codecentric.voicenotes.context;

import android.app.Application;

/**
 * Base class for maintaining global application state and sharing of objects
 * used across the whole application.
 */
public class ApplicationContext extends Application {

	/**
	 * Shared preferences file name.
	 */
	public static final String PREFS_NAME = "VoiceNotesPrefsFile";

	/**
	 * Shared variable - is application already run?
	 */
	public static final String APP_FIRST_RUN_CHECK = "appFirstRunCheck";

	private DatabaseHelper dbHelper;

	/**
	 * Initializes the application context by creating needed elements such as
	 * {@link DatabaseHelper}.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		dbHelper = new DatabaseHelper(getApplicationContext());
	}

	/**
	 * Method returns {@link ApplicationContext.EntityHelper} object. This
	 * should be the only way to access it across whole application.
	 * 
	 * @return {@link ApplicationContext.EntityHelper} object
	 */
	public DatabaseHelper getDatabaseHelper() {
		return dbHelper;
	}

}
