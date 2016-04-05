package com.worth.ifs.application.resource.comparators;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class DualFieldComparatorTest {

	private MyObjComparator comparator;
	
	@Before
	public void setUp() {
		comparator = new MyObjComparator();
	}
	
	@Test
	public void compareFirstNullName() {
		MyObject o1 = obj(null, 1);
		MyObject o2 = obj("a", 1);
		
		List<MyObject> result = sort(o1, o2);
		
		assertEquals(o1, result.get(0));
		assertEquals(o2, result.get(1));
	}

	@Test
	public void compareSecondNullName() {
		MyObject o1 = obj("a", 1);
		MyObject o2 = obj(null, 1);
		
		List<MyObject> result = sort(o1, o2);
		
		assertEquals(o2, result.get(0));
		assertEquals(o1, result.get(1));
	}
	
	@Test
	public void compareBothNullNameUsesIdPositive() {
		MyObject o1 = obj(null, 2);
		MyObject o2 = obj(null, 1);
		
		List<MyObject> result = sort(o1, o2);
		
		assertEquals(o2, result.get(0));
		assertEquals(o1, result.get(1));
	}
	
	@Test
	public void compareBothNullNameUsesIdNegative() {
		MyObject o1 = obj(null, 1);
		MyObject o2 = obj(null, 2);
		
		List<MyObject> result = sort(o1, o2);
		
		assertEquals(o1, result.get(0));
		assertEquals(o2, result.get(1));
	}
	
	@Test
	public void compareNameEqualIdBothNull() {
		MyObject o1 = obj(null, null);
		MyObject o2 = obj(null, null);
		
		List<MyObject> result = sort(o1, o2);
		
		assertEquals(o1, result.get(0));
		assertEquals(o2, result.get(1));
	}
	
	@Test
	public void compareNameEqualOneIdNullPositive() {
		MyObject o1 = obj(null, null);
		MyObject o2 = obj(null, 1);
		
		List<MyObject> result = sort(o1, o2);
		
		assertEquals(o1, result.get(0));
		assertEquals(o2, result.get(1));
	}
	
	@Test
	public void compareNameEqualOneIdNullNegative() {
		MyObject o1 = obj(null, 1);
		MyObject o2 = obj(null, null);
		
		List<MyObject> result = sort(o1, o2);
		
		assertEquals(o1, result.get(1));
		assertEquals(o2, result.get(0));
	}
	
	private List<MyObject> sort(MyObject... args) {
		List<MyObject> list = Arrays.asList(args);
		Collections.sort(list, comparator);
		return list;
	}
	
	private MyObject obj(String name, Integer id) {
		MyObject result = new MyObject();
		result.setId(id);
		result.setName(name);
		return result;
	}
	
	private static class MyObject {
		private Integer id;
		private String name;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	private static class MyObjComparator extends DualFieldComparator<String, Integer> implements Comparator<MyObject> {

		@Override
		public int compare(MyObject o1, MyObject o2) {
			String o1Lead = o1.getName();
			String o2Lead = o2.getName();
			
			Integer o1Id = o1.getId();
			Integer o2Id = o2.getId();
			
			return compare(o1Lead, o2Lead, o1Id, o2Id);
		}
		
	}
}
