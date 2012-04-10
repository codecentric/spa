package de.codecentric.voicenotes.entity;

import java.util.Date;

import de.codecentric.spa.annotations.Id;

/**
 * Base entity class. All objects that should be persisted into database should
 * subclass this class.
 */
public abstract class DataEntity {

	@Id
	public long id;

	public Date timeCreated;

	/**
	 * Initializes an id property to zero value.
	 */
	public DataEntity() {
		this.id = 0;
	}

}
