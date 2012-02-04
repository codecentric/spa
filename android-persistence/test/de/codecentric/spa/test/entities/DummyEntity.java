package de.codecentric.spa.test.entities;

import de.codecentric.spa.annotations.Entity;
import de.codecentric.spa.annotations.Id;
import de.codecentric.spa.annotations.Transient;

/**
 * Dummy Entity superclass used for testing purposes.
 */
@Entity
public class DummyEntity {

	private static final long serialVersionUID = 6448964292369068146L;

	@Id
	public long id;

	public String name;
	public int someInt;

	@Transient
	public String notPersisted;

	public DummyEntity() {
		super();
	}

}