package de.codecentric.spa.tester.entity;

import java.text.SimpleDateFormat;
import java.util.List;

import de.codecentric.spa.annotations.CascadeType;
import de.codecentric.spa.annotations.Entity;
import de.codecentric.spa.annotations.FetchType;
import de.codecentric.spa.annotations.OneToMany;
import de.codecentric.spa.tester.context.SpaTesterApplicationContext;

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

	@Override
	public String toString() {
		String prefix = SpaTesterApplicationContext.getIdentationPrefix();
		StringBuffer toString = new StringBuffer(prefix + "### State ###\n");

		prefix = SpaTesterApplicationContext.increaseIdentationLevel();

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss:SSS");

		toString.append(prefix + "id: " + id + "\n");
		toString.append(prefix + "timeCreated: " + sdf.format(timeCreated) + "\n");
		toString.append(prefix + "lastUpdated: " + sdf.format(lastUpdated) + "\n");
		toString.append(prefix + "name: " + name + "\n");
		toString.append(prefix + "bdp: " + bdp + "\n");

		toString.append(prefix + "cities:\n");
		if (cities != null) {
			prefix = SpaTesterApplicationContext.increaseIdentationLevel();
			for (City c : cities) {
				toString.append(c.toString());
			}
			SpaTesterApplicationContext.decreaseIdentationLevel();
		} else {
			toString.append("null");
		}

		SpaTesterApplicationContext.decreaseIdentationLevel();

		return toString.toString();
	}

}
