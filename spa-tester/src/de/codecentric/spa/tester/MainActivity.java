package de.codecentric.spa.tester;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import de.codecentric.spa.EntityWrapper;
import de.codecentric.spa.ctx.PersistenceApplicationContext;

public class MainActivity extends Activity {

	private Button oneToManyButton, oneToOneButton;

	private EntityWrapper wrapper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		oneToManyButton = (Button) findViewById(R.id.oneToManyBtn);
		oneToOneButton = (Button) findViewById(R.id.oneToOneBtn);

		wrapper = ((PersistenceApplicationContext) getApplication()).getEntityWrapper();
	}

	@Override
	protected void onStart() {
		super.onStart();

		ButtonClickListener clickListener = new ButtonClickListener();
		oneToManyButton.setOnClickListener(clickListener);
		oneToOneButton.setOnClickListener(clickListener);
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

	/**
	 * {@link OnClickListener} implementation class.
	 */
	class ButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.oneToManyBtn:
				startActivity(new Intent(MainActivity.this, OneToManyActivity.class));
				break;
			case R.id.oneToOneBtn:
				startActivity(new Intent(MainActivity.this, OneToOneActivity.class));
			}

		}

	}

}
