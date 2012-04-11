package de.codecentric.voicenotes.entity;

import java.util.Date;

import de.codecentric.spa.annotations.Entity;

/**
 * Class representing a single note.
 */
@Entity
public class Note extends DataEntity {

	public String title;
	public String text;
	public Date dueTime;
	public Boolean hasAlarm;
	public Boolean hasRecording;
	public String recordingPath;

	public Note() {
		super();
		title = "";
		text = "";
		timeCreated = null;
		dueTime = null;
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
