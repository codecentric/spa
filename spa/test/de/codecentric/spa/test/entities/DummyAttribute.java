package de.codecentric.spa.test.entities;

import de.codecentric.spa.annotations.Column;
import de.codecentric.spa.annotations.Entity;
import de.codecentric.spa.annotations.Id;
import de.codecentric.spa.annotations.ManyToOne;

@Entity
public class DummyAttribute {

	@Id
	public Long id;

	public String dummyName;
	public String dummyValue;

	@ManyToOne
	public DummySubEntity parentSubEntity;

	@ManyToOne
	@Column(name = "parent_id")
	public DummySubEntity parentSubEntity2;

}
