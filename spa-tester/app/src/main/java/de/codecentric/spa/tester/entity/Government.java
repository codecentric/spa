package de.codecentric.spa.tester.entity;

import java.text.SimpleDateFormat;

import de.codecentric.spa.annotations.Entity;
import de.codecentric.spa.tester.context.SpaTesterApplicationContext;

@Entity
public class Government extends DataEntity {

	public String name;

	public Government() {
		super();
	}

	public Government(String name) {
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		String prefix = SpaTesterApplicationContext.getIndentationPrefix();
		StringBuffer toString = new StringBuffer(prefix + "### Government ###\n");

		prefix = SpaTesterApplicationContext.increaseIndentationLevel();

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss:SSS");

		toString.append(prefix + "id: " + id + "\n");
		toString.append(prefix + "timeCreated: " + sdf.format(timeCreated) + "\n");
		toString.append(prefix + "lastUpdated: " + sdf.format(lastUpdated) + "\n");
		toString.append(prefix + "name: " + name + "\n");

		SpaTesterApplicationContext.decreaseIndentationLevel();

		return toString.toString();
	}

}
