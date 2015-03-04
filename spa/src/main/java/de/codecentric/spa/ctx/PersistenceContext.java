package de.codecentric.spa.ctx;

import android.content.Context;
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
 * Base class for maintaining state and sharing of objects used for persistence.
 */
public class PersistenceContext {

    private static PersistenceContext persistenceContext = null;

    /**
     * Database helper.
     */
    protected DatabaseHelper dbHelper;

    protected SQLProvider sqlProvider;
    protected EntityMetaDataProvider entityMetaDataProvider;
    protected RelationshipMetaDataProvider relationshipMetaDataProvider;
    protected EntityWrapper entityWrapper;

    /**
     * Constructs the instance of persistence context.
     * 
     * After this constructor is called, if everything went well, the created instance has initialized {@link SQLProvider} and {@link EntityMetaDataProvider}. Those can be retrieved anywhere in
     * application via methods {@link PersistenceContext#getSQLProvider()} and {@link PersistenceContext#getEntityMetaDataProvider()}.
     */
    private PersistenceContext() {
        super();
        sqlProvider = SQLProvider.getInstance();
        entityMetaDataProvider = EntityMetaDataProvider.getInstance();
        relationshipMetaDataProvider = RelationshipMetaDataProvider.getInstance();
        entityWrapper = EntityWrapper.getInstance(this);
    }

    /**
     * Method scans given class names and initializes persistence context of the application. Every call to this method will destroy previously initialized persistence context and create a new one.
     * 
     * @param context
     *            application context
     * @param classNames
     *            array of class names to scan
     * @return initialized persistence context
     */
    public static PersistenceContext init(Context context, String[] classNames) {
        try {
            persistenceContext = new PersistenceContext();

            // first do the scan...
            if (classNames != null && classNames.length != 0) {
                for (String className : classNames) {
                    Class<?> cls = Class.forName(className);
                    persistenceContext.inspectClass(cls);
                }
            }

            // ... generate SQL for scanned classes...
            Class<?>[] classes = persistenceContext.entityMetaDataProvider.getPersistentClasses();
            if (classes != null && classes.length > 0) {
                for (Class<?> cls : classes) {
                    EntityMetaData metaData = persistenceContext.entityMetaDataProvider.getMetaData(cls);
                    if (metaData != null && metaData.hasStructure()) {
                        SQLStatements sql = SQLGenerator.generateSQL(metaData);
                        persistenceContext.sqlProvider.addSQL(cls, sql);
                    }

                    // ... instantiate entity helpers for every persistent class
                    // ... when SQL statements are ready

                    persistenceContext.entityWrapper.putEntityHelper(cls, new EntityHelper(persistenceContext, cls));
                }
            }

            return persistenceContext;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method returns the instance of persistence context. In case this method returns null, context is not initialized previously so {@link #init(String[])} method should be called.
     * 
     * @return
     */
    public static PersistenceContext getInstance() {
        return persistenceContext;
    }

    /**
     * Method triggers the scanning process in order to obtain {@link EntityMetaData} and {@link SQLStatements} for given class.
     * 
     * {@link EntityScanner} scans given class and returns appropriate {@link EntityMetaData} instance which is used by {@link SQLGenerator} to generate {@link SQLStatements}.
     * 
     * Generated {@link SQLStatements} are stored into the {@link SQLProvider} of this context and can be retrieved later using {@link SQLProvider#getSQL(Class)} method.
     * 
     * @param cls
     */
    protected void inspectClass(Class<?> cls) {
        EntityScanner.scanClass(cls, true);
    }

    /**
     * Method returns the {@link SQLProvider} used by this context. In case context is not initialized, exception is thrown.
     * 
     * @return {@link SQLProvider} used by this context
     */
    public SQLProvider getSQLProvider() {
        checkContext();
        return sqlProvider;
    }

    /**
     * Method returns the {@link EntityMetaDataProvider} used by this context. In case context is not initialized, exception is thrown.
     * 
     * @return {@link EntityMetaDataProvider} used by this context
     */
    public EntityMetaDataProvider getEntityMetaDataProvider() {
        checkContext();
        return entityMetaDataProvider;
    }

    /**
     * Method returns the {@link RelationshipMetaDataProvider} used by this context. In case context is not initialized, exception is thrown.
     * 
     * @return {@link RelationshipMetaDataProvider} used by this context
     */
    public RelationshipMetaDataProvider getRelationshipMetaDataProvider() {
        checkContext();
        return relationshipMetaDataProvider;
    }

    /**
     * Method returns {@link DatabaseHelper} used in this persistence context. In case context is not initialized, exception is thrown.
     * 
     * @return {@link DatabaseHelper} used in this persistence context
     */
    public DatabaseHelper getDatabaseHelper() {
        checkContext();
        return dbHelper;
    }

    public void setDatabaseHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Method returns {@link EntityWrapper} used in this persistence context. In case context is not initialized, exception is thrown.
     * 
     * @return {@link EntityWrapper} used in this persistence context
     */
    public EntityWrapper getEntityWrapper() {
        checkContext();
        return entityWrapper;
    }

    /**
     * Method returns {@link EntityHelper} used for given class. In case context is not initialized, exception is thrown.
     * 
     * @param cls
     * @return {@link EntityHelper} used for given class
     */
    @SuppressWarnings("rawtypes")
    public EntityHelper getEntityHelper(Class<?> cls) {
        checkContext();
        return entityWrapper.getEntityHelper(cls);
    }

    private void checkContext() {
        if (persistenceContext == null) {
            throw new RuntimeException("Persistence context is not initialized!");
        }
    }

}
