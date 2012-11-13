package de.codecentric.spa.tester;

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
import de.codecentric.spa.metadata.RelationshipMetaData;
import de.codecentric.spa.tester.context.SpaTesterApplicationContext;
import de.codecentric.spa.tester.entity.City;
import de.codecentric.spa.tester.entity.Government;
import de.codecentric.spa.tester.entity.State;

public class ManyToOneActivity extends Activity {

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

				// check new database state
				Assert.assertTrue(state.id != 0L);
				Assert.assertTrue(state.government.id != 0L);
				Assert.assertTrue(wrapper.listAll(State.class).size() == 1);
				Assert.assertTrue(wrapper.listAll(Government.class).size() == 1);

				State persisted = wrapper.findById(state.id, State.class);
				Assert.assertNotNull(persisted);
				Assert.assertNull(persisted.government);

				RelationshipMetaData rMetaData = ((PersistenceApplicationContext) getApplication()).getRelationshipMetaDataProvider().getMetaDataByField(
						State.class, "government");
				String condition = rMetaData.getForeignKeyColumnName() + " = ?";
				Assert.assertEquals(1, wrapper.findBy(condition,new String[]{String.valueOf(state.government.id)}, State.class).size());

				// modify structure
				logMessage("Modifying 'state' structure, changing name of the government.\n");
				state.government.name += " modified";
				logMessage("Saving modified 'state' structure.\n");
				wrapper.saveOrUpdate(state);
				logMessage("Saved modified 'state' structure.\n" + state.toString());

				// check database structure
				logMessage("Listing governments: \n");
				List<Government> governments = wrapper.listAll(Government.class);
				Assert.assertTrue(governments.size() == 1);
				logMessage(governments.get(0).toString());

				// modify structure
				logMessage("Modifying 'state' structure, setting new government.\n");
				state.government = new Government("Republic");
				logMessage("Saving modified 'state' structure.\n");
				wrapper.saveOrUpdate(state);
				logMessage("Saved modified 'state' structure.\n" + state.toString());

				// check database
				Assert.assertTrue(wrapper.listAll(Government.class).size() == 2);
				// previous should be 2, we didn't delete old government
				// -> we should've do it

				// do some deleting
				logMessage("Deleting governments with id < 2.\n");
				wrapper.deleteBy("id < ?",new String[]{"2"}, Government.class);
				Assert.assertTrue(wrapper.listAll(Government.class).size() == 1);
				logMessage("Deleting governments with id < 2 went as expected.\n");

				// delete given structure
				logMessage("Deleting state (and substructure) with id: " + state.id + "\n");
				wrapper.delete(state.id, State.class);
				logMessage("Deleted state (and substructure) with id: " + state.id + "\n");

				// check database structure, it should be empty again
				Assert.assertTrue(wrapper.listAll(State.class).size() == 0);
				logMessage("Table 'state' is empty.\n");
				// Table city should not be empty - it is the parent side
				// of one-to-one, it is not deleted when a state is deleted.
				Assert.assertTrue(wrapper.listAll(City.class).size() != 0);
				wrapper.deleteAll(City.class);
				logMessage("Table 'city' is empty.\n");
				// Table government should not be empty - it is the parent side
				// of many-to-one, it is not deleted when a state is deleted.
				Assert.assertTrue(wrapper.listAll(Government.class).size() == 1);
				// Do manual deletion of all governments...
				wrapper.deleteAll(Government.class);
				Assert.assertTrue(wrapper.listAll(Government.class).size() == 0);
				logMessage("Table 'government' is empty.\n");

				Toast.makeText(ManyToOneActivity.this, "Test passed", 1000).show();
				SpaTesterApplicationContext.resetIdentationLevel();
			}
		});
	}

	private void logMessage(String message) {
		executionText.setText(executionText.getText() + message);
	}

}
