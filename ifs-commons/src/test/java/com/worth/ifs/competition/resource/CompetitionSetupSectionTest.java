package com.worth.ifs.competition.resource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CompetitionSetupSectionTest {

	@Test
	public void testFromPathNotRecognised() {
		String path = "something";
		
		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);
		
		assertNull(result);
	}
	
	@Test
	public void testFromPathInitial() {
		String path = "initial";
		
		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);
		
		assertEquals(CompetitionSetupSection.INITIAL_DETAILS, result);
	}
	
	@Test
	public void testFromPathAdditional() {
		String path = "additional";
		
		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);
		
		assertEquals(CompetitionSetupSection.ADDITIONAL_INFO, result);
	}
	
	@Test
	public void testFromPathEligibility() {
		String path = "eligibility";
		
		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);
		
		assertEquals(CompetitionSetupSection.ELIGIBILITY, result);
	}
	
	@Test
	public void testFromPathMilestones() {
		String path = "milestones";
		
		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);
		
		assertEquals(CompetitionSetupSection.MILESTONES, result);
	}
	
	@Test
	public void testFromPathApplication() {
		String path = "application";
		
		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);
		
		assertEquals(CompetitionSetupSection.APPLICATION_FORM, result);
	}
}
