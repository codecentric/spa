package de.codecentric.spa.tester;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.codecentric.spa.EntityWrapper;
import de.codecentric.spa.ctx.PersistenceApplicationContext;
import de.codecentric.spa.tester.context.SpaTesterApplicationContext;
import de.codecentric.spa.tester.entity.City;
import de.codecentric.spa.tester.entity.Government;
import de.codecentric.spa.tester.entity.State;

/**
 * OneToOneActivity is used to test different database operations over data-structures containing fields with ONE-TO-ONE relationship.
 */
public class OneToOneActivity extends Activity {

	private Button executeButton;
	private TextView executionText;

	private EntityWrapper wrapper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.one_to_one);

		executeButton = (Button) findViewById(R.id.executeOneToOneBtn);
		executionText = (TextView) findViewById(R.id.executionOneToOneText);

		wrapper = ((PersistenceApplicationContext) getApplication()).getEntityWrapper();
	}

	@Override
	protected void onStart() {
		super.onStart();
		executeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (executionText.getText().length() != 0) {
					logMessage("\n");
				}
				logMessage("Starting new test...\n");

				// check database state, it should be empty
				Assert.assertTrue(wrapper.listAll(State.class).size() == 0);
				logMessage("Table 'state' is empty.\n");
				Assert.assertTrue(wrapper.listAll(City.class).size() == 0);
				logMessage("Table 'city' is empty.\n");

				// create new state with its structure
				State state = DataStructureProvider.getDataStructure();

				// save state structure
				logMessage("Saving 'state' instance.\n");
				wrapper.saveOrUpdate(state);
				logMessage("Saved 'state' instance:\n" + state.toString());

				List<City> persistedCities1 = wrapper.findBy("state_cities_fk = " + state.id, City.class);
				Assert.assertNotNull(persistedCities1);

				// check new database state
				Assert.assertTrue(state.id != 0L);
				Assert.assertTrue(wrapper.listAll(State.class).size() == 1);
				Assert.assertTrue(wrapper.listAll(City.class).size() == 5);

				State persistedState = wrapper.findById(state.id, State.class);
				Assert.assertNotNull(persistedState);
				Assert.assertNull(persistedState.capitol);

				// modify structure
				logMessage("Modifying 'state' structure, deleting and adding new capitol city, modifying persisted capitol, change state name, last updated...\n");

				// delete capitol city from state
				wrapper.deleteBy("name = 'state_capitol_city'", City.class);
				City newCity = new City("new capitol city", 123456);
				persistedState.name = "new state name";
				persistedState.lastUpdated = new Date();
				persistedState.capitol = newCity;

				// load persisted government
				Government persistedGovernment = wrapper.executeSelect("select g.* from government g, state s where g.id = s.state_government_fk and s.id = "
						+ persistedState.id, Government.class);
				Assert.assertNotNull(persistedGovernment);
				persistedState.government = persistedGovernment;

				// load persisted cities
				List<City> persistedCities = wrapper.findBy("state_cities_fk = " + persistedState.id, City.class);
				Assert.assertNotNull(persistedCities);
				Assert.assertFalse(persistedCities.isEmpty());
				persistedState.cities = persistedCities;

				logMessage("Saving modified 'state' structure...\n");
				wrapper.saveOrUpdate(persistedState);
				logMessage("Saved modified 'state' structure:\n" + persistedState.toString());

				// load persisted capitol
				City persistedCapitol = wrapper.findBy("state_capitol_fk = " + persistedState.id, City.class).get(0);
				Assert.assertNotNull(persistedCapitol);
				persistedState.capitol = persistedCapitol;

				// modify structure
				logMessage("Modifying 'state' structure, change capitol city name and population\n");
				persistedState.lastUpdated = new Date();
				persistedState.capitol.name = "changed_capitol_name";
				persistedState.capitol.population = 1001;
				logMessage("Saving modified 'state' structure...\n");
				wrapper.saveOrUpdate(persistedState);
				logMessage("Number of cities: " + wrapper.listAll(City.class).size() + "\n");
				logMessage("Saved modified 'state' structure:\n" + persistedState.toString());

				// do some deleting
				logMessage("Deleting capitol city.\n");
				wrapper.delete(persistedState.capitol.id, City.class);
				Assert.assertTrue(wrapper.listAll(City.class).size() == 4);
				logMessage("Deleting capitol city went as expected.\n");

				// delete given structure
				logMessage("Deleting state (and substructure) with id: " + persistedState.id + "\n");
				wrapper.delete(persistedState.id, State.class);
				logMessage("Deleted state (and substructure) with id: " + persistedState.id + "\n");

				// check database structure, it should be empty again
				Assert.assertTrue(wrapper.listAll(State.class).size() == 0);
				logMessage("Table 'state' is empty.\n");
				Assert.assertTrue(wrapper.listAll(City.class).size() == 0);
				logMessage("Table 'city' is empty.\n");

				Toast.makeText(OneToOneActivity.this, "Test passed", 1000).show();
				SpaTesterApplicationContext.resetIdentationLevel();
			}
		});
	}

	private void logMessage(String message) {
		executionText.setText(executionText.getText() + message);
	}

}
