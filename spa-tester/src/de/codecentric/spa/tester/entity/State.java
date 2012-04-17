package de.codecentric.spa.tester.entity;

import java.util.List;

import de.codecentric.spa.annotations.CascadeType;
import de.codecentric.spa.annotations.Entity;
import de.codecentric.spa.annotations.FetchType;
import de.codecentric.spa.annotations.OneToMany;

@Entity
public class State extends DataEntity {

	public String name;
	public Double bdp;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	public List<City> cities;

	public State() {
		super();
	}

	public State(String name, Double bdp) {
		super();
		this.name = name;
		this.bdp = bdp;
	}

}
