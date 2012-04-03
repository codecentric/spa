package de.codecentric.voicenotes.persistence.entity;

/**
 * Class representing a single note.
 */
public class Note extends DataEntity {

	public String title;
	public String text;
	public String timeCreated;
	public String dueTime;
	public boolean hasAlarm;
	public boolean hasRecording;
	public String recordingPath;

	public Note() {
		super();
		title = "";
		text = "";
		timeCreated = "";
		dueTime = "";
		hasAlarm = false;
		hasRecording = false;
		recordingPath = "";
	}

	/**
	 * Class defining constants related to {@link Note}.
	 */
	public static class Extras {

		public static final String EXTRA_NOTE_ID = "noteId";

	}

}
