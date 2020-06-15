package org.innovateuk.ifs.competition.resource;

import org.junit.Test;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.OPEN;
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
	public void testFromPathProjectEligibility() {
		String path = "project-eligibility";
		
		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);
		
		assertEquals(CompetitionSetupSection.PROJECT_ELIGIBILITY, result);
	}

	@Test
	public void testFromPathOrganisationalEligibility() {
		String path = "organisational-eligibility";

		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);

		assertEquals(CompetitionSetupSection.ORGANISATIONAL_ELIGIBILITY, result);
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
	public void fromPathProjectDocument() {
		String path = "project-document";

		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);

		assertEquals(CompetitionSetupSection.PROJECT_DOCUMENT, result);
	}

	@Test
	public void testAllSectionsEditableWhenSetupIsCompleteAndNotYetInAssessment() {
		CompetitionSetupSection homeSection = CompetitionSetupSection.HOME;
		CompetitionSetupSection projectEligibilitySection = CompetitionSetupSection.PROJECT_ELIGIBILITY;
		CompetitionSetupSection organisationalEligibilitySection = CompetitionSetupSection.ORGANISATIONAL_ELIGIBILITY;
		CompetitionSetupSection milestonesSection = CompetitionSetupSection.MILESTONES;
		CompetitionSetupSection applicationFormSection = CompetitionSetupSection.APPLICATION_FORM;
		CompetitionSetupSection assessorSection = CompetitionSetupSection.ASSESSORS;
		CompetitionSetupSection initialDetailsSection = CompetitionSetupSection.INITIAL_DETAILS;
		CompetitionSetupSection additionalInfoSection = CompetitionSetupSection.ADDITIONAL_INFO;
		CompetitionSetupSection projectDocumentSection = CompetitionSetupSection.PROJECT_DOCUMENT;

		ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);

		CompetitionResource competitionResource = newCompetitionResource()
				.withSetupComplete(true)
				.withCompetitionStatus(OPEN)
				.withStartDate(tomorrow)
				.withFundersPanelDate(tomorrow)
				.build();

		assertFalse(homeSection.preventEdit(competitionResource));
		assertFalse(projectEligibilitySection.preventEdit(competitionResource));
		assertFalse(organisationalEligibilitySection.preventEdit(competitionResource));
		assertFalse(milestonesSection.preventEdit(competitionResource));
		assertFalse(applicationFormSection.preventEdit(competitionResource));
		assertFalse(assessorSection.preventEdit(competitionResource));
		assertFalse(initialDetailsSection.preventEdit(competitionResource));
		assertFalse(additionalInfoSection.preventEdit(competitionResource));
		assertFalse(projectDocumentSection.preventEdit(competitionResource));
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
		CompetitionSetupSection projectEligibilitySection = CompetitionSetupSection.PROJECT_ELIGIBILITY;
		CompetitionSetupSection organisationalEligibilitySection = CompetitionSetupSection.ORGANISATIONAL_ELIGIBILITY;
		CompetitionSetupSection milestonesSection = CompetitionSetupSection.MILESTONES;
		CompetitionSetupSection applicationFormSection = CompetitionSetupSection.APPLICATION_FORM;
		CompetitionSetupSection assessorSection = CompetitionSetupSection.ASSESSORS;
		CompetitionSetupSection initialDetailsSection = CompetitionSetupSection.INITIAL_DETAILS;
		CompetitionSetupSection additionalInfoSection = CompetitionSetupSection.ADDITIONAL_INFO;
		CompetitionSetupSection projectDocumentSection = CompetitionSetupSection.PROJECT_DOCUMENT;

		ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);

		CompetitionResource competitionResource = newCompetitionResource()
				.withSetupComplete(true)
				.withStartDate(yesterday)
				.withFundersPanelDate(yesterday)
				.build();

		assertTrue(homeSection.preventEdit(competitionResource));
		assertTrue(projectEligibilitySection.preventEdit(competitionResource));
		assertTrue(organisationalEligibilitySection.preventEdit(competitionResource));
		assertTrue(milestonesSection.preventEdit(competitionResource));
		assertTrue(applicationFormSection.preventEdit(competitionResource));
		assertTrue(assessorSection.preventEdit(competitionResource));
		assertTrue(initialDetailsSection.preventEdit(competitionResource));
		assertTrue(additionalInfoSection.preventEdit(competitionResource));
		assertTrue(projectDocumentSection.preventEdit(competitionResource));
	}
}
