package de.codecentric.spa.tester.entity;

import java.text.SimpleDateFormat;

import de.codecentric.spa.annotations.Entity;
import de.codecentric.spa.tester.context.SpaTesterApplicationContext;

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

	@Override
	public String toString() {
		String prefix = SpaTesterApplicationContext.getIdentationPrefix();
		StringBuffer toString = new StringBuffer(prefix + "### City ###\n");

		prefix = SpaTesterApplicationContext.increaseIdentationLevel();

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss:SSS");

		toString.append(prefix + "id: " + id + "\n");
		toString.append(prefix + "timeCreated: " + sdf.format(timeCreated) + "\n");
		toString.append(prefix + "lastUpdated: " + sdf.format(lastUpdated) + "\n");
		toString.append(prefix + "name: " + name + "\n");
		toString.append(prefix + "population: " + population + "\n");

		SpaTesterApplicationContext.decreaseIdentationLevel();

		return toString.toString();
	}

}
