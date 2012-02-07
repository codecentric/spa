package de.codecentric.spa.metadata;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.codecentric.spa.annotations.CascadeType;
import de.codecentric.spa.annotations.FetchType;
import de.codecentric.spa.metadata.RelationshipInfo;
import de.codecentric.spa.metadata.RelationshipMetaData;
import de.codecentric.spa.metadata.RelationshipMetaDataProvider;
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
		RelationshipInfo relationshipInfo = new RelationshipInfo();
		CascadeType[] cascade = new CascadeType[1];
		cascade[0] = CascadeType.REMOVE;
		relationshipInfo.setFetch(FetchType.LAZY);
		relationshipInfo.setCascade(cascade);
		metaData.setRelationshipInfo(relationshipInfo);

		provider.addMetaData(cls, metaData);
		Assert.assertNotNull(provider.getMetaData(cls));
		Assert.assertNotNull(provider.getMetaData(cls).get(0)
				.getRelationshipInfo().getCascade());
		Assert.assertEquals(FetchType.LAZY, provider.getMetaData(cls).get(0)
				.getRelationshipInfo().getFetch());
		Assert.assertNotNull(provider.getMetaDataByChild(DummyAttribute.class));

	}

}
