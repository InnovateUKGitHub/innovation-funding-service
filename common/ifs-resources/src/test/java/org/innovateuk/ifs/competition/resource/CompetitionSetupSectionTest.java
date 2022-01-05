package org.innovateuk.ifs.competition.resource;

import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.OPEN;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;

public class CompetitionSetupSectionTest {

	@Test
	public void fromPathNotRecognised() {
		String path = "something";

		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);

		assertNull(result);
	}

	@Test
	public void fromPathInitial() {
		String path = "initial";

		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);

		assertEquals(CompetitionSetupSection.INITIAL_DETAILS, result);
	}

	@Test
	public void fromPathAdditional() {
		String path = "additional";

		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);

		assertEquals(CompetitionSetupSection.ADDITIONAL_INFO, result);
	}

	@Test
	public void fromPathProjectEligibility() {
		String path = "project-eligibility";

		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);

		assertEquals(CompetitionSetupSection.PROJECT_ELIGIBILITY, result);
	}

	@Test
	public void fromPathOrganisationalEligibility() {
		String path = "organisational-eligibility";

		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);

		assertEquals(CompetitionSetupSection.ORGANISATIONAL_ELIGIBILITY, result);
	}

	@Test
	public void fromPathMilestones() {
		String path = "milestones";

		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);

		assertEquals(CompetitionSetupSection.MILESTONES, result);
	}

	@Test
	public void fromPathCompletionStage() {
		String path = "completion-stage";

		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);

		assertEquals(CompetitionSetupSection.COMPLETION_STAGE, result);
	}

	@Test
	public void fromPathApplicationSubmission() {
		String path = "application-submission";

		CompetitionSetupSection result = CompetitionSetupSection.fromPath(path);

		assertEquals(CompetitionSetupSection.APPLICATION_SUBMISSION, result);
	}

	@Test
	public void fromPathApplication() {
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
	public void allSectionsEditableWhenSetupIsCompleteAndNotYetInAssessment() {
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

		UserResource loggedInUser = newUserResource().withRoleGlobal(Role.SUPER_ADMIN_USER).build();

		CompetitionResource competitionResource = newCompetitionResource()
				.withSetupComplete(true)
				.withCompetitionStatus(OPEN)
				.withStartDate(tomorrow)
				.withFundersPanelDate(tomorrow)
				.build();

		assertFalse(homeSection.preventEdit(competitionResource, loggedInUser));
		assertFalse(projectEligibilitySection.preventEdit(competitionResource, loggedInUser));
		assertFalse(organisationalEligibilitySection.preventEdit(competitionResource, loggedInUser));
		assertFalse(milestonesSection.preventEdit(competitionResource, loggedInUser));
		assertFalse(applicationFormSection.preventEdit(competitionResource, loggedInUser));
		assertFalse(assessorSection.preventEdit(competitionResource, loggedInUser));
		assertFalse(initialDetailsSection.preventEdit(competitionResource, loggedInUser));
		assertFalse(additionalInfoSection.preventEdit(competitionResource, loggedInUser));
		assertFalse(projectDocumentSection.preventEdit(competitionResource, loggedInUser));
	}

	@Test
	public void sectionsEditableWhenCompetitionStartedAndSetupIsCompleteAndNotYetInAssessment() {
		CompetitionSetupSection initialDetailsSection = CompetitionSetupSection.INITIAL_DETAILS;
		CompetitionSetupSection additionalInfoSection = CompetitionSetupSection.ADDITIONAL_INFO;

		ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
		ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);

		UserResource loggedInUser = newUserResource().withRoleGlobal(Role.SUPER_ADMIN_USER).build();

		CompetitionResource competitionResource = newCompetitionResource()
				.withSetupComplete(true)
				.withStartDate(yesterday)
				.withFundersPanelDate(tomorrow)
				.build();

		assertFalse(initialDetailsSection.preventEdit(competitionResource, loggedInUser));
		assertFalse(additionalInfoSection.preventEdit(competitionResource, loggedInUser));
	}

	@Test
	public void sectionsNotEditableWhenCompetitionStartedAndSetupIsCompleteAndNotYetInAssessment() {
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

		UserResource loggedInUser = newUserResource().withRoleGlobal(Role.SUPER_ADMIN_USER).build();

		CompetitionResource competitionResource = newCompetitionResource()
				.withSetupComplete(true)
				.withStartDate(yesterday)
				.withFundersPanelDate(yesterday)
				.build();

		assertTrue(homeSection.preventEdit(competitionResource, loggedInUser));
		assertTrue(projectEligibilitySection.preventEdit(competitionResource, loggedInUser));
		assertTrue(organisationalEligibilitySection.preventEdit(competitionResource, loggedInUser));
		assertTrue(milestonesSection.preventEdit(competitionResource, loggedInUser));
		assertTrue(applicationFormSection.preventEdit(competitionResource, loggedInUser));
		assertTrue(assessorSection.preventEdit(competitionResource, loggedInUser));
		assertTrue(initialDetailsSection.preventEdit(competitionResource, loggedInUser));
		assertTrue(additionalInfoSection.preventEdit(competitionResource, loggedInUser));
		assertTrue(projectDocumentSection.preventEdit(competitionResource, loggedInUser));
	}

	@Test
	public void getAllNextSectionsForCompletionStage() {
		List<CompetitionSetupSection> nextSections = CompetitionSetupSection.COMPLETION_STAGE.getAllNextSections();

		assertEquals(1, nextSections.size());
		assertEquals(CompetitionSetupSection.MILESTONES, nextSections.get(0));
	}

	@Test
	public void getAllNextSectionsForApplicationSubmission() {
		List<CompetitionSetupSection> nextSections = CompetitionSetupSection.APPLICATION_SUBMISSION.getAllNextSections();

		assertEquals(1, nextSections.size());
		assertEquals(CompetitionSetupSection.MILESTONES, nextSections.get(0));
	}
}
