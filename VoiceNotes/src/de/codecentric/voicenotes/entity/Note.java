package de.codecentric.voicenotes.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.codecentric.spa.annotations.CascadeType;
import de.codecentric.spa.annotations.Entity;
import de.codecentric.spa.annotations.OneToMany;

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
	@OneToMany(cascade = { CascadeType.ALL })
	public List<Comment> comments = new ArrayList<Comment>();

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
