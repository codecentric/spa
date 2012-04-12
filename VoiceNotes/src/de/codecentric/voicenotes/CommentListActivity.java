package de.codecentric.voicenotes;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import de.codecentric.spa.EntityWrapper;
import de.codecentric.spa.ctx.PersistenceApplicationContext;
import de.codecentric.voicenotes.entity.Comment;
import de.codecentric.voicenotes.entity.Note;

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
		Note n = wrapper.findById(noteId, Note.class);

		commentList = n.comments;
		setListAdapter(new ArrayAdapter<Comment>(this,
				R.layout.list_comment_screen, commentList));

		listView = getListView();
		listView.setTextFilterEnabled(true);
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
			// TODO: ovo srediti
			wrapper.deleteAll(Comment.class);
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
