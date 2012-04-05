package de.codecentric.voicenotes.layout;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.codecentric.voicenotes.R;
import de.codecentric.voicenotes.entity.Note;

public class NoteListAdapter extends ArrayAdapter<Note> {

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