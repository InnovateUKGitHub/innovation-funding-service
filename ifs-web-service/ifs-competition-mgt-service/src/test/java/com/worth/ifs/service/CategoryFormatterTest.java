package com.worth.ifs.service;

import static com.worth.ifs.category.builder.CategoryResourceBuilder.newCategoryResource;
import static com.worth.ifs.util.CollectionFunctions.asLinkedSet;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.worth.ifs.category.resource.CategoryResource;
public class CategoryFormatterTest {
	private CategoryFormatter formatter;
	
	private List<CategoryResource> allCategories;
	
	@Before
	public void setUpCategories() {
		formatter = new CategoryFormatter();
		allCategories = newCategoryResource()
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
