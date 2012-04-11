package de.codecentric.voicenotes;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;
import de.codecentric.spa.EntityWrapper;
import de.codecentric.spa.ctx.PersistenceApplicationContext;
import de.codecentric.voicenotes.entity.Note;
import de.codecentric.voicenotes.layout.NoteListAdapter;

/**
 * Activity lists of notes.
 * 
 * This activity uses {@link EntityWrapper} to do all database work.
 */
public class NoteListActivity extends ListActivity {

	private ListView listView;
	private EntityWrapper wrapper;
	private List<Note> noteList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_note_screen);

		listView = (ListView) findViewById(android.R.id.list);
		wrapper = ((PersistenceApplicationContext) getApplication()).getEntityWrapper();
	}

	@Override
	protected void onStart() {
		super.onStart();

		noteList = wrapper.listAll(Note.class);
		setListAdapter(new NoteListAdapter(this, android.R.layout.simple_list_item_1, noteList));
		listView.setOnItemClickListener(new NoteListItemClickListener());
	}

	@Override
	protected void onStop() {
		super.onStop();
		noteList = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.notelist_context_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean handled = false;

		switch (item.getItemId()) {
		case R.id.delete_all_btn: {
			wrapper.deleteAll(Note.class);
			Toast.makeText(getApplicationContext(), "Notes deleted", Toast.LENGTH_SHORT).show();
			startActivity(getIntent());
			finish();
			break;
		}

		case R.id.app_ctx_menu_txtnote_btn: {
			Intent intent = new Intent(this, TextualNoteActivity.class);
			if (intent != null) {
				startActivity(intent);
				handled = true;
			}
			break;
		}
		}

		return handled;
	}

	private class NoteListItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
			createAndShowActionChooserDialog(noteList.get(pos));
		}

	}

	/**
	 * Method creates and shows action chooser dialog.
	 * 
	 * @param aNote
	 *            a selected note
	 */
	private void createAndShowActionChooserDialog(final Note aNote) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.choose_action);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View dialogView = inflater.inflate(R.layout.action_chooser_dialog, null);

		RadioButton[] radioArray = new RadioButton[4];
		radioArray[0] = (RadioButton) dialogView.findViewById(R.id.action_edit_note);
		radioArray[1] = (RadioButton) dialogView.findViewById(R.id.action_listen_voice_note);
		radioArray[2] = (RadioButton) dialogView.findViewById(R.id.action_edit_comments);
		radioArray[3] = (RadioButton) dialogView.findViewById(R.id.action_delete_note);

		fixRadioButtonsLayout(aNote, radioArray);
		builder.setView(dialogView);
		Dialog dialog = builder.create();
		attachRadioButtonListeners(dialog, aNote, radioArray);

		dialog.show();
	}

	private void fixRadioButtonsLayout(Note aNote, RadioButton... radioButtons) {
		final float scale = this.getResources().getDisplayMetrics().density;

		fixSingleRadioButtonLayout(radioButtons[0], scale);
		fixSingleRadioButtonLayout(radioButtons[1], scale);
		fixSingleRadioButtonLayout(radioButtons[2], scale);
		fixSingleRadioButtonLayout(radioButtons[3], scale);

		if (!aNote.hasRecording) {
			radioButtons[1].setVisibility(View.INVISIBLE);
		}
	}

	private void fixSingleRadioButtonLayout(RadioButton radioBtn, float scale) {
		radioBtn.setPadding(radioBtn.getPaddingLeft() + (int) (10.0f * scale + 0.5f), radioBtn.getPaddingTop(),
				radioBtn.getPaddingRight(), radioBtn.getPaddingBottom());
	}

	private void attachRadioButtonListeners(Dialog dialog, Note note, RadioButton... radioButtons) {
		OnClickListener actionChooserClickListener = new ActionChooserClickListener(dialog, note);
		radioButtons[0].setOnClickListener(actionChooserClickListener);
		radioButtons[1].setOnClickListener(actionChooserClickListener);
		radioButtons[2].setOnClickListener(actionChooserClickListener);
		radioButtons[3].setOnClickListener(actionChooserClickListener);
	}

	private class ActionChooserClickListener implements View.OnClickListener {

		private Dialog dialog;
		private Note note;

		protected ActionChooserClickListener(Dialog dialog, Note note) {
			this.dialog = dialog;
			this.note = note;
		}

		@Override
		public void onClick(View view) {
			RadioButton button = (RadioButton) view;
			if (button.getId() == R.id.action_edit_note) {
				Intent intent = new Intent(NoteListActivity.this, TextualNoteActivity.class);
				intent.putExtra(Note.Extras.EXTRA_NOTE_ID, note.id);
				startActivity(intent);
			} else if (button.getId() == R.id.action_listen_voice_note) {
				Intent intent = new Intent(NoteListActivity.this, PlayVoiceNoteActivity.class);
				intent.putExtra(Note.Extras.EXTRA_NOTE_ID, note.id);
				startActivity(intent);
			} else if (button.getId() == R.id.action_edit_comments) {

			} else if (button.getId() == R.id.action_delete_note) {
				String msg = "";
				try {
					wrapper.delete(note.id, Note.class);
					((NoteListAdapter) getListAdapter()).remove(note);
					msg = "Note deleted";
				} catch (Exception e) {
					msg = "Error deleting note";
				}

				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}

			dialog.cancel();
		}
	}
}
