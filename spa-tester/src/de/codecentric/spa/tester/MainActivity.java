package de.codecentric.spa.tester;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import de.codecentric.spa.EntityWrapper;
import de.codecentric.spa.ctx.PersistenceApplicationContext;

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
				startActivity(new Intent(MainActivity.this, OneToManyActivity.class));
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

}
