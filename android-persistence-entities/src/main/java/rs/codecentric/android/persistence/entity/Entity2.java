package rs.codecentric.android.persistence.entity;

import java.util.List;

import de.codecentric.spa.annotations.CascadeType;
import de.codecentric.spa.annotations.Column;
import de.codecentric.spa.annotations.FetchType;
import de.codecentric.spa.annotations.Id;
import de.codecentric.spa.annotations.OneToMany;
import de.codecentric.spa.annotations.OneToOne;

@de.codecentric.spa.annotations.Entity
public class Entity2 {

	@Id
	public long id;

	public String entityTwoName;

	@OneToMany(fetch=FetchType.EAGER, cascade={CascadeType.ALL})
	@Column(name = "entity2_id")
	public List<EntityAttribute> attributes;

	@OneToOne
	public EntityExtension extension;

}
