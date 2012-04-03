package de.codecentric.voicenotes.persistence;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import de.codecentric.voicenotes.persistence.entity.DataEntity;
import de.codecentric.voicenotes.persistence.entity.Note;

/**
 * Helper class used for database operations regarding notes persisted by the
 * application.
 */
public class NoteEntityHelper extends EntityHelper {

	public NoteEntityHelper() {
		super();
		tableName = "note";

		createTableSQL = "CREATE TABLE "
				+ tableName
				+ " ("
				+ BaseColumns._ID
				+ " INTEGER PRIMARY KEY, title TEXT, note_text TEXT, time_created TEXT, due_time TEXT, has_alarm INTEGER, has_recording INTEGER, recording_path TEXT)";

		dropTableSQL = "DROP TABLE IF EXISTS " + tableName;

		insertStmtSQL = "INSERT INTO "
				+ tableName
				+ " (title, note_text, time_created, due_time, has_alarm, has_recording, recording_path)"
				+ " VALUES (?, ?, ?, ?, ?, ?, ?)";

		updateStmtSQL = "UPDATE "
				+ tableName
				+ " SET title = ?, note_text = ?, time_created = ?, due_time = ?, has_alarm = ?, has_recording = ?, recording_path = ? WHERE "
				+ BaseColumns._ID + " = ?";

		selectStmtSQL = "SELECT " + BaseColumns._ID + ", "
				+ "title, note_text, time_created, due_time, has_alarm, "
				+ "has_recording, recording_path FROM " + tableName;

		selectSingleStmtSQL = "SELECT " + BaseColumns._ID + ", "
				+ "title, note_text, time_created, due_time, has_alarm, "
				+ "has_recording, recording_path FROM " + tableName + " WHERE "
				+ BaseColumns._ID + " = ?";
	}

	/**
	 * Method persists note into database.
	 * 
	 * @param data
	 */
	public void saveOrUpdate(final Note data) {
		if (data.id != 0) {
			fillUpdateStatement(data);
			updateStmt.execute();
		} else {
			fillInsertStatement(data);
			data.id = insertStmt.executeInsert();
		}
	}

	/**
	 * {@link EntityHelper#fillUpdateStatement(DataEntity)}
	 */
	@Override
	protected void fillUpdateStatement(DataEntity data) {
		Note aNote = (Note) data;
		updateStmt.bindString(1, aNote.title);
		updateStmt.bindString(2, aNote.text);
		updateStmt.bindString(3, aNote.timeCreated);
		updateStmt.bindString(4, aNote.dueTime != null ? aNote.dueTime : "");
		updateStmt.bindLong(5, aNote.hasAlarm ? 1 : 0);
		updateStmt.bindLong(6, aNote.hasRecording ? 1 : 0);
		updateStmt.bindString(7, aNote.recordingPath);
		updateStmt.bindLong(8, aNote.id);

	}

	/**
	 * {@link EntityHelper#fillUpdateStatement(DataEntity)}
	 */
	@Override
	protected void fillInsertStatement(DataEntity data) {
		Note aNote = (Note) data;
		insertStmt.bindString(1, aNote.title);
		insertStmt.bindString(2, aNote.text);
		insertStmt.bindString(3, aNote.timeCreated);
		insertStmt.bindString(4, aNote.dueTime != null ? aNote.dueTime : "");
		insertStmt.bindLong(5, aNote.hasAlarm ? 1 : 0);
		insertStmt.bindLong(6, aNote.hasRecording ? 1 : 0);
		insertStmt.bindString(7, aNote.recordingPath);
	}

	/**
	 * Method loads note with specified id. If not found, returns null.
	 * 
	 * @param db
	 *            database in use
	 * @param id
	 * @return textual note instance or null if not found
	 */
	public Note loadTextualNote(SQLiteDatabase db, long id) {
		Note data = null;

		Cursor c = db.rawQuery(selectSingleStmtSQL,
				new String[] { String.valueOf(id) });
		if (c.moveToFirst()) {
			data = new Note();
			data.id = c.getLong(0);
			data.title = c.getString(1);
			data.text = c.getString(2);
			data.timeCreated = c.getString(3);
			data.dueTime = c.getString(4);
			data.hasAlarm = c.getLong(5) == 1L;
			data.hasRecording = c.getLong(6) == 1L;
			data.recordingPath = c.getString(7);
		}
		c.close();

		return data;
	}

	/**
	 * Method loads all notes with specified id. If not found, returns null.
	 * 
	 * @param db
	 *            database in use
	 * @return textual note instance or null if not found
	 */
	public List<Note> listAllNotes(SQLiteDatabase db) {
		List<Note> notes = new ArrayList<Note>(0);

		Cursor c = db.rawQuery(selectStmtSQL, new String[] {});
		while (c.moveToNext()) {
			Note data = new Note();
			data.id = c.getLong(0);
			data.title = c.getString(1);
			data.text = c.getString(2);
			data.timeCreated = c.getString(3);
			data.dueTime = c.getString(4);
			data.hasAlarm = c.getLong(5) == 1L;
			data.hasRecording = c.getLong(6) == 1L;
			data.recordingPath = c.getString(7);
			notes.add(data);
		}
		c.close();

		return notes;
	}

	/**
	 * {@link EntityHelper#close()}
	 */
	@Override
	public void close() {
		updateStmt.close();
		insertStmt.close();
	}

	/**
	 * {@link EntityHelper#compileSQLStatements(SQLiteDatabase)}
	 */
	@Override
	public void compileSQLStatements(SQLiteDatabase db) {
		updateStmt = db.compileStatement(updateStmtSQL);
		insertStmt = db.compileStatement(insertStmtSQL);
	}

}
