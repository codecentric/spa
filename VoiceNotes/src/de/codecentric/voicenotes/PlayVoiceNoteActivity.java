package de.codecentric.voicenotes;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.codecentric.spa.EntityWrapper;
import de.codecentric.spa.ctx.PersistenceApplicationContext;
import de.codecentric.voicenotes.context.PreferenceHelper;
import de.codecentric.voicenotes.entity.Note;

public class PlayVoiceNoteActivity extends BaseActivity {

	/**
	 * Value red from application preferences - true if device should vibrate on
	 * recording events, otherwise false.
	 */
	private boolean doVibrateOnPlay;

	private long noteId;
	private Note note;
	private EntityWrapper wrapper;

	private TextView playExplanationLbl;
	private TextView playTimeLbl;
	private ImageButton playStopBtn;
	private ProgressBar playProgressBar;

	private Vibrator vibrator;
	private Handler playProgressHandler;

	private boolean isPlaying;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_audio_note_screen);

		wrapper = new EntityWrapper((PersistenceApplicationContext) getApplicationContext());
		note = null;

		isPlaying = false;

		playProgressHandler = new Handler();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			noteId = extras.getLong(Note.Extras.EXTRA_NOTE_ID);
		}

		playExplanationLbl = (TextView) findViewById(R.id.playExplanationLbl);
		playTimeLbl = (TextView) findViewById(R.id.playTimeLbl);
		playStopBtn = (ImageButton) findViewById(R.id.playStopBtn);
		playProgressBar = (ProgressBar) findViewById(R.id.playProgress);
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (noteId != 0L) {
			note = wrapper.findById(noteId, Note.class);
		}

		playStopBtn.setOnClickListener(new PlayStopClickListener());
	}

	@Override
	protected void onResume() {
		super.onResume();

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		doVibrateOnPlay = vibrator != null
				&& PreferenceHelper.getBooleanPreference(this, getString(R.string.p_vibrate_on_rec));
	}

	class PlayStopClickListener implements OnClickListener {

		private long mediaDuration;
		private long currentMediaPosition;
		private long updateDelay = 100L;

		/**
		 * Task that updates progress bar and texts of screen labels.
		 */
		private Runnable updateProgressTask = new Runnable() {
			@Override
			public void run() {
				currentMediaPosition += updateDelay;

				int seconds = (int) (currentMediaPosition / 1000);
				long tenthsOfASecond = (currentMediaPosition % 1000) / 100;
				if (seconds < 10) {
					playTimeLbl.setText("0" + seconds + ":" + tenthsOfASecond);
				} else {
					playTimeLbl.setText(seconds + ":" + tenthsOfASecond);
				}

				if (currentMediaPosition < mediaDuration) {
					if (currentMediaPosition % 1000 == 0) {
						int x = Math.round((currentMediaPosition / (float) mediaDuration) * 100f);
						playProgressBar.setProgress(x);
					}
					playProgressHandler.postDelayed(this, updateDelay);
				} else {
					playProgressBar.setProgress(playProgressBar.getMax());
					handleMediaPlayerStop();
				}
			}
		};

		@Override
		public void onClick(View view) {
			isPlaying = !isPlaying;

			if (isPlaying) {
				File file = new File(note.recordingPath);
				if (file.exists()) {
					handleMediaPlayerStart(file);
				}
			} else {
				handleMediaPlayerStop();
			}
		}

		private void handleMediaPlayerStart(File file) {
			playExplanationLbl.setText(R.string.stop_explanation);
			playStopBtn.setBackgroundResource(R.drawable.ic_stop_config);
			if (doVibrateOnPlay) {
				vibrator.vibrate(vibrator_delay);
			}

			Uri recordingUri = Uri.fromFile(file);
			MediaPlayer mediaPlayer = MediaPlayer.create(PlayVoiceNoteActivity.this, recordingUri);
			mediaDuration = mediaPlayer.getDuration();
			currentMediaPosition = 0L;
			playProgressBar.setProgress(0);

			Intent startServiceIntent = new Intent(PlayVoiceNoteActivity.this, PlayVoiceNoteService.class);
			startServiceIntent.putExtra(PlayVoiceNoteService.FILE_URI_PARAM, recordingUri);
			startService(startServiceIntent);
			playProgressHandler.postDelayed(updateProgressTask, updateDelay);
		}

		/**
		 * Method handles UI components behavior when playing is stopped.
		 */
		private void handleMediaPlayerStop() {
			isPlaying = false;
			playExplanationLbl.setText(R.string.play_explanation);
			playStopBtn.setBackgroundResource(R.drawable.ic_play_config);
			if (doVibrateOnPlay) {
				vibrator.vibrate(vibrator_delay);
			}
			stopService(new Intent(PlayVoiceNoteActivity.this, PlayVoiceNoteService.class));
			playProgressHandler.removeCallbacks(updateProgressTask);
		}

	}

}
