package de.codecentric.voicenotes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import de.codecentric.spa.EntityWrapper;
import de.codecentric.spa.ctx.PersistenceActivity;
import de.codecentric.spa.ctx.PersistenceApplicationContext;
import de.codecentric.voicenotes.context.Constants;
import de.codecentric.voicenotes.context.OnetimeAlarmReceiver;
import de.codecentric.voicenotes.entity.Note;

/**
 * Activity used to show and edit textual note.
 */
public class TextualNoteActivity extends PersistenceActivity {

	private Note entity;
	private EntityWrapper wrapper;

	private ScrollView scrollView;
	private EditText noteTitleTxt;
	private EditText noteTextTxt;
	private EditText noteDueTimeTxt;
	private TextView noteCreationTimeLbl;
	private TextView successLbl;
	private ImageButton alarmBtn;
	private Button saveBtn;

	private Dialog dateTimePickerDialog;
	private TimePicker timePicker;
	private DatePicker datePicker;
	private Date dueDate;
	private PendingIntent pendingIntent;
	private AlarmManager alarmManager;

	private Handler uiUpdater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.textual_note_screen);
		wrapper = new EntityWrapper((PersistenceApplicationContext) getApplication());

		entity = new Note();

		// check if activity is started in 'view/edit note' or 'create' mode
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			entity.id = extras.getLong(Note.Extras.EXTRA_NOTE_ID);
		}

		uiUpdater = new Handler();

		scrollView = (ScrollView) findViewById(R.id.scroll_view);
		noteTitleTxt = (EditText) findViewById(R.id.note_title_txt);
		noteTextTxt = (EditText) findViewById(R.id.note_text_txt);
		noteDueTimeTxt = (EditText) findViewById(R.id.note_due_time_txt);
		noteCreationTimeLbl = (TextView) findViewById(R.id.note_creation_time_lbl);
		successLbl = (TextView) findViewById(R.id.successLbl);
		alarmBtn = (ImageButton) findViewById(R.id.alarmBtn);
		saveBtn = (Button) findViewById(R.id.saveBtn);
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (entity.id != 0) {
			// we will load existing note => present it's creation time
			noteCreationTimeLbl.setVisibility(View.VISIBLE);
		} else {
			noteCreationTimeLbl.setVisibility(View.GONE);
		}

		noteDueTimeTxt.setOnTouchListener(new DueTimeFieldClickListener());
		alarmBtn.setOnClickListener(new AlarmButtonClickListener());
		saveBtn.setOnClickListener(new SaveTextualNoteClickListener());

		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (entity.id != 0) {
			entity = wrapper.findById(entity.id, entity.getClass());
			if (entity != null) {
				presentTextualNoteData();

				Intent intent = new Intent(TextualNoteActivity.this, OnetimeAlarmReceiver.class);
				pendingIntent = PendingIntent.getBroadcast(TextualNoteActivity.this, 0, intent,
						PendingIntent.FLAG_CANCEL_CURRENT);
			}
		}
	}

	/**
	 * Method sets values of textual note to the appropriate UI fields.
	 */
	private void presentTextualNoteData() {
		noteTitleTxt.setText(entity.title);
		noteTextTxt.setText(entity.text);
		noteDueTimeTxt.setText(entity.dueTime);
		noteCreationTimeLbl.setText(getString(R.string.textual_note_creation_time) + " " + entity.timeCreated);

		if (entity.dueTime != null && entity.dueTime.length() > 0) {
			String[] timeAndDateStringArr = entity.dueTime.split(" ");
			String[] timeStringArr = timeAndDateStringArr[0].split(":");
			String[] dateStringArr = timeAndDateStringArr[1].split("-");
			Calendar c = new GregorianCalendar(Integer.parseInt(dateStringArr[0]),
					Integer.parseInt(dateStringArr[1]) - 1, Integer.parseInt(dateStringArr[2]),
					Integer.parseInt(timeStringArr[0]), Integer.parseInt(timeStringArr[1]));
			dueDate = c.getTime();
		} else {
			dueDate = null;
		}

		if (entity.hasAlarm) {
			alarmBtn.setImageDrawable(getResources().getDrawable(R.drawable.notify_alarm_remove));
		} else {
			alarmBtn.setImageDrawable(getResources().getDrawable(R.drawable.notify_alarm_add));
		}
	}

	/**
	 * Method creates dialog containing information about validation errors.
	 * 
	 * @return validation info dialog
	 */
	private Dialog createValidationDialog() {
		Dialog d = null;

		String title = noteTitleTxt.getText().toString();
		String text = noteTextTxt.getText().toString();
		String dueTime = noteDueTimeTxt.getText().toString();

		if ("".equals(title) || "".equals(text) || "".equals(dueTime) || new Date().after(dueDate)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			if ("".equals(title)) {
				builder.setMessage(R.string.txt_note_empty_title);
				noteTitleTxt.requestFocus();
			} else if ("".equals(text)) {
				builder.setMessage(R.string.txt_note_empty_text);
				noteTextTxt.requestFocus();
			} else if ("".equals(dueTime)) {
				builder.setMessage(R.string.txt_note_due_date_empty_text);
				noteDueTimeTxt.requestFocus();
			} else if (new Date().after(dueDate)) {
				builder.setMessage(R.string.txt_note_due_date_passed);
				noteDueTimeTxt.requestFocus();
			}

			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			d = builder.create();
		}

		return d;
	}

	class DueTimeFieldClickListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (MotionEvent.ACTION_UP == event.getAction()) {
				if (dateTimePickerDialog == null) {
					dateTimePickerDialog = createDateTimePickerDialog();
				}
				timePicker = (TimePicker) dateTimePickerDialog.findViewById(R.id.time_picker);
				datePicker = (DatePicker) dateTimePickerDialog.findViewById(R.id.date_picker);
				String currentDueDate = noteDueTimeTxt.getText().toString();
				if (!"".equals(currentDueDate)) {
					String[] timeAndDateStringArr = currentDueDate.split(" ");

					String[] timeStringArr = timeAndDateStringArr[0].split(":");
					timePicker.setCurrentHour(Integer.parseInt(timeStringArr[0]));
					timePicker.setCurrentMinute(Integer.parseInt(timeStringArr[1]));

					String[] dateStringArr = timeAndDateStringArr[1].split("-");
					datePicker.updateDate(Integer.parseInt(dateStringArr[0]), Integer.parseInt(dateStringArr[1]) - 1,
							Integer.parseInt(dateStringArr[2]));
				}
				dateTimePickerDialog.show();
			}
			return true;
		}

	}

	/**
	 * Method creates date and time picker dialog.
	 * 
	 * @return date and time picker dialog
	 */
	private Dialog createDateTimePickerDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.set_exp_time);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.date_time_picker_view, null);
		builder.setView(v);

		((TimePicker) v.findViewById(R.id.time_picker)).setIs24HourView(true);

		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int hour = timePicker.getCurrentHour();
				int minute = timePicker.getCurrentMinute();
				int year = datePicker.getYear();
				int month = datePicker.getMonth();
				int day = datePicker.getDayOfMonth();

				Calendar c = new GregorianCalendar(year, month, day, hour, minute);
				dueDate = c.getTime();
				String timeString = (new SimpleDateFormat(Constants.DATE_FORMAT)).format(dueDate);
				noteDueTimeTxt.setText(timeString);

				dialog.cancel();
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		return builder.show();
	}

	/**
	 * Class that adds and removes alarms bound to the textual note.
	 */
	class AlarmButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (!entity.hasAlarm) {
				registerAlarm();
			} else {
				cancelAlarm();
			}
		}

		private void registerAlarm() {
			Dialog d = createValidationDialog();
			if (d != null) {
				d.show();
				return;
			}

			Bundle extras = new Bundle();
			extras.putString(Constants.EXTRA_NOTIFICATION_TITLE, noteTitleTxt.getText().toString());
			extras.putString(Constants.EXTRA_NOTIFICATION_TEXT, noteTextTxt.getText().toString());
			extras.putString(Constants.EXTRA_NOTIFICATION_ACTIVITY_NAME, "TextualNoteActivity");
			extras.putLong(Note.Extras.EXTRA_NOTE_ID, entity.id);

			entity.hasAlarm = true;
			if (saveTextualNote()) {
				Intent intent = new Intent(TextualNoteActivity.this, OnetimeAlarmReceiver.class);
				intent.putExtras(extras);

				pendingIntent = PendingIntent.getBroadcast(TextualNoteActivity.this, 0, intent,
						PendingIntent.FLAG_CANCEL_CURRENT);
				alarmManager.set(AlarmManager.RTC_WAKEUP, dueDate.getTime(), pendingIntent);

				alarmBtn.setImageDrawable(getResources().getDrawable(R.drawable.notify_alarm_remove));
			}
		}

		private void cancelAlarm() {
			entity.hasAlarm = false;
			if (saveTextualNote()) {
				alarmManager.cancel(pendingIntent);
				alarmBtn.setImageDrawable(getResources().getDrawable(R.drawable.notify_alarm_add));
			}
		}

	}

	class SaveTextualNoteClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			saveTextualNoteAndFinish();
		}

	}

	/**
	 * Method saves textual note and finishes this activity.
	 */
	private void saveTextualNoteAndFinish() {
		if (saveTextualNote()) {
			issueSuccessNotification();
		}
	}

	/**
	 * Method forms a {@link Note} by reading the date from UI fields and saves
	 * it.
	 */
	private boolean saveTextualNote() {
		Dialog d = createValidationDialog();
		if (d != null) {
			d.show();
			return false;
		}

		entity.title = noteTitleTxt.getText().toString();
		entity.text = noteTextTxt.getText().toString();
		entity.dueTime = noteDueTimeTxt.getText().toString();

		if (entity.id == 0) {
			entity.timeCreated = (new SimpleDateFormat(Constants.DATE_FORMAT)).format(new Date());
		}

		wrapper.saveOrUpdate(entity);
		return true;
	}

	/**
	 * Method presents a notification for user stating that {@link Note} is
	 * saved successfully.
	 */
	private void issueSuccessNotification() {
		scrollView.scrollTo(0, 0);
		successLbl.setText(R.string.note_saved_ok);
		successLbl.setVisibility(View.VISIBLE);

		Runnable updateUITask = new Runnable() {
			@Override
			public void run() {
				successLbl.setText("");
				successLbl.setVisibility(View.GONE);
				finish();
			}
		};

		uiUpdater.postDelayed(updateUITask, Constants.UI_POST_NOTIFICATION_DELAY);
	}

}
