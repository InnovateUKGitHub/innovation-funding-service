package com.worth.ifs.service;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.worth.ifs.service.ApplicationSummarySortFieldService;

public class ApplicationSummarySortFieldServiceTest {
	private ApplicationSummarySortFieldService service;
	
	@Before
	public void setUp() {
		service = new ApplicationSummarySortFieldService();
	}
	
	@Test
	public void testOpenCompetitionAllowedFields() {
		Arrays.asList("percentageComplete", "id", "lead", "name", "leadApplicant").stream().forEach(field -> {
			String result = service.sortFieldForOpenCompetition(field);
			assertEquals(field, result);
		});
	}
	
	@Test
	public void testOpenCompetitionDefaultField() {
		String result = service.sortFieldForOpenCompetition("potato");
		assertEquals("percentageComplete", result);
	}
	
	@Test
	public void testNotSubmittedAllowedFields() {
		Arrays.asList("percentageComplete", "id", "lead", "name").stream().forEach(field -> {
			String result = service.sortFieldForNotSubmittedApplications(field);
			assertEquals(field, result);
		});
	}
	
	@Test
	public void testNotSubmittedDefaultField() {
		String result = service.sortFieldForNotSubmittedApplications("potato");
		assertEquals("percentageComplete", result);
	}
	
	@Test
	public void testSubmittedAllowedFields() {
		Arrays.asList("id", "lead", "name", "numberOfPartners", "grantRequested", "totalProjectCost", "duration").stream().forEach(field -> {
			String result = service.sortFieldForSubmittedApplications(field);
			assertEquals(field, result);
		});
	}
	
	@Test
	public void testSubmittedDefaultField() {
		String result = service.sortFieldForSubmittedApplications("potato");
		assertEquals("id", result);
	}
}
