package de.codecentric.spa.metadata;

import java.util.List;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

/**
 * JUnit test for {@link PackageScanner}.
 */
public class PackageScannerTest {

/**
	 * Method tests situation when not existing package name is used to call {@link PackageScanner#scanPackage(String).
	 * 
	 * @throws Exception
	 */
    @Test
    public void scanPackageFailTest() throws Exception {
        Assert.assertEquals(0, PackageScanner.scanPackage("some.package.name").size());
    }

/**
	 * 
	 * Method tests situation when existing package name is used to call {@link PackageScanner#scanPackage(String)(legal situation).
	 * 
	 * @throws Exception
	 */
    @Test
    public void scanPackageTest() throws Exception {
        List<EntityMetaData> entityMetaDataList = PackageScanner.scanPackage("de.codecentric.spa.test.entities");
        Assert.assertNotNull(entityMetaDataList);
        Assert.assertEquals(4, entityMetaDataList.size());

        Assert.assertEquals("dummy_attribute", entityMetaDataList.get(0).getTableName());

        Assert.assertEquals("dummyName", entityMetaDataList.get(0).getFieldNameForColumn("dummy_name"));
        Assert.assertEquals("dummyValue", entityMetaDataList.get(0).getFieldNameForColumn("dummy_value"));

        Assert.assertEquals("dummy_sub_entity", entityMetaDataList.get(3).getTableName());

        Assert.assertEquals("name", entityMetaDataList.get(3).getFieldNameForColumn("name"));
        Assert.assertEquals("someInt", entityMetaDataList.get(3).getFieldNameForColumn("some_int"));
        Assert.assertEquals("subFldDbl", entityMetaDataList.get(3).getFieldNameForColumn("sub_fld_dbl"));

    }

    @Test
    @Ignore
    public void scanJarTest() throws Exception {
        String path = new java.io.File(".").getCanonicalPath();
        List<EntityMetaData> entityMetaDataList = PackageScanner.scanJar(path + "\\test-lib\\test-entities.jar", "de.codecentric.spa.jar.test.entities");
        Assert.assertNotNull(entityMetaDataList);
        Assert.assertEquals(4, entityMetaDataList.size());
    }

    /**
     * Method used to test {@link PackageScanner#getJarClassPathEntries()} method call.
     */
    @Test
    public void getJarClassPathEntriesTest() {
        List<String> classPathEntries = PackageScanner.getJarClassPathEntries();
        Assert.assertNotNull(classPathEntries);
    }

}
