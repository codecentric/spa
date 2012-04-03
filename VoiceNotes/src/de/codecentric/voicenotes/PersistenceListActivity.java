package de.codecentric.voicenotes;

import android.app.ListActivity;
import de.codecentric.voicenotes.context.ApplicationContext;
import de.codecentric.voicenotes.context.DatabaseHelper;

public class PersistenceListActivity extends ListActivity {

	protected DatabaseHelper dbHelper;

	/**
	 * Opens the database connection.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		dbHelper = ((ApplicationContext) getApplication()).getDatabaseHelper();
	}

}
