package de.codecentric.spa.tester.entity;

import de.codecentric.spa.annotations.Entity;

@Entity
public class City extends DataEntity {

	public String name;
	public Integer population;

	public City() {
		super();
	}

	public City(String name, Integer population) {
		super();
		this.name = name;
		this.population = population;
	}

}
