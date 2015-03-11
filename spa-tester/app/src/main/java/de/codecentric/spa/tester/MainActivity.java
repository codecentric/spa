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

	private Button oneToOneButton, oneToManyButton, manyToOneButton, performanceTestButton, typeCheckTestButton;

	private EntityWrapper wrapper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		oneToOneButton = (Button) findViewById(R.id.oneToOneBtn);
		oneToManyButton = (Button) findViewById(R.id.oneToManyBtn);
		manyToOneButton = (Button) findViewById(R.id.manyToOneBtn);
        performanceTestButton = (Button) findViewById(R.id.performanceTestBtn);
        typeCheckTestButton = (Button) findViewById(R.id.typeCheckTestBtn);
	}

	@Override
	protected void onStart() {
		super.onStart();

		ButtonClickListener clickListener = new ButtonClickListener();
		oneToManyButton.setOnClickListener(clickListener);
		oneToOneButton.setOnClickListener(clickListener);
		manyToOneButton.setOnClickListener(clickListener);
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
			case R.id.oneToOneBtn:
				startActivity(new Intent(MainActivity.this, OneToOneActivity.class));
				break;
			case R.id.oneToManyBtn:
				startActivity(new Intent(MainActivity.this, OneToManyActivity.class));
				break;
			case R.id.manyToOneBtn:
				startActivity(new Intent(MainActivity.this, ManyToOneActivity.class));
				break;
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
