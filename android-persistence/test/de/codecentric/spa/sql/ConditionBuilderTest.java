package de.codecentric.spa.sql;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.codecentric.spa.metadata.EntityMetaData;
import de.codecentric.spa.metadata.EntityMetaDataProvider;
import de.codecentric.spa.metadata.EntityScanner;
import de.codecentric.spa.test.entities.DummySubEntity;

/**
 * JUnit test for {@link ConditionBuilder}.
 */
public class ConditionBuilderTest {

	private EntityMetaDataProvider entityMetaDataProvider;

	@Before
	public void setUp() {
		entityMetaDataProvider = EntityMetaDataProvider.getInstance();
		entityMetaDataProvider.clearMetaData();
	}

	/**
	 * Test method used to test {@link ConditionBuilder} to build many different
	 * sqlite query statement conditions.
	 */
	@Test
	public void testConditionBuilderAddCondition() {
		EntityScanner.scanClass(DummySubEntity.class, true);
		EntityMetaData entityMetaData = entityMetaDataProvider
				.getMetaData(DummySubEntity.class);

		ConditionBuilder condition = new ConditionBuilder(entityMetaData);
		condition.addCondition("someInt", "3", ConditionBuilder.Operator.LESS);
		Assert.assertEquals(" WHERE some_int<3", condition.build());

		condition = new ConditionBuilder(entityMetaData);
		condition.addCondition("someInt", "3",
				ConditionBuilder.Operator.LESS_EQ);
		Assert.assertEquals(" WHERE some_int<=3", condition.build());

		condition = new ConditionBuilder(entityMetaData);
		condition.addCondition("someInt", "3", ConditionBuilder.Operator.EQ);
		Assert.assertEquals(" WHERE some_int==3", condition.build());

		condition = new ConditionBuilder(entityMetaData);
		condition.addCondition("someInt", "3",
				ConditionBuilder.Operator.GREATER);
		Assert.assertEquals(" WHERE some_int>3", condition.build());

		condition = new ConditionBuilder(entityMetaData);
		condition.addCondition("someInt", "3",
				ConditionBuilder.Operator.GREATER_EQ);
		Assert.assertEquals(" WHERE some_int>=3", condition.build());

		condition = new ConditionBuilder(entityMetaData);
		condition
				.addCondition("someInt", "3", ConditionBuilder.Operator.NOT_EQ);
		Assert.assertEquals(" WHERE some_int<>3", condition.build());

		condition = new ConditionBuilder(entityMetaData);
		condition
				.openGroup()
				.addCondition("someInt", "1", ConditionBuilder.Operator.GREATER)
				.closeGroup().and().openGroup()
				.addCondition("someInt", "4", ConditionBuilder.Operator.LESS)
				.closeGroup();
		Assert.assertEquals(" WHERE (some_int>1) AND (some_int<4)",
				condition.build());

		condition = new ConditionBuilder(entityMetaData);
		condition
				.openGroup()
				.addCondition("someInt", "1", ConditionBuilder.Operator.GREATER)
				.closeGroup().or().openGroup()
				.addCondition("someInt", "4", ConditionBuilder.Operator.LESS)
				.closeGroup();
		Assert.assertEquals(" WHERE (some_int>1) OR (some_int<4)",
				condition.build());

	}

	/**
	 * Test if IllegalStateException is thrown on
	 * {@link ConditionBuilder#addCondition(String, String, rs.codecentric.android.persistence.ConditionBuilder.Operator)}
	 * when value==null.
	 */
	@Test
	public void testConditionBuilderAddConditionFail1() {
		EntityScanner.scanClass(DummySubEntity.class, true);
		EntityMetaData entityMetaData = entityMetaDataProvider
				.getMetaData(DummySubEntity.class);
		ConditionBuilder condition = new ConditionBuilder(entityMetaData);

		try {
			condition.addCondition(null, "3", ConditionBuilder.Operator.LESS);
		} catch (IllegalStateException ex) {
			Assert.assertTrue(ex instanceof IllegalStateException);
		}
	}

	/**
	 * Test if IllegalStateException is thrown on
	 * {@link ConditionBuilder#addCondition(String, String, rs.codecentric.android.persistence.ConditionBuilder.Operator)}
	 * when value==null.
	 */
	@Test
	public void testConditionBuilderAddConditionFail2() {
		EntityScanner.scanClass(DummySubEntity.class, true);
		EntityMetaData entityMetaData = entityMetaDataProvider
				.getMetaData(DummySubEntity.class);
		ConditionBuilder condition = new ConditionBuilder(entityMetaData);

		try {
			condition.addCondition("someInt", null,
					ConditionBuilder.Operator.LESS);
		} catch (IllegalStateException ex) {
			Assert.assertTrue(ex instanceof IllegalStateException);
		}
	}

	/**
	 * Test if IllegalStateException is thrown on
	 * {@link ConditionBuilder#closeGroup()} method call.
	 */
	@Test
	public void testConditionBuilderCloseGroupFail() {
		EntityScanner.scanClass(DummySubEntity.class, true);
		EntityMetaData entityMetaData = entityMetaDataProvider
				.getMetaData(DummySubEntity.class);
		ConditionBuilder condition = new ConditionBuilder(entityMetaData);

		try {
			condition.addCondition("someInt", "3",
					ConditionBuilder.Operator.LESS).closeGroup();
		} catch (IllegalStateException ex) {
			Assert.assertTrue(ex instanceof IllegalStateException);
		}
	}

	/**
	 * Test if IllegalStateException is thrown on
	 * {@link ConditionBuilder#build()} method call.
	 */
	@Test
	public void testConditionBuilderBuildFail() {
		EntityScanner.scanClass(DummySubEntity.class, true);
		EntityMetaData entityMetaData = entityMetaDataProvider
				.getMetaData(DummySubEntity.class);
		ConditionBuilder condition = new ConditionBuilder(entityMetaData);

		try {
			condition
					.addCondition("someInt", "3",
							ConditionBuilder.Operator.LESS).openGroup().build();
		} catch (IllegalStateException ex) {
			Assert.assertTrue(ex instanceof IllegalStateException);
		}
	}

}
