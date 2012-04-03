package de.codecentric.voicenotes.context;

/**
 * Utility class containing useful constants used across whole application.
 */
public class Constants {

	public static final String DATE_FORMAT = "HH:mm yyyy-MM-dd";

	public static final long UI_POST_NOTIFICATION_DELAY = 2000L;

	/**
	 * Intent extra value name - value should contain notification text.
	 */
	public static final String EXTRA_NOTIFICATION_TEXT = "notification_text";

	/**
	 * Intent extra value name - value should contain notification title.
	 */
	public static final String EXTRA_NOTIFICATION_TITLE = "notification_title";

	/**
	 * Intent extra value name - value should contain name of a class to
	 * activate.
	 */
	public static final String EXTRA_NOTIFICATION_ACTIVITY_NAME = "notification_activity";

}
