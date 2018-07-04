package org.innovateuk.ifs.service;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.service.CategoryFormatter;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.junit.Assert.assertEquals;

public class CategoryFormatterTest {
	private CategoryFormatter formatter;
	
	private List<InnovationAreaResource> allCategories;
	
	@Before
	public void setUpCategories() {
		formatter = new CategoryFormatter();
		allCategories = newInnovationAreaResource()
				.withId(1L, 2L, 3L)
				.withName("first", "second", "third")
				.build(3);
	}
	
	@Test
	public void testWithNullCategories() {
		String result = formatter.format(null, allCategories);
		
		assertEquals("", result);
	}
	
	@Test
	public void testWithNoCategories() {
		String result = formatter.format(asLinkedSet(), allCategories);
		
		assertEquals("", result);
	}
	
	@Test
	public void testWithSingleCategory() {
		String result = formatter.format(asLinkedSet(2L), allCategories);
		
		assertEquals("second", result);
	}
	
	@Test
	public void testWithMultipleCategories() {
		String result = formatter.format(asLinkedSet(1L, 2L, 3L), allCategories);
		
		assertEquals("first, second, third", result);
	}
}
