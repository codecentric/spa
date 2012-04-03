package de.codecentric.voicenotes.persistence.entity;

/**
 * Base entity class. All objects that should be persisted into database should
 * subclass this class.
 */
public abstract class DataEntity {

	public long id;

	/**
	 * Initializes an id property to zero value.
	 */
	public DataEntity() {
		this.id = 0;
	}

}
