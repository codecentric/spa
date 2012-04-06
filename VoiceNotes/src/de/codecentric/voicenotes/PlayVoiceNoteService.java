package de.codecentric.voicenotes;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

public class PlayVoiceNoteService extends Service {

	public static final String FILE_URI_PARAM = "fileUri";

	private MediaPlayer mediaPlayer;
	private Uri fileUri;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		fileUri = (Uri) intent.getExtras().get(FILE_URI_PARAM);
		mediaPlayer = MediaPlayer.create(this, fileUri);
		mediaPlayer.start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		fileUri = null;
		mediaPlayer.release();
		mediaPlayer = null;
	}

	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	public long getCurrentPositionInMedia() {
		return mediaPlayer.getCurrentPosition();
	}

	public long getMediaDuration() {
		return mediaPlayer.getDuration();
	}

}
