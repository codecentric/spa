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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;
import de.codecentric.spa.EntityWrapper;
import de.codecentric.spa.ctx.PersistenceApplicationContext;
import de.codecentric.voicenotes.entity.Comment;

public class CommentListActivity extends ListActivity {

	private ListView listView;
	private EntityWrapper wrapper;
	private List<Comment> commentList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.list_comment_screen);

		wrapper = ((PersistenceApplicationContext) getApplication())
				.getEntityWrapper();

		Long noteId = getIntent().getExtras().getLong(
				Comment.Extras.EXTRA_NOTE_ID);

		// commentList = wrapper.findBy(" where "
		// + ((PersistenceApplicationContext) getApplication())
		// .getEntityMetaDataProvider().getMetaData(Note.class)
		// .getColumnNameForField("comments") + "=" + noteId,
		// Comment.class);
		commentList = wrapper.findBy(" where comments_fk = " + noteId,
				Comment.class);
		setListAdapter(new ArrayAdapter<Comment>(this,
				R.layout.list_comment_screen, commentList));

		listView = getListView();
		listView.setTextFilterEnabled(true);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Comment comment = commentList.get(position);
				AlertDialog.Builder builder = new AlertDialog.Builder(CommentListActivity.this);
				builder.setTitle(R.string.choose_action);

				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final View dialogView = inflater.inflate(
						R.layout.comment_chooser_dialog, null);

				RadioButton[] radioArray = new RadioButton[2];
				radioArray[0] = (RadioButton) dialogView
						.findViewById(R.id.action_edit_comment);
				radioArray[1] = (RadioButton) dialogView
						.findViewById(R.id.action_delete_comment);

				fixRadioButtonsLayout(comment, radioArray);
				builder.setView(dialogView);
				Dialog dialog = builder.create();
				attachRadioButtonListeners(dialog, comment, radioArray);

				dialog.show();
			}
		});
	}

	private void attachRadioButtonListeners(Dialog dialog, Comment comment,
			RadioButton... radioButtons) {
		OnClickListener actionChooserClickListener = new ActionChooserClickListener(
				dialog, comment);
		radioButtons[0].setOnClickListener(actionChooserClickListener);
		radioButtons[1].setOnClickListener(actionChooserClickListener);		
	}

	private void fixRadioButtonsLayout(Comment comment,
			RadioButton... radioButtons) {
		final float scale = this.getResources().getDisplayMetrics().density;
		fixSingleRadioButtonLayout(radioButtons[0], scale);
		fixSingleRadioButtonLayout(radioButtons[1], scale);
	}

	private void fixSingleRadioButtonLayout(RadioButton radioBtn, float scale) {
		radioBtn.setPadding(radioBtn.getPaddingLeft()
				+ (int) (10.0f * scale + 0.5f), radioBtn.getPaddingTop(),
				radioBtn.getPaddingRight(), radioBtn.getPaddingBottom());
	}

	private class ActionChooserClickListener implements View.OnClickListener {

		private Dialog dialog;
		private Comment comment;

		protected ActionChooserClickListener(Dialog dialog, Comment comment) {
			this.dialog = dialog;
			this.comment = comment;
		}

		@Override
		public void onClick(View view) {
			RadioButton button = (RadioButton) view;
			
			if (button.getId() == R.id.action_delete_comment) {
				String msg = "";
				try {
					wrapper.delete(comment.id, Comment.class);
					((ArrayAdapter<Comment>) getListAdapter()).remove(comment);
					msg = "Comment deleted";
				} catch (Exception e) {
					msg = "Error deleting comment";
				}

				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}
			
			dialog.cancel();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.comment_context_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean handled = false;

		switch (item.getItemId()) {
		case R.id.delete_all_comments_btn: {
			for (Comment c : commentList) {
				wrapper.delete(c.id, Comment.class);
			}
			Toast.makeText(getApplicationContext(), "Comments deleted",
					Toast.LENGTH_SHORT).show();
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
}
