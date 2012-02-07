package rs.codecentric.android.persistence.entity;

import de.codecentric.spa.annotations.CascadeType;
import de.codecentric.spa.annotations.Column;
import de.codecentric.spa.annotations.FetchType;
import de.codecentric.spa.annotations.Id;
import de.codecentric.spa.annotations.ManyToOne;


@de.codecentric.spa.annotations.Entity
public class Child {

	@Id
	public long id;

	public String name;

	public String value;

	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@Column(name = "parent_id")
	public Parent parent;

}
