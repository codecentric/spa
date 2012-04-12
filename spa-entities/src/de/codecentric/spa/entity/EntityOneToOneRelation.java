package de.codecentric.spa.entity;

import de.codecentric.spa.annotations.Id;
import de.codecentric.spa.annotations.OneToOne;

@de.codecentric.spa.annotations.Entity
public class EntityOneToOneRelation {

	@Id
	public long id;

	public String entityName;

	@OneToOne
	public EntityExtension extension;

}
