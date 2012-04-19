package de.codecentric.spa.metadata;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.codecentric.spa.metadata.RelationshipMetaData.RelationshipType;
import de.codecentric.spa.test.entities.DummyAttribute;
import de.codecentric.spa.test.entities.DummySubEntity;

public class RelationshipMetaDataProviderTest {

	private RelationshipMetaDataProvider provider;

	@Before
	public void setUp() {
		provider = RelationshipMetaDataProvider.getInstance();
		provider.clearMetaData();
	}

	@Test
	public void test() {
		Class<?> cls = DummySubEntity.class;

		List<RelationshipMetaData> metaDataList = provider.getMetaData(cls);
		Assert.assertNull(metaDataList);

		RelationshipMetaData metaData = new RelationshipMetaData();
		metaData.setChildClass(DummyAttribute.class);
		metaData.setFieldName("attributes");
		metaData.setForeignKeyColumnName("attributes_fk");
		metaData.setParentClass(DummySubEntity.class);
		metaData.setRelationshipType(RelationshipType.ONE_TO_MANY);

		provider.addMetaData(cls, metaData);
		Assert.assertNotNull(provider.getMetaData(cls));
		Assert.assertNotNull(provider.getMetaDataByChild(DummyAttribute.class));

	}

}
