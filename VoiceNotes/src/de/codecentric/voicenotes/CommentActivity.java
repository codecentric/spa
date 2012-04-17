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
import android.widget.Toast;
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

	private TextView commentCreationTimeLbl;
	private EditText commentTextTxt;
	private Button saveBtn;
	private Note note;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_screen);

		Bundle extras = getIntent().getExtras();
		entity = new Comment();
		wrapper = ((PersistenceApplicationContext) getApplication())
				.getEntityWrapper();
		note = wrapper.findById(extras.getLong(Note.Extras.EXTRA_NOTE_ID),
				Note.class);

		// check if activity is started in 'view/edit note' or 'create' mode
		if (extras != null) {
			entity.id = extras.getLong(Comment.Extras.EXTRA_COMMENT_ID);
			entity = wrapper.findById(entity.id, Comment.class);
			if (entity == null) {
				entity = new Comment();
			}
		}

		commentCreationTimeLbl = (TextView) findViewById(R.id.comment_creation_time_lbl);
		commentTextTxt = (EditText) findViewById(R.id.comment_text_txt);
		saveBtn = (Button) findViewById(R.id.saveBtn);
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (entity.id != 0L) {
			// we will load existing note => present it's creation time
			commentCreationTimeLbl.setVisibility(View.VISIBLE);
			commentTextTxt.setText(entity.text);
		} else {
			commentCreationTimeLbl.setVisibility(View.GONE);
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

		String text = commentTextTxt.getText().toString();

		if ("".equals(text)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			if ("".equals(text)) {
				builder.setMessage(R.string.txt_comment_empty_text);
				commentTextTxt.requestFocus();
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
			finish();
		}

	}

	/**
	 * Method saves textual note and finishes this activity.
	 */
	private void saveCommentAndFinish() {
		if (saveCommentNote()) {
			issueSuccessNotification();
		}else{
			issueFailNotification();
		}
	}

	private void issueSuccessNotification() {
		Toast.makeText(getApplicationContext(), R.string.comment_saved_ok,
				Toast.LENGTH_SHORT).show();
		finish();
	}
	
	private void issueFailNotification() {
		Toast.makeText(getApplicationContext(), R.string.comment_savefailed,
				Toast.LENGTH_SHORT).show();
		finish();
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

		entity.text = commentTextTxt.getText().toString();

		if (entity.id == 0L) {
			entity.timeCreated = new Date();
		}

		note.comments.add(entity);
		try {
			wrapper.saveOrUpdate(entity);
			return true;
		} catch (Exception e) {
			return false;
		}		
	}
}
