package de.codecentric.spa.metadata;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.codecentric.spa.metadata.EntityMetaData;
import de.codecentric.spa.metadata.FieldMetaData;
import de.codecentric.spa.sql.SQLiteTypeMapper;
import de.codecentric.spa.test.entities.DummySubEntity;

public class EntityMetaDataTest {

	private EntityMetaData metaData;

	@Before
	public void setUp() {
		metaData = new EntityMetaData(DummySubEntity.class);
	}

	@Test
	public void testMetaData() {
		Assert.assertEquals(DummySubEntity.class, metaData.getDescribingClass());
		Assert.assertEquals(null, metaData.getFieldNameForColumn("sub_fld_dbl"));
		Assert.assertNull(metaData.getIdentifier());
		Assert.assertNull(metaData.getPersistentField("subFldDbl"));
		Assert.assertEquals(0, metaData.getPersistentFields().size());
		Assert.assertEquals("dummy_sub_entity", metaData.getTableName());
	}

	@Test
	public void testMetaData2() {
		FieldMetaData fmd = new FieldMetaData();
		fmd.setColumnName("sub_fld_dbl");
		fmd.setColumnType(SQLiteTypeMapper.TYPE_REAL);
		fmd.setFieldName("subFldDbl");
		metaData.addPersistentField(fmd);

		FieldMetaData id = new FieldMetaData();
		id.setColumnName("id");
		id.setColumnType(SQLiteTypeMapper.TYPE_INT);
		id.setFieldName("id");
		metaData.setIdentifier(id);

		Assert.assertEquals("subFldDbl", metaData.getFieldNameForColumn("sub_fld_dbl"));
		Assert.assertNotNull(metaData.getPersistentField("subFldDbl"));
		Assert.assertEquals(1, metaData.getPersistentFields().size());

		Assert.assertNotNull(metaData.getIdentifier());

		Assert.assertNull(metaData.getPersistentField(null));
	}

}
