package de.codecentric.spa.tester;

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
import de.codecentric.spa.tester.entity.State;

public class OneToManyActivity extends Activity {

	private Button executeButton;
	private TextView executionText;

	private EntityWrapper wrapper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.one_to_many);

		executeButton = (Button) findViewById(R.id.executeBtn);
		executionText = (TextView) findViewById(R.id.executionText);

		wrapper = ((PersistenceApplicationContext) getApplication()).getEntityWrapper();
	}

	@Override
	protected void onStart() {
		super.onStart();
		executeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// create new state with its structure
				State state = DataStructureProvider.getOneToManyForSave();

				// check database state, it should be empty
				Assert.assertTrue(wrapper.listAll(State.class).size() == 0);
				logMessage("Table 'state' is empty.\n");
				Assert.assertTrue(wrapper.listAll(City.class).size() == 0);
				logMessage("Table 'city' is empty.\n");

				// save state structure
				logMessage("Saving 'state' instance.\n");
				wrapper.saveOrUpdate(state);
				logMessage("Saved 'state' instance:\n" + state.toString());

				// check new database state
				Assert.assertTrue(state.id != 0L);
				Assert.assertTrue(wrapper.listAll(State.class).size() == 1);
				Assert.assertTrue(wrapper.listAll(City.class).size() == 4);

				// // modify structure
				// logMessage("Modifying 'state' structure, adding new city.\n");
				// City newCity = new City("new city", 123456);
				// state.cities.add(newCity);
				// logMessage("Saving modified 'state' structure.\n");
				// wrapper.saveOrUpdate(state);
				// logMessage("Saved modified 'state' structure.\n");
				//
				// // check database structure
				// Assert.assertTrue(wrapper.listAll(City.class).size() == 5);

				// delete given structure
				logMessage("Deleting state (and substructure) with id: " + state.id + "\n");
				wrapper.delete(state.id, State.class);
				logMessage("Deleted state (and substructure) with id: " + state.id + "\n");

				// check database structure, it should be empty again
				Assert.assertTrue(wrapper.listAll(State.class).size() == 0);
				logMessage("Table 'state' is empty.\n");
				Assert.assertTrue(wrapper.listAll(City.class).size() == 0);
				logMessage("Table 'city' is empty.\n");

				Toast.makeText(OneToManyActivity.this, "Test passed", 1000).show();
				SpaTesterApplicationContext.resetIdentationLevel();
			}
		});
	}

	private void logMessage(String message) {
		executionText.setText(executionText.getText() + message);
	}

}
