package de.codecentric.spa.entity;

import java.util.List;

import de.codecentric.spa.annotations.Column;
import de.codecentric.spa.annotations.Id;
import de.codecentric.spa.annotations.OneToMany;
import de.codecentric.spa.annotations.OneToOne;

@de.codecentric.spa.annotations.Entity
public class Entity2 {

	@Id
	public long id;

	public String entityTwoName;

	@OneToMany
	@Column(name = "entity2_id")
	public List<EntityAttribute> attributes;

	@OneToOne
	public EntityExtension extension;

}
