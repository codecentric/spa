package de.codecentric.spa.ctx;

import android.app.Application;
import de.codecentric.spa.EntityHelper;
import de.codecentric.spa.EntityWrapper;
import de.codecentric.spa.metadata.EntityMetaData;
import de.codecentric.spa.metadata.EntityMetaDataProvider;
import de.codecentric.spa.metadata.EntityScanner;
import de.codecentric.spa.metadata.RelationshipMetaDataProvider;
import de.codecentric.spa.sql.SQLGenerator;
import de.codecentric.spa.sql.SQLGenerator.SQLStatements;
import de.codecentric.spa.sql.SQLProvider;

/**
 * Base class for maintaining global application state and sharing of objects
 * used across the whole application.
 */
public abstract class PersistenceApplicationContext extends Application {

	/**
	 * Database helper.
	 */
	protected DatabaseHelper dbHelper;

	protected SQLProvider sqlProvider;
	protected EntityMetaDataProvider entityMetaDataProvider;
	protected RelationshipMetaDataProvider relationshipMetaDataProvider;
	protected EntityWrapper entityWrapper;

	/**
	 * Constructs the instance of application context and initializes it.
	 * 
	 * After this constructor is called, if everything went well, the created
	 * instance has initialized {@link SQLProvider} and
	 * {@link EntityMetaDataProvider}. Those can be retrieved anywhere in
	 * application via methods
	 * {@link PersistenceApplicationContext#getSQLProvider()} and
	 * {@link PersistenceApplicationContext#getEntityMetaDataProvider()}.
	 */
	public PersistenceApplicationContext() {
		super();
		sqlProvider = SQLProvider.getInstance();
		entityMetaDataProvider = EntityMetaDataProvider.getInstance();
		relationshipMetaDataProvider = RelationshipMetaDataProvider.getInstance();
		entityWrapper = EntityWrapper.getInstance(this);
	}

	/**
	 * Method triggers the scanning process in order to obtain
	 * {@link EntityMetaData} and {@link SQLStatements} for given class.
	 * 
	 * {@link EntityScanner} scans given class and returns appropriate
	 * {@link EntityMetaData} instance which is used by {@link SQLGenerator} to
	 * generate {@link SQLStatements}.
	 * 
	 * Generated {@link SQLStatements} are stored into the {@link SQLProvider}
	 * of this application context and can be retrieved later using
	 * {@link SQLProvider#getSQL(Class)} method.
	 * 
	 * @param cls
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void inspectClass(Class<?> cls) {
		EntityScanner.scanClass(cls, true);
		if (EntityScanner.isPersistentClass(cls)) {
			entityWrapper.putEntityHelper(cls, new EntityHelper(this, cls));
		}
	}

	/**
	 * Method returns the {@link SQLProvider} used by this application context.
	 * 
	 * @return {@link SQLProvider} used by this application context
	 */
	public SQLProvider getSQLProvider() {
		return sqlProvider;
	}

	/**
	 * Method returns the {@link EntityMetaDataProvider} used by this
	 * application context.
	 * 
	 * @return {@link EntityMetaDataProvider} used by this application context
	 */
	public EntityMetaDataProvider getEntityMetaDataProvider() {
		return entityMetaDataProvider;
	}

	/**
	 * Method returns the {@link RelationshipMetaDataProvider} used by this
	 * application context.
	 * 
	 * @return {@link RelationshipMetaDataProvider} used by this application
	 *         context
	 */
	public RelationshipMetaDataProvider getRelationshipMetaDataProvider() {
		return relationshipMetaDataProvider;
	}

	/**
	 * Method returns {@link DatabaseHelper} used in this application context.
	 * 
	 * @return {@link DatabaseHelper} used in this application context
	 */
	public DatabaseHelper getDatabaseHelper() {
		return dbHelper;
	}

	/**
	 * Method returns {@link EntityWrapper} used in this application context.
	 * 
	 * @return {@link EntityWrapper} used in this application context
	 */
	public EntityWrapper getEntityWrapper() {
		return entityWrapper;
	}

	/**
	 * Method returns {@link EntityHelper} used for given class.
	 * 
	 * @param cls
	 * @return {@link EntityHelper} used for given class
	 */
	@SuppressWarnings("rawtypes")
	public EntityHelper getEntityHelper(Class<?> cls) {
		return entityWrapper.getEntityHelper(cls);
	}

}
