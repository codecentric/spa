package de.codecentric.spa.entity;

import de.codecentric.spa.annotations.Id;

@de.codecentric.spa.annotations.Entity
public class Parent {

	@Id
	public long id;

	public String name;

}
