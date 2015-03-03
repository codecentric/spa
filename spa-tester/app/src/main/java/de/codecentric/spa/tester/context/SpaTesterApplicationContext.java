package de.codecentric.spa.tester.context;

import de.codecentric.spa.EntityHelper;
import de.codecentric.spa.ctx.PersistenceApplicationContext;
import de.codecentric.spa.metadata.EntityMetaData;
import de.codecentric.spa.sql.SQLGenerator;
import de.codecentric.spa.sql.SQLGenerator.SQLStatements;

public class SpaTesterApplicationContext extends PersistenceApplicationContext {

	private static int identationLevel = 0;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onCreate() {
		super.onCreate();

		try {
			String[] clsNames = new String[] { "de.codecentric.spa.tester.entity.State",
					"de.codecentric.spa.tester.entity.City", "de.codecentric.spa.tester.entity.Government" };

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

					// ... instantiate entity helpers for every persistent class
					// ... when SQL statements are ready
					entityWrapper.putEntityHelper(cls, new EntityHelper(this, cls));
				}
			}

			// ... and then instantiate database helper
			dbHelper = new SpaTesterDatabaseHelper((PersistenceApplicationContext) getApplicationContext(),
					"VOICE_NOTES_DB", 1);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static void resetIdentationLevel() {
		identationLevel = 0;
	}

	public static String increaseIdentationLevel() {
		String identPrefix = "";
		identationLevel++;
		for (int i = 0; i < identationLevel; i++) {
			identPrefix += "\t";
		}
		return identPrefix;
	}

	public static String decreaseIdentationLevel() {
		String identPrefix = "";
		identationLevel--;
		if (identationLevel < 0) {
			identationLevel = 0;
		}
		for (int i = 0; i < identationLevel; i++) {
			identPrefix += "\t";
		}
		return identPrefix;
	}

	public static String getIdentationPrefix() {
		String identPrefix = "";
		for (int i = 0; i < identationLevel; i++) {
			identPrefix += "\t";
		}
		return identPrefix;
	}

}
