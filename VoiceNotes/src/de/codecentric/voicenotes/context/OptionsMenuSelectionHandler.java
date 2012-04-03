package de.codecentric.voicenotes.context;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import de.codecentric.voicenotes.NoteListActivity;
import de.codecentric.voicenotes.PreferencesActivity;
import de.codecentric.voicenotes.R;
import de.codecentric.voicenotes.TextualNoteActivity;

/**
 * Class handling the application's options menu items and their behavior.
 */
public class OptionsMenuSelectionHandler {

	/**
	 * Method handles the options item selection - based on selected item,
	 * method returns the {@link Intent} instance that should be started.
	 * 
	 * @param ctx
	 *            context of execution
	 * @param item
	 *            selected menu item
	 * @return intent to start
	 */
	public static Intent handleItemSelection(Context ctx, MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.app_ctx_menu_pref_btn:
			intent = new Intent(ctx, PreferencesActivity.class);
			break;
		case R.id.app_ctx_menu_txtnote_btn:
			intent = new Intent(ctx, TextualNoteActivity.class);
			break;
		case R.id.app_ctx_menu_listnotes_btn:
			intent = new Intent(ctx, NoteListActivity.class);
			break;
		}
		return intent;
	}

}
