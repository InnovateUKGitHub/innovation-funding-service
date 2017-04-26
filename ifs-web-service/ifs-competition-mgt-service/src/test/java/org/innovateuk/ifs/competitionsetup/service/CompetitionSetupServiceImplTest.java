package org.innovateuk.ifs.competitionsetup.service;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competitionsetup.form.AdditionalInfoForm;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.service.formpopulator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.competitionsetup.service.modelpopulator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.service.sectionupdaters.CompetitionSetupSectionSaver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupServiceImplTest {

	@InjectMocks
	private CompetitionSetupServiceImpl service;
	
	@Mock
	private CompetitionService competitionService;

	@Test
	public void testPopulateCompetitionSectionModelAttributesNoMatchingFormPopulator() {
		Model model = new ExtendedModelMap();
		CompetitionResource competition = newCompetitionResource()
				.withId(1L)
				.withCompetitionCode("code")
				.withName("name")
				.withSetupComplete(false)
				.build();
		
		service.setCompetitionSetupSectionModelPopulators(asList());
		
		CompetitionSetupSection section = CompetitionSetupSection.INITIAL_DETAILS;
		
		List<CompetitionSetupSection> completedSections = new ArrayList<>();
		service.populateCompetitionSectionModelAttributes(model, competition, section);
		
		verifyCommonModelAttributes(model, competition, section, completedSections);
		assertEquals("section-initial", model.asMap().get("currentSectionFragment"));
	}
	
	@Test
	public void testPopulateCompetitionSectionModelAttributesEligibility() {
		Model model = new ExtendedModelMap();
		CompetitionResource competition = newCompetitionResource()
				.withCompetitionCode("code")
				.withSetupComplete(false)
				.withName("name")
				.build();
		
		CompetitionSetupSectionModelPopulator matchingPopulator = mock(CompetitionSetupSectionModelPopulator.class);
		when(matchingPopulator.sectionToPopulateModel()).thenReturn(CompetitionSetupSection.ELIGIBILITY);
		CompetitionSetupSectionModelPopulator notMatchingPopulator = mock(CompetitionSetupSectionModelPopulator.class);
		when(notMatchingPopulator.sectionToPopulateModel()).thenReturn(CompetitionSetupSection.MILESTONES);
		
		service.setCompetitionSetupSectionModelPopulators(asList(matchingPopulator, notMatchingPopulator));

		CompetitionSetupSection section = CompetitionSetupSection.ELIGIBILITY;
		
		List<CompetitionSetupSection> completedSections = new ArrayList<>();

		service.populateCompetitionSectionModelAttributes(model, competition, section);
		
		verifyCommonModelAttributes(model, competition, section, completedSections);
		assertEquals("section-eligibility", model.asMap().get("currentSectionFragment"));
		
		verify(matchingPopulator).populateModel(model, competition);
		verify(notMatchingPopulator, never()).populateModel(model, competition);;
	}

	private void verifyCommonModelAttributes(Model model, CompetitionResource competition,
			CompetitionSetupSection section, List<CompetitionSetupSection> completedSections) {
		assertEquals(9, model.asMap().size());
		assertEquals(Boolean.FALSE, model.asMap().get("isInitialComplete"));
		assertEquals(Boolean.TRUE, model.asMap().get("editable"));
		assertEquals(competition, model.asMap().get("competition"));
		assertEquals(section, model.asMap().get("currentSection"));
		assertArrayEquals(CompetitionSetupSection.values(), (Object[])model.asMap().get("allSections"));
		assertEquals(Boolean.FALSE, model.asMap().get("preventEdit"));
		assertEquals(Boolean.FALSE, model.asMap().get("isSetupAndLive"));
		assertEquals(Boolean.FALSE, model.asMap().get("setupComplete"));
	}
	
	@Test
	public void testGetSectionFormData() {
		CompetitionResource competitionResource = newCompetitionResource().build();
		
		CompetitionSetupFormPopulator matchingPopulator = mock(CompetitionSetupFormPopulator.class);
		when(matchingPopulator.sectionToFill()).thenReturn(CompetitionSetupSection.ADDITIONAL_INFO);
		CompetitionSetupForm matchingForm = mock(CompetitionSetupForm.class);
		when(matchingPopulator.populateForm(competitionResource)).thenReturn(matchingForm);
		
		CompetitionSetupFormPopulator otherPopulator = mock(CompetitionSetupFormPopulator.class);
		when(otherPopulator.sectionToFill()).thenReturn(CompetitionSetupSection.APPLICATION_FORM);
		CompetitionSetupForm otherForm = mock(CompetitionSetupForm.class);
		when(otherPopulator.populateForm(competitionResource)).thenReturn(otherForm);

		service.setCompetitionSetupFormPopulators(asList(matchingPopulator, otherPopulator));
		
		CompetitionSetupForm result = service.getSectionFormData(competitionResource, CompetitionSetupSection.ADDITIONAL_INFO);
		
		assertEquals(matchingForm, result);
		verify(matchingPopulator).populateForm(competitionResource);
		verify(otherPopulator, never()).populateForm(competitionResource);
	}
	
	@Test
	public void testSaveSection() {
		CompetitionSetupForm competitionSetupForm = new AdditionalInfoForm();
		CompetitionResource competitionResource = newCompetitionResource().build();
		
		CompetitionSetupSectionSaver matchingSaver = mock(CompetitionSetupSectionSaver.class);
		when(matchingSaver.sectionToSave()).thenReturn(CompetitionSetupSection.ADDITIONAL_INFO);
		when(matchingSaver.supportsForm(AdditionalInfoForm.class)).thenReturn(true);
		
		CompetitionSetupSectionSaver otherSaver = mock(CompetitionSetupSectionSaver.class);
		when(otherSaver.sectionToSave()).thenReturn(CompetitionSetupSection.APPLICATION_FORM);
		when(otherSaver.supportsForm(AdditionalInfoForm.class)).thenReturn(false);

		when(matchingSaver.saveSection(competitionResource, competitionSetupForm)).thenReturn(serviceSuccess());

		service.setCompetitionSetupSectionSavers(asList(matchingSaver, otherSaver));
		
		service.saveCompetitionSetupSection(competitionSetupForm, competitionResource, CompetitionSetupSection.ADDITIONAL_INFO);
		
		verify(matchingSaver).saveSection(competitionResource, competitionSetupForm);
		verify(otherSaver, never()).saveSection(competitionResource, competitionSetupForm);
	}

	@Test
	public void testIsCompetitionReadyToOpen() {
		Map<CompetitionSetupSection, Boolean> testSectionStatus = new HashMap<>();
		testSectionStatus.put(CompetitionSetupSection.INITIAL_DETAILS, Boolean.TRUE);
		testSectionStatus.put(CompetitionSetupSection.ADDITIONAL_INFO, Boolean.TRUE);
		testSectionStatus.put(CompetitionSetupSection.ELIGIBILITY, Boolean.TRUE);
		testSectionStatus.put(CompetitionSetupSection.MILESTONES, Boolean.TRUE);
		testSectionStatus.put(CompetitionSetupSection.APPLICATION_FORM, Boolean.TRUE);
		testSectionStatus.put(CompetitionSetupSection.ASSESSORS, Boolean.FALSE);
		testSectionStatus.put(CompetitionSetupSection.CONTENT, Boolean.TRUE);

		CompetitionResource competitionResource = newCompetitionResource()
				.withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
				.withStartDate(ZonedDateTime.now().plusDays(1)).build();
		competitionResource.setSectionSetupStatus(testSectionStatus);

		assertTrue(service.isCompetitionReadyToOpen(competitionResource));
	}


	@Test
	public void testIsCompetitionReadyToOpenFailure() {
		Map<CompetitionSetupSection, Boolean> testSectionStatus = new HashMap<>();
		testSectionStatus.put(CompetitionSetupSection.INITIAL_DETAILS, Boolean.TRUE);
		testSectionStatus.put(CompetitionSetupSection.ADDITIONAL_INFO, Boolean.FALSE);
		testSectionStatus.put(CompetitionSetupSection.ELIGIBILITY, Boolean.TRUE);
		testSectionStatus.put(CompetitionSetupSection.MILESTONES, Boolean.TRUE);
		testSectionStatus.put(CompetitionSetupSection.APPLICATION_FORM, Boolean.TRUE);

		CompetitionResource competitionResource = newCompetitionResource()
				.withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
				.withStartDate(ZonedDateTime.now().plusDays(1)).build();
		competitionResource.setSectionSetupStatus(testSectionStatus);

		assertFalse(service.isCompetitionReadyToOpen(competitionResource));
	}



	@Test
	public void testSetCompetitionAsReadyToOpenWhenReadyToOpen() {
		CompetitionResource competitionResource = newCompetitionResource().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).build();
		when(competitionService.getById(any(Long.class))).thenReturn(competitionResource);
		service.setCompetitionAsReadyToOpen(2L);
		assertEquals(competitionResource.getCompetitionStatus(), CompetitionStatus.READY_TO_OPEN);

	}

	@Test
	public void testSetCompetitionAsReadyToOpenSuccess() {
		long id = 2L;
		Map<CompetitionSetupSection, Boolean> testSectionStatus = new HashMap<>();
		testSectionStatus.put(CompetitionSetupSection.INITIAL_DETAILS, Boolean.TRUE);
		testSectionStatus.put(CompetitionSetupSection.ADDITIONAL_INFO, Boolean.TRUE);
		testSectionStatus.put(CompetitionSetupSection.ELIGIBILITY, Boolean.TRUE);
		testSectionStatus.put(CompetitionSetupSection.MILESTONES, Boolean.TRUE);
		testSectionStatus.put(CompetitionSetupSection.APPLICATION_FORM, Boolean.TRUE);
		testSectionStatus.put(CompetitionSetupSection.ASSESSORS, Boolean.FALSE);
		testSectionStatus.put(CompetitionSetupSection.CONTENT, Boolean.TRUE);
		CompetitionResource competitionResource = newCompetitionResource()
				.withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
				.build();
		competitionResource.setSectionSetupStatus(testSectionStatus);

		when(competitionService.getById(any(Long.class))).thenReturn(competitionResource);
		service.setCompetitionAsReadyToOpen(id);
		verify(competitionService).markAsSetup(id);

	}


	@Test(expected=IllegalArgumentException.class)
	public void testSetCompetitionAsReadyToOpenFail() {
		Map<CompetitionSetupSection, Boolean> testSectionStatus = new HashMap<>();
		testSectionStatus.put(CompetitionSetupSection.INITIAL_DETAILS, Boolean.TRUE);
		testSectionStatus.put(CompetitionSetupSection.ADDITIONAL_INFO, Boolean.FALSE);
		testSectionStatus.put(CompetitionSetupSection.ELIGIBILITY, Boolean.TRUE);
		testSectionStatus.put(CompetitionSetupSection.MILESTONES, Boolean.TRUE);
		testSectionStatus.put(CompetitionSetupSection.APPLICATION_FORM, Boolean.TRUE);
		CompetitionResource competitionResource = newCompetitionResource()
				.withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
				.build();
		competitionResource.setSectionSetupStatus(testSectionStatus);

		when(competitionService.getById(any(Long.class))).thenReturn(competitionResource);
		service.setCompetitionAsReadyToOpen(2L);
		verify(competitionService.getById(any(Long.class)));
		verifyNoMoreInteractions(competitionResource);
	}

	@Test
	public void testPopulateModel() {
		Model model = new ExtendedModelMap();

		ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
		ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);

		CompetitionSetupSection competitionSetupSection = CompetitionSetupSection.ADDITIONAL_INFO;

		CompetitionSetupSectionModelPopulator matchingPopulator = mock(CompetitionSetupSectionModelPopulator.class);
		when(matchingPopulator.sectionToPopulateModel()).thenReturn(competitionSetupSection);

		service.setCompetitionSetupSectionModelPopulators(asList(matchingPopulator));

		CompetitionResource competition = newCompetitionResource()
				.withSetupComplete(true)
				.withStartDate(yesterday)
				.withFundersPanelDate(yesterday)
				.build();

		service.populateCompetitionSectionModelAttributes(model, competition, competitionSetupSection);

		assertEquals(true, model.asMap().get("preventEdit"));
		assertEquals(true, model.asMap().get("isSetupAndLive"));
		assertEquals(true, model.asMap().get("setupComplete"));
	}

	@Test
	public void testPopulateModel_competitionNotSetupAndLive() {
		Model model = new ExtendedModelMap();

		ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
		ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);

		CompetitionSetupSection competitionSetupSection = CompetitionSetupSection.ADDITIONAL_INFO;

		CompetitionSetupSectionModelPopulator matchingPopulator = mock(CompetitionSetupSectionModelPopulator.class);
		when(matchingPopulator.sectionToPopulateModel()).thenReturn(competitionSetupSection);

		service.setCompetitionSetupSectionModelPopulators(asList(matchingPopulator));

		CompetitionResource competition = newCompetitionResource()
				.withSetupComplete(false)
				.withFundersPanelDate(tomorrow)
				.withStartDate(yesterday)
				.build();

		service.populateCompetitionSectionModelAttributes(model, competition, competitionSetupSection);

		assertEquals(false, model.asMap().get("preventEdit"));
		assertEquals(false, model.asMap().get("isSetupAndLive"));
		assertEquals(false, model.asMap().get("setupComplete"));
	}
}
