package de.codecentric.voicenotes.entity;

import de.codecentric.spa.annotations.Id;

/**
 * Base entity class. All objects that should be persisted into database should
 * subclass this class.
 */
public abstract class DataEntity {

	@Id
	public long id;

	/**
	 * Initializes an id property to zero value.
	 */
	public DataEntity() {
		this.id = 0;
	}

}
