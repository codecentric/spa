package de.codecentric.spa.test.entities;

import de.codecentric.spa.annotations.Entity;
import de.codecentric.spa.annotations.Id;
import de.codecentric.spa.annotations.Transient;

/**
 * Dummy Entity superclass used for testing purposes.
 */
@Entity
public class DummyEntity {

	@Id
	public Long id;

	public String name;
	public Integer someInt;

	@Transient
	public String notPersisted;

	public DummyEntity() {
		super();
	}

}