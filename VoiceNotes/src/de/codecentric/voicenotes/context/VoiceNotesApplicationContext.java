package de.codecentric.voicenotes.context;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import de.codecentric.spa.ctx.PersistenceApplicationContext;
import de.codecentric.spa.metadata.EntityMetaData;
import de.codecentric.spa.sql.SQLGenerator;
import de.codecentric.spa.sql.SQLGenerator.SQLStatements;
import de.codecentric.voicenotes.R;

/**
 * Base class for maintaining global application state and sharing of objects
 * used across the whole application.
 */
public class VoiceNotesApplicationContext extends PersistenceApplicationContext {

	/**
	 * Shared preferences file name.
	 */
	public static final String PREFS_NAME = "VoiceNotesPrefsFile";

	/**
	 * Shared variable - is application already run?
	 */
	public static final String APP_FIRST_RUN_CHECK = "appFirstRunCheck";

	/**
	 * Overridden method. Applications should override this method in order to
	 * trigger scanning of persistent classes and instantiate custom database
	 * helper class (class extending the {@link SQLiteOpenHelper}).
	 * 
	 * NOTE: database helper must be instantiated in this method but AFTER the
	 * scanning process is complete.
	 * 
	 * @see Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		try {
			// Names of classes are read from application resources, for
			// example. It can be done differently, also.
			String[] clsNames = getResources().getStringArray(R.array.persistent_classes);

			// first do the scan...
			if (clsNames != null && clsNames.length != 0) {
				for (String className : clsNames) {
					Class<?> cls = Class.forName(className);
					inspectClass(cls);
				}
			}

			// ... generate SQL for scanned classes...
			Class<?>[] classes = entityMetaDataProvider.getPersistentClasses();
			if (classes != null && classes.length > 0) {
				for (Class<?> cls : classes) {
					EntityMetaData metaData = entityMetaDataProvider.getMetaData(cls);
					if (metaData != null && metaData.hasStructure()) {
						SQLStatements sql = SQLGenerator.generateSQL(metaData);
						sqlProvider.addSQL(cls, sql);
					}
				}
			}

			// ... and then instantiate database helper
			dbHelper = new VoiceNotesDatabaseHelper((PersistenceApplicationContext) getApplicationContext(),
					"VOICE_NOTES_DB", 1);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
