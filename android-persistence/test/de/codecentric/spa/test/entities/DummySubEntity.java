package de.codecentric.spa.test.entities;

import java.util.List;

import de.codecentric.spa.annotations.Column;
import de.codecentric.spa.annotations.Entity;
import de.codecentric.spa.annotations.OneToMany;
import de.codecentric.spa.annotations.OneToOne;

/**
 * Dummy sub-entity class extending DummyEntity class used for testing purposes.
 */
@Entity
public class DummySubEntity extends DummyEntity {

	private static final long serialVersionUID = -9025885843231630281L;

	public double subFldDbl;

	@OneToMany
	public List<DummyAttribute> attributes;

	@OneToMany
	@Column(name = "dummy_sub_entity_id")
	public List<DummyAttribute> attributes2;

	@OneToOne
	public DummyExtension extension;

}
