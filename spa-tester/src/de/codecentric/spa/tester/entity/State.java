package de.codecentric.spa.tester.entity;

import java.text.SimpleDateFormat;
import java.util.List;

import de.codecentric.spa.annotations.Entity;
import de.codecentric.spa.annotations.ManyToOne;
import de.codecentric.spa.annotations.OneToMany;
import de.codecentric.spa.annotations.OneToOne;
import de.codecentric.spa.tester.context.SpaTesterApplicationContext;

@Entity
public class State extends DataEntity {

	public String name;
	public Double bdp;
	
	@OneToOne
	public City capitol;

	@OneToMany
	public List<City> cities;

	@ManyToOne
	public Government government;

	public State() {
		super();
	}

	public State(String name, Double bdp, Government government) {
		super();
		this.name = name;
		this.bdp = bdp;
		this.government = government;
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
		toString.append(prefix + "capitol:\n");

		if (capitol != null) {
			toString.append(capitol.toString());
		} else {
			toString.append("null\n");
		}

		toString.append(prefix + "government:\n");
		if (government != null) {
			prefix = SpaTesterApplicationContext.increaseIdentationLevel();
			toString.append(government.toString());
			SpaTesterApplicationContext.decreaseIdentationLevel();
		} else {
			toString.append("null\n");
		}

		toString.append(prefix + "cities:\n");

		if (cities != null) {
			for (City c : cities) {
				toString.append(c.toString());
			}
		} else {
			toString.append("null\n");
		}

		SpaTesterApplicationContext.decreaseIdentationLevel();

		return toString.toString();
	}
}
