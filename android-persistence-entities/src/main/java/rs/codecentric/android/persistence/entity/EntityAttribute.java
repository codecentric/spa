package rs.codecentric.android.persistence.entity;

import de.codecentric.spa.annotations.Column;
import de.codecentric.spa.annotations.Id;
import de.codecentric.spa.annotations.ManyToOne;

@de.codecentric.spa.annotations.Entity
public class EntityAttribute {

	@Id
	public long id;

	public String name;

	public String value;

	@ManyToOne
	@Column(name = "entity_id")
	public Entity entityParent;

}
