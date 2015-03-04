package de.codecentric.spa.metadata;

import de.codecentric.spa.annotations.CascadeType;
import de.codecentric.spa.annotations.FetchType;

/**
 * Class not used currently.
 * 
 * Class describing the relationship between two objects.
 */
@Deprecated
public class RelationshipInfo {

	private CascadeType[] cascade;

	private FetchType fetch;

	public CascadeType[] getCascade() {
		return cascade;
	}

	public void setCascade(CascadeType[] cascade) {
		this.cascade = cascade;
	}

	public FetchType getFetch() {
		return fetch;
	}

	public void setFetch(FetchType fetch) {
		this.fetch = fetch;
	}

}
