package de.codecentric.spa.ctx;

import android.app.Activity;

/**
 * Persistence activity class - all activities that do some persistence jobs and want to do it using directly {@link DatabaseHelper} should extend this class.
 */
public abstract class PersistenceActivity extends Activity {

    protected DatabaseHelper dbHelper;

    public PersistenceActivity() {
        super();

    }

    /**
     * Opens the database connection.
     */
    @Override
    protected void onStart() {
        super.onStart();
        dbHelper = PersistenceContext.getInstance().getDatabaseHelper();
    }

}
