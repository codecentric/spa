package de.codecentric.voicenotes;

import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.codecentric.spa.EntityWrapper;
import de.codecentric.spa.ctx.PersistenceApplicationContext;
import de.codecentric.voicenotes.entity.Comment;
import de.codecentric.voicenotes.entity.Note;

/**
 * Activity used to show and edit textual note.
 */
public class CommentActivity extends BaseActivity {

	private Comment entity;
	private EntityWrapper wrapper;

	private TextView noteCreationTimeLbl;
	private EditText noteTextTxt;
	private Button saveBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.textual_note_screen);

		entity = new Comment();
		wrapper = ((PersistenceApplicationContext) getApplication())
				.getEntityWrapper();

		// check if activity is started in 'view/edit note' or 'create' mode
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			entity.id = extras.getLong(Comment.Extras.EXTRA_NOTE_ID);
		}

		noteCreationTimeLbl = (TextView) findViewById(R.id.note_creation_time_lbl);
		noteTextTxt = (EditText) findViewById(R.id.comment_text_txt);
		saveBtn = (Button) findViewById(R.id.saveBtn);
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (entity.id != 0L) {
			// we will load existing note => present it's creation time
			noteCreationTimeLbl.setVisibility(View.VISIBLE);
		} else {
			noteCreationTimeLbl.setVisibility(View.GONE);
		}

		saveBtn.setOnClickListener(new SaveCommentClickListener());
	}

	/**
	 * Method creates dialog containing information about validation errors.
	 * 
	 * @return validation info dialog
	 */
	private Dialog createValidationDialog() {
		Dialog d = null;

		String text = noteTextTxt.getText().toString();

		if ("".equals(text)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			if ("".equals(text)) {
				builder.setMessage(R.string.txt_comment_empty_text);
				noteTextTxt.requestFocus();
			}

			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});

			d = builder.create();
		}

		return d;
	}

	class SaveCommentClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			saveCommentAndFinish();
		}

	}

	/**
	 * Method saves textual note and finishes this activity.
	 */
	private void saveCommentAndFinish() {
		if (saveCommentNote()) {
			issueSuccessNotification();
		}
	}

	private void issueSuccessNotification() {
		// TODO: toast message
	}

	/**
	 * Method forms a {@link Note} by reading the date from UI fields and saves
	 * it.
	 */
	private boolean saveCommentNote() {
		Dialog d = createValidationDialog();
		if (d != null) {
			d.show();
			return false;
		}

		entity.text = noteTextTxt.getText().toString();

		if (entity.id == 0L) {
			entity.timeCreated = new Date();
		}

		wrapper.saveOrUpdate(entity);
		return true;
	}
}
