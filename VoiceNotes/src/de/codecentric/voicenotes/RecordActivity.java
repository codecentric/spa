package de.codecentric.voicenotes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.codecentric.spa.EntityWrapper;
import de.codecentric.spa.ctx.PersistenceApplicationContext;
import de.codecentric.voicenotes.context.Constants;
import de.codecentric.voicenotes.context.PreferenceHelper;
import de.codecentric.voicenotes.entity.Note;

/**
 * Activity presenting the screen used for recording the voice note.
 */
public class RecordActivity extends BaseActivity {

	/**
	 * True if recording is in progress, otherwise false;
	 */
	private boolean isRecording;

	/**
	 * Value read from application preferences - true if sounds should be
	 * played, otherwise false.
	 */
	private boolean playSounds;

	/**
	 * Value red from application preferences - true if device should vibrate on
	 * recording events, otherwise false.
	 */
	private boolean doVibrateOnRec;

	private TextView recLbl;
	private ImageButton recBtn;
	private TextView timeLbl;
	private ProgressBar recProgressBar;

	private Handler recProgressHandler;

	private MediaPlayer startMediaPlayer;
	private MediaPlayer stopMediaPlayer;

	private MediaRecorder mediaRecorder;
	private String recordingFileName;

	private Vibrator vibrator;

	private static final long vibrator_delay = 700;
	private static final long delay = 997;

	private Note aNote;
	private EntityWrapper wrapper;

	/**
	 * Amount of time in milliseconds how long a voice note can be.
	 */
	private int maxTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_screen);

		recBtn = (ImageButton) findViewById(R.id.recBtn);
		recLbl = (TextView) findViewById(R.id.recLbl);
		timeLbl = (TextView) findViewById(R.id.timeLbl);
		recProgressBar = (ProgressBar) findViewById(R.id.recProgress);

		maxTime = Integer.parseInt(getString(R.string.max_time_to_record));
		recProgressHandler = new Handler();
		isRecording = false;

		wrapper = new EntityWrapper((PersistenceApplicationContext) getApplication());
		aNote = new Note();
	}

	@Override
	protected void onStart() {
		super.onStart();
		recBtn.setOnClickListener(new RecordButtonClickListener());
	}

	@Override
	protected void onResume() {
		super.onResume();

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		doVibrateOnRec = vibrator != null
				&& PreferenceHelper.getBooleanPreference(this, getString(R.string.p_vibrate_on_rec));

		playSounds = PreferenceHelper.getBooleanPreference(this, getString(R.string.p_play_sounds));
		if (playSounds) {
			startMediaPlayer = MediaPlayer.create(this, R.raw.beep);
			stopMediaPlayer = MediaPlayer.create(this, R.raw.rec_over);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (playSounds) {
			startMediaPlayer.release();
			startMediaPlayer = null;
			stopMediaPlayer.release();
			stopMediaPlayer = null;
		}

		recProgressBar.setProgress(0);
		timeLbl.setText("");
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * Record button click listener.
	 */
	class RecordButtonClickListener implements OnClickListener {

		private long startTime;

		/**
		 * Task that updates progress bar and texts of screen labels.
		 */
		private Runnable updateProgressTask = new Runnable() {
			@Override
			public void run() {
				final long start = startTime;
				long millis = System.currentTimeMillis() - start;

				int seconds = (int) (millis / 1000);
				int minutes = seconds / 60;
				seconds = seconds % 60;

				if (seconds < 10) {
					timeLbl.setText("" + minutes + ":0" + seconds);
				} else {
					timeLbl.setText("" + minutes + ":" + seconds);
				}

				if (millis < maxTime) {
					int x = Math.round((millis / (float) maxTime) * 100f);
					recProgressBar.setProgress(x);
					recProgressHandler.postDelayed(this, delay);
				} else { // stop after defined amount of time
					stopRecordingAndPersist();
					handleUIOnRecStop();
					recProgressBar.setProgress(recProgressBar.getMax());
				}
			}
		};

		/**
		 * Click handler method.
		 */
		@Override
		public void onClick(View v) {
			isRecording = !isRecording;

			if (isRecording) {
				recBtn.setBackgroundResource(R.drawable.microphone_disabled_config);
				recLbl.setText(R.string.rec_in_progress);
				if (doVibrateOnRec) {
					vibrator.vibrate(vibrator_delay);
				}
				if (playSounds) {
					startMediaPlayer.start();
					while (startMediaPlayer.isPlaying()) {
						// nothing to do, just wait
					}
				}
				startProgress();
				startRecording();
			} else {
				stopRecordingAndPersist();
				handleUIOnRecStop();
			}
		}

		/**
		 * Method handles UI components behavior when recording is stopped.
		 */
		private void handleUIOnRecStop() {
			if (doVibrateOnRec) {
				vibrator.vibrate(vibrator_delay);
			}
			if (playSounds) {
				stopMediaPlayer.start();
				while (stopMediaPlayer.isPlaying()) {
					// nothing to do, just wait
				}
			}
			recBtn.setBackgroundResource(R.drawable.microphone_enabled_config);
			recLbl.setText(R.string.rec_explanation);
			recProgressHandler.removeCallbacks(updateProgressTask);
		}

		/**
		 * Method starts recording from the microphone.
		 */
		private void startRecording() {
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recordingFileName = generateAudioFileName();
			mediaRecorder.setOutputFile(recordingFileName);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			try {
				mediaRecorder.prepare();
			} catch (IOException e) {
				Log.e(this.getClass().getName(), "prepare() failed");
			}
			mediaRecorder.start();
		}

		/**
		 * Method stops recording and persists audio note.
		 */
		private void stopRecordingAndPersist() {
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;

			if (aNote == null) {
				aNote = new Note();
			}
			aNote.timeCreated = (new SimpleDateFormat(Constants.DATE_FORMAT)).format(new Date());
			aNote.hasRecording = true;
			aNote.recordingPath = recordingFileName;
			wrapper.saveOrUpdate(aNote);
		}

		/**
		 * Method generates name of the file where the audio will be saved. It
		 * will be placed in external storage directory with a name "voicenote_"
		 * + System.currentTimeMillis() + ".3gp.
		 * 
		 * @return generated file name
		 */
		private String generateAudioFileName() {
			String fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
			fileName += "/voicenote_" + System.currentTimeMillis() + ".3gp";
			return fileName;
		}

		/**
		 * Method initializes progress bar and starts its updating.
		 */
		private void startProgress() {
			startTime = System.currentTimeMillis();

			recProgressBar.setProgress(0);
			recProgressHandler.removeCallbacks(updateProgressTask);
			recProgressHandler.postDelayed(updateProgressTask, 100);
		}

	}

}