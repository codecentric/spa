package de.codecentric.spa.metadata;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.codecentric.spa.metadata.EntityMetaData;
import de.codecentric.spa.metadata.EntityMetaDataProvider;
import de.codecentric.spa.test.entities.DummySubEntity;

public class EntityMetaDataProviderTest {

	private EntityMetaDataProvider provider;

	@Before
	public void setUp() {
		provider = EntityMetaDataProvider.getInstance();
	}

	@Test
	public void test() {
		Class<?>[] clsArr = provider.getPersistentClasses();
		Assert.assertNull(clsArr);

		Class<?> cls = DummySubEntity.class;

		Assert.assertNull(provider.getMetaData(cls));

		provider.addMetaData(cls, new EntityMetaData(cls));
		Assert.assertNotNull(provider.getMetaData(cls));

		clsArr = provider.getPersistentClasses();
		Assert.assertEquals(1, clsArr.length);
	}

}
