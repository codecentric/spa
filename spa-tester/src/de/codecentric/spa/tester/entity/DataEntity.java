package de.codecentric.spa.tester.entity;

import java.util.Date;

import de.codecentric.spa.annotations.Id;

public abstract class DataEntity {

	@Id
	public Long id;

	public Date timeCreated;

	/**
	 * Initializes an id property to zero value.
	 */
	public DataEntity() {
		this.id = 0L;
	}

}
