package de.codecentric.voicenotes;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.codecentric.spa.EntityWrapper;
import de.codecentric.spa.ctx.PersistenceApplicationContext;
import de.codecentric.voicenotes.entity.Note;

public class NoteListActivity extends ListActivity {

	private ListView listView;
	private EntityWrapper wrapper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_note_screen);

		listView = (ListView) findViewById(android.R.id.list);

		wrapper = new EntityWrapper((PersistenceApplicationContext) getApplication());
	}

	@Override
	protected void onStart() {
		super.onStart();

		List<Note> entities = wrapper.listAll(Note.class);
		setListAdapter(new NoteListAdapter(this, android.R.layout.simple_list_item_1, entities));

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
				Intent intent = new Intent(NoteListActivity.this, TextualNoteActivity.class);
				intent.putExtra(Note.Extras.EXTRA_NOTE_ID, (Long) view.getTag());
				startActivity(intent);
			}
		});
	}

	private class NoteListAdapter extends ArrayAdapter<Note> {

		public NoteListAdapter(Context context, int textViewResourceId, List<Note> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.note_list_item_view, null);

			Note note = getItem(position);
			v.setTag(note.id); // quick hack to have entity id in list item

			if (note.hasRecording) {
				v.findViewById(R.id.sound_marker).setVisibility(View.VISIBLE);
			}

			String itemTitle = note.title;
			if (itemTitle == null || "".equals(itemTitle)) {
				itemTitle = note.timeCreated;
			}
			TextView txt = (TextView) v.findViewById(R.id.item_txt1);
			txt.setText(itemTitle);

			return v;
		}

	}

}
