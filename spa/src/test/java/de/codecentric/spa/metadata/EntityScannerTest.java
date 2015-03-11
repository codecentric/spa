package de.codecentric.spa.metadata;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.codecentric.spa.metadata.EntityScanner.StringUtils;
import de.codecentric.spa.test.entities.DummySubEntity;

/**
 * JUnit test for {@link EntityScanner}.
 * 
 */
public class EntityScannerTest {

    private EntityMetaDataProvider entityMetaDataProvider;

    @Before
    public void setUp() {
        entityMetaDataProvider = EntityMetaDataProvider.getInstance();
        entityMetaDataProvider.clearMetaData();
    }

    /**
     * Test method for {@link EntityScanner#scanClass(Class)}.
     */
    @Test
    public void doScanTest() {
        EntityScanner.scanClass(Object.class, true);
        Assert.assertNull(entityMetaDataProvider.getMetaData(Object.class));

        Class<?> cls = DummySubEntity.class;
        EntityScanner.scanClass(cls, true);
        EntityMetaData entityMetaData = entityMetaDataProvider.getMetaData(cls);
        Assert.assertEquals("dummy_sub_entity", entityMetaData.getTableName());

        Assert.assertEquals("name", entityMetaData.getFieldNameForColumn("name"));
        Assert.assertEquals("someInt", entityMetaData.getFieldNameForColumn("some_int"));
        Assert.assertEquals("subFldDbl", entityMetaData.getFieldNameForColumn("sub_fld_dbl"));
        Assert.assertNull(entityMetaData.getFieldNameForColumn("not_persisted"));

        Assert.assertEquals("name", entityMetaData.getColumnNameForField("name"));
        Assert.assertEquals("some_int", entityMetaData.getColumnNameForField("someInt"));
        Assert.assertEquals("sub_fld_dbl", entityMetaData.getColumnNameForField("subFldDbl"));

        EntityScanner.scanClass(DummyNotPersistedEntity.class, true);
        Assert.assertNull(entityMetaDataProvider.getMetaData(DummyNotPersistedEntity.class));
    }

    /**
     * Test method for {@link StringUtils#uncamelize(String)}.
     */
    @Test
    public void stringUtilsScan() {
        Assert.assertEquals("some_field_name", StringUtils.uncamelize("someFieldName"));
        Assert.assertEquals("somefieldname", StringUtils.uncamelize("somefieldname"));
        Assert.assertEquals("some_field_name", StringUtils.uncamelize("SomeFieldName"));
        Assert.assertEquals("some_field_name_somefield_name", StringUtils.uncamelize("SomeFieldName_somefieldName"));
        Assert.assertNull(StringUtils.uncamelize(null));
    }

}

/**
 * Dummy not-persistent class used for testing purposes.
 */
class DummyNotPersistedEntity {
    public long id;
    public String name;
    public int someInt;

}
