package de.codecentric.spa.ctx;

import android.app.ListActivity;

/**
 * Persistence list activity class - all list activities that do some persistence jobs and want to do it using directly {@link DatabaseHelper} should extend this class.
 */
public class PersistenceListActivity extends ListActivity {

    protected DatabaseHelper dbHelper;

    /**
     * Opens the database connection.
     */
    @Override
    protected void onStart() {
        super.onStart();
        dbHelper = PersistenceContext.getInstance().getDatabaseHelper();
    }

}
