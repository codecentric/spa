package de.codecentric.voicenotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import de.codecentric.voicenotes.context.ApplicationContext;

public class SplashScreenActivity extends PersistenceActivity {

	private boolean isFirstRun;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Initialize database state on first application run.
		SharedPreferences settings = getSharedPreferences(
				ApplicationContext.PREFS_NAME, 0);
		isFirstRun = settings.getBoolean(
				ApplicationContext.APP_FIRST_RUN_CHECK, true);
		if (isFirstRun) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(ApplicationContext.APP_FIRST_RUN_CHECK, false);
			editor.commit();
		}

		final int welcomeScreenDisplay = 1000;
		Thread welcomeThread = new Thread() {
			int wait = 0;

			@Override
			public void run() {
				try {
					super.run();
					/*
					 * Use while to get the splash time. Use sleep() to increase
					 * the wait variable for every 100L.
					 */
					while (wait < welcomeScreenDisplay) {
						sleep(100);
						wait += 100;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					/*
					 * Called after splash times up. Do some action after splash
					 * times up. Here we moved to another main activity class
					 */
					startActivity(new Intent(SplashScreenActivity.this,
							RecordActivity.class));
					finish();
				}
			}
		};
		welcomeThread.start();
	}

}
