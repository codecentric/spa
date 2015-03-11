package de.codecentric.spa.tester;

import de.codecentric.spa.EntityWrapper;
import de.codecentric.spa.ctx.PersistenceContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button performanceTestButton, typeCheckTestButton;

	private EntityWrapper wrapper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        performanceTestButton = (Button) findViewById(R.id.performanceTestBtn);
        typeCheckTestButton = (Button) findViewById(R.id.typeCheckTestBtn);
	}

	@Override
	protected void onStart() {
		super.onStart();

		ButtonClickListener clickListener = new ButtonClickListener();
        performanceTestButton.setOnClickListener(clickListener);
        typeCheckTestButton.setOnClickListener(clickListener);

        wrapper = PersistenceContext.getInstance().getEntityWrapper();
	}

	@Override
	protected void onResume() {
		super.onResume();
		clearDatabase();
	}

	private void clearDatabase() {
		Class<?>[] classes = PersistenceContext.getInstance().getEntityMetaDataProvider()
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
            case R.id.performanceTestBtn:
                startActivity(new Intent(MainActivity.this, PerformanceTestActivity.class));
                break;
            case R.id.typeCheckTestBtn:
                startActivity(new Intent(MainActivity.this, TypeCheckActivity.class));
                break;
			}

		}

	}

}
