package org.innovateuk.ifs.competition.resource;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;

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

	@Test
	public void testAllSectionsEditableWhenSetupIsCompleteAndNotYetInAssessment() {
		CompetitionSetupSection homeSection = CompetitionSetupSection.HOME;
		CompetitionSetupSection eligibilitySection = CompetitionSetupSection.ELIGIBILITY;
		CompetitionSetupSection milestonesSection = CompetitionSetupSection.MILESTONES;
		CompetitionSetupSection applicationFormSection = CompetitionSetupSection.APPLICATION_FORM;
		CompetitionSetupSection assessorSection = CompetitionSetupSection.ASSESSORS;
		CompetitionSetupSection initialDetailsSection = CompetitionSetupSection.INITIAL_DETAILS;
		CompetitionSetupSection additionalInfoSection = CompetitionSetupSection.ADDITIONAL_INFO;

		ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);

		CompetitionResource competitionResource = newCompetitionResource()
				.withSetupComplete(true)
				.withStartDate(tomorrow)
				.withFundersPanelDate(tomorrow)
				.build();

		assertFalse(homeSection.preventEdit(competitionResource));
		assertFalse(eligibilitySection.preventEdit(competitionResource));
		assertFalse(milestonesSection.preventEdit(competitionResource));
		assertFalse(applicationFormSection.preventEdit(competitionResource));
		assertFalse(assessorSection.preventEdit(competitionResource));
		assertFalse(initialDetailsSection.preventEdit(competitionResource));
		assertFalse(additionalInfoSection.preventEdit(competitionResource));
	}

	@Test
	public void testSectionsEditableWhenCompetitionStartedAndSetupIsCompleteAndNotYetInAssessment() {
		CompetitionSetupSection initialDetailsSection = CompetitionSetupSection.INITIAL_DETAILS;
		CompetitionSetupSection additionalInfoSection = CompetitionSetupSection.ADDITIONAL_INFO;

		ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
		ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);

		CompetitionResource competitionResource = newCompetitionResource()
				.withSetupComplete(true)
				.withStartDate(yesterday)
				.withFundersPanelDate(tomorrow)
				.build();

		assertFalse(initialDetailsSection.preventEdit(competitionResource));
		assertFalse(additionalInfoSection.preventEdit(competitionResource));
	}

	@Test
	public void testSectionsNotEditableWhenCompetitionStartedAndSetupIsCompleteAndNotYetInAssessment() {
		CompetitionSetupSection homeSection = CompetitionSetupSection.HOME;
		CompetitionSetupSection eligibilitySection = CompetitionSetupSection.ELIGIBILITY;
		CompetitionSetupSection milestonesSection = CompetitionSetupSection.MILESTONES;
		CompetitionSetupSection applicationFormSection = CompetitionSetupSection.APPLICATION_FORM;
		CompetitionSetupSection assessorSection = CompetitionSetupSection.ASSESSORS;
		CompetitionSetupSection initialDetailsSection = CompetitionSetupSection.INITIAL_DETAILS;
		CompetitionSetupSection additionalInfoSection = CompetitionSetupSection.ADDITIONAL_INFO;

		ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);

		CompetitionResource competitionResource = newCompetitionResource()
				.withSetupComplete(true)
				.withStartDate(yesterday)
				.withFundersPanelDate(yesterday)
				.build();

		assertTrue(homeSection.preventEdit(competitionResource));
		assertTrue(eligibilitySection.preventEdit(competitionResource));
		assertTrue(milestonesSection.preventEdit(competitionResource));
		assertTrue(applicationFormSection.preventEdit(competitionResource));
		assertTrue(assessorSection.preventEdit(competitionResource));
		assertTrue(initialDetailsSection.preventEdit(competitionResource));
		assertTrue(additionalInfoSection.preventEdit(competitionResource));
	}

	@Test
	public void testIsComplete() {
		CompetitionSetupSection homeSection = CompetitionSetupSection.HOME;
		CompetitionSetupSection eligibilitySection = CompetitionSetupSection.ELIGIBILITY;
		CompetitionSetupSection milestonesSection = CompetitionSetupSection.MILESTONES;
		CompetitionSetupSection applicationFormSection = CompetitionSetupSection.APPLICATION_FORM;
		CompetitionSetupSection assessorSection = CompetitionSetupSection.ASSESSORS;
		CompetitionSetupSection initialDetailsSection = CompetitionSetupSection.INITIAL_DETAILS;
		CompetitionSetupSection additionalInfoSection = CompetitionSetupSection.ADDITIONAL_INFO;


		CompetitionResource competitionResource = newCompetitionResource()
				.withSectionSetupStatus(ImmutableMap.<CompetitionSetupSection, Boolean> builder()
						.put(homeSection, true)
						.put(eligibilitySection,true)
						.put(milestonesSection,false)
						.put(applicationFormSection,false)
						.put(assessorSection,false)
						.put(initialDetailsSection,false)
						.put(additionalInfoSection,false).build())
				.build();

		assertEquals(homeSection.isComplete(competitionResource), true);
		assertEquals(eligibilitySection.isComplete(competitionResource), true);
		assertEquals(milestonesSection.isComplete(competitionResource), false);
		assertEquals(applicationFormSection.isComplete(competitionResource), false);
		assertEquals(assessorSection.isComplete(competitionResource), false);
		assertEquals(initialDetailsSection.isComplete(competitionResource), false);
		assertEquals(additionalInfoSection.isComplete(competitionResource), false);


	}
}
