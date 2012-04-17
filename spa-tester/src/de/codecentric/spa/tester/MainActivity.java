package de.codecentric.spa.tester;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import de.codecentric.spa.EntityWrapper;
import de.codecentric.spa.ctx.PersistenceApplicationContext;
import de.codecentric.spa.tester.entity.City;
import de.codecentric.spa.tester.entity.State;

public class MainActivity extends Activity {

	private Button oneToManyButton;

	private EntityWrapper wrapper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		oneToManyButton = (Button) findViewById(R.id.oneToManyBtn);

		wrapper = ((PersistenceApplicationContext) getApplication()).getEntityWrapper();
	}

	@Override
	protected void onStart() {
		super.onStart();

		oneToManyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				createAndShowOneToManyActionChooserDialog();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		clearDatabase();
	}

	private void clearDatabase() {
		Class<?>[] classes = ((PersistenceApplicationContext) getApplication()).getEntityMetaDataProvider()
				.getPersistentClasses();
		for (Class<?> cls : classes) {
			wrapper.deleteAll(cls);
		}
		Toast.makeText(this, "Database is cleared.", 1000).show();
	}

	private void createAndShowOneToManyActionChooserDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.choose_action);
		//
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View dialogView = inflater.inflate(R.layout.one_to_many_action_chooser_dialog, null);

		RadioButton[] radioArray = new RadioButton[4];
		radioArray[0] = (RadioButton) dialogView.findViewById(R.id.one_to_many_case_one_btn);
		radioArray[1] = (RadioButton) dialogView.findViewById(R.id.one_to_many_case_two_btn);

		fixRadioButtonsLayout(radioArray);
		builder.setView(dialogView);
		Dialog dialog = builder.create();
		attachOneToManyActionDialogRadioButtonListeners(dialog, radioArray);

		dialog.show();
	}

	private void fixRadioButtonsLayout(RadioButton... radioButtons) {
		final float scale = this.getResources().getDisplayMetrics().density;

		fixSingleRadioButtonLayout(radioButtons[0], scale);
		fixSingleRadioButtonLayout(radioButtons[1], scale);
	}

	private void fixSingleRadioButtonLayout(RadioButton radioBtn, float scale) {
		radioBtn.setPadding(radioBtn.getPaddingLeft() + (int) (10.0f * scale + 0.5f), radioBtn.getPaddingTop(),
				radioBtn.getPaddingRight(), radioBtn.getPaddingBottom());
	}

	private void attachOneToManyActionDialogRadioButtonListeners(Dialog dialog, RadioButton... radioButtons) {
		OnClickListener actionChooserClickListener = new OneToManyActionChooserClickListener(dialog);
		radioButtons[0].setOnClickListener(actionChooserClickListener);
		radioButtons[1].setOnClickListener(actionChooserClickListener);
	}

	private class OneToManyActionChooserClickListener implements View.OnClickListener {

		private Dialog dialog;

		protected OneToManyActionChooserClickListener(Dialog dialog) {
			this.dialog = dialog;
		}

		@Override
		public void onClick(View view) {
			RadioButton button = (RadioButton) view;
			if (button.getId() == R.id.one_to_many_case_one_btn) {
				doCase1();
			} else if (button.getId() == R.id.one_to_many_case_two_btn) {

			}

			dialog.cancel();
		}
	}

	private void doCase1() {
		State state = new State("some state", 120.34);

		List<City> cities = new ArrayList<City>();
		cities.add(new City("city 1", 459830));
		cities.add(new City("city 2", 123545));
		cities.add(new City("city 3", 12422));
		cities.add(new City("city 4", 451887));
		state.cities = cities;

		Assert.assertTrue(wrapper.listAll(State.class).size() == 0);
		Assert.assertTrue(wrapper.listAll(City.class).size() == 0);
		wrapper.saveOrUpdate(state);

		Assert.assertTrue(state.id != 0L);
		Assert.assertTrue(wrapper.listAll(State.class).size() == 1);
		Assert.assertTrue(wrapper.listAll(City.class).size() == 4);

		// String newName = "new state name";
		// state.name = newName;
		// wrapper.saveOrUpdate(state);
		// Assert.assertTrue(newName.equals(wrapper.findById(state.id,
		// State.class)));

		wrapper.delete(state.id, State.class);
		Assert.assertTrue(wrapper.listAll(State.class).size() == 0);
		Assert.assertTrue(wrapper.listAll(City.class).size() == 0);

		Toast.makeText(this, "Test passed", 1000).show();
	}

}
