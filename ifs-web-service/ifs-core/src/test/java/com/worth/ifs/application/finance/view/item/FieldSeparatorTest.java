package com.worth.ifs.application.finance.view.item;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.application.finance.view.FieldSeparator;
public class FieldSeparatorTest {

	private FieldSeparator separator;
	
	@Before
	public void setUp() {
		separator = new FieldSeparator();
	}
	
	@Test
	public void testSeparateEmpty() {
		List<FinanceFormField> fields = asList();
		
		List<List<FinanceFormField>> result = separator.separateFields(fields);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void testSeparateSingle() {
		List<FinanceFormField> fields = asList(f("a", "1"), f("b", "2"), f("c", "3"));
		
		List<List<FinanceFormField>> result = separator.separateFields(fields);
		
		assertEquals(1, result.size());
		assertEquals(3, result.get(0).size());
		assertEquals("a", result.get(0).get(0).getFieldName());
		assertEquals("1", result.get(0).get(0).getValue());
		assertEquals("b", result.get(0).get(1).getFieldName());
		assertEquals("2", result.get(0).get(1).getValue());
		assertEquals("c", result.get(0).get(2).getFieldName());
		assertEquals("3", result.get(0).get(2).getValue());
	}
	
	@Test
	public void testSeparateMultiple() {
		List<FinanceFormField> fields = asList(f("a", "1"), f("b","2"), f("a", "3"), f("b", "4"));
		
		List<List<FinanceFormField>> result = separator.separateFields(fields);
		
		assertEquals(2, result.size());
		assertEquals(2, result.get(0).size());
		assertEquals(2, result.get(1).size());
		assertEquals("a", result.get(0).get(0).getFieldName());
		assertEquals("1", result.get(0).get(0).getValue());
		assertEquals("b", result.get(0).get(1).getFieldName());
		assertEquals("2", result.get(0).get(1).getValue());
		assertEquals("a", result.get(1).get(0).getFieldName());
		assertEquals("3", result.get(1).get(0).getValue());
		assertEquals("b", result.get(1).get(1).getFieldName());
		assertEquals("4", result.get(1).get(1).getValue());
	}
	
	@Test
	public void testSeparateMultipleSomethingAbsent() {
		List<FinanceFormField> fields = asList(f("a", "1"), f("b","2"), f("b", "4"));
		
		List<List<FinanceFormField>> result = separator.separateFields(fields);
		
		assertEquals(2, result.size());
		assertEquals(2, result.get(0).size());
		assertEquals(1, result.get(1).size());
		assertEquals("a", result.get(0).get(0).getFieldName());
		assertEquals("1", result.get(0).get(0).getValue());
		assertEquals("b", result.get(0).get(1).getFieldName());
		assertEquals("2", result.get(0).get(1).getValue());
		assertEquals("b", result.get(1).get(0).getFieldName());
		assertEquals("4", result.get(1).get(0).getValue());
	}
	
	private FinanceFormField f(String fieldName, String value) {
		return new FinanceFormField(fieldName, value, null, null, null, null);
	}
}
