package rs.codecentric.android.persistence.entity;

import de.codecentric.spa.annotations.Id;
import de.codecentric.spa.annotations.Transient;

@de.codecentric.spa.annotations.Entity
public class Entity {

	@Id
	public long id;

	public String name;
	public boolean isOrIsNot;
	public int someInt;

	@Transient
	public String notPersisted;

}
