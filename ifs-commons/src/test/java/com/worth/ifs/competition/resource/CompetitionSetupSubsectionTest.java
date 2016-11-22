package com.worth.ifs.competition.resource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CompetitionSetupSubsectionTest {

	@Test
	public void testFromPathNotRecognised() {
		String path = "something";

		CompetitionSetupSubsection result = CompetitionSetupSubsection.fromPath(path);
		
		assertNull(result);
	}
	
	@Test
	public void testFromPathProject() {
		String path = "project";

		CompetitionSetupSubsection result = CompetitionSetupSubsection.fromPath(path);
		
		assertEquals(CompetitionSetupSubsection.PROJECT_DETAILS, result);
	}
	
	@Test
	public void testFromPathQuestion() {
		String path = "question";

		CompetitionSetupSubsection result = CompetitionSetupSubsection.fromPath(path);

		assertEquals(CompetitionSetupSubsection.QUESTIONS, result);
	}
	
	@Test
	public void testFromPathFinance() {
		String path = "finance";

		CompetitionSetupSubsection result = CompetitionSetupSubsection.fromPath(path);

		assertEquals(CompetitionSetupSubsection.FINANCES, result);
	}
}
