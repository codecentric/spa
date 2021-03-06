package de.codecentric.voicenotes.entity;

import de.codecentric.spa.annotations.Entity;

@Entity
public class Comment extends DataEntity {

	public String text;

	public Comment() {
		super();
	}

	@Override
	public String toString() {
		return text;
	}

	/**
	 * Class defining constants related to {@link Comment}.
	 */
	public static class Extras {

		public static final String EXTRA_COMMENT_ID = "commentId";

	}
}
