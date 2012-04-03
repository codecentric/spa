package de.codecentric.voicenotes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import de.codecentric.voicenotes.context.OptionsMenuSelectionHandler;

/**
 * Abstract class representing base activity that should be extended by all
 * activities defined in this application.
 * 
 * Class defines behavior of the application when "Menu" button is pressed.
 * Since response on that action should be the same for every activity, this
 * class should be the root of hierarchy of all activity classes in this
 * application.
 */
public abstract class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * Method is called when options menu is created by the activity.
	 * 
	 * @see {@link Activity#onCreateOptionsMenu(Menu menu)}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.app_context_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean handled = false;
		Intent i = OptionsMenuSelectionHandler.handleItemSelection(this, item);
		if (i != null) {
			startActivity(i);
			handled = true;
		}
		return handled;
	}

}
