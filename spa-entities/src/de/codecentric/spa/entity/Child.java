package de.codecentric.spa.entity;

import de.codecentric.spa.annotations.Column;
import de.codecentric.spa.annotations.Id;
import de.codecentric.spa.annotations.ManyToOne;

@de.codecentric.spa.annotations.Entity
public class Child {

	@Id
	public long id;

	public String name;

	public String value;

	@ManyToOne
	@Column(name = "parent_id")
	public Parent parent;

}
