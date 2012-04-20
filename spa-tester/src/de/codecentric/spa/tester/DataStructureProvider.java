package de.codecentric.spa.tester;

import java.util.ArrayList;
import java.util.List;

import de.codecentric.spa.tester.entity.City;
import de.codecentric.spa.tester.entity.State;

public class DataStructureProvider {

	public static State getOneToManyForSave() {
		State state = new State("some state", 120.34);

		List<City> cities = new ArrayList<City>();
		cities.add(new City("city 1", 459830));
		cities.add(new City("city 2", 123545));
		cities.add(new City("city 3", 12422));
		cities.add(new City("city 4", 451887));
		state.cities = cities;
		
		state.capitol = new City("state_capitol_city", 10000);

		return state;
	}

}
